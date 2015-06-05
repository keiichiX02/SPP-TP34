package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Multithreaded extends MyUtils {
	
	private int nbWorkers;	
	private ArrayList<Worker> listOfThreads = new ArrayList<Worker>();
	private CyclicBarrier barrier1;
	private CyclicBarrier barrier2;
	
	/**
	 * 
	 * @param n upper limit of your primes
	 * @param nbWorkers number of threads used
	 * @throws IOException
	 */
	public Multithreaded(int n, int nbWorkers) throws IOException {		
		super(n);
		this.nbWorkers = nbWorkers;
		Runnable ba1runnable = new Runnable() {
			public void run() {
				System.out.println("BarrierAction 1 executed: All threads reached barrier 1");
			}
		};
		Runnable ba2runnable = new Runnable() {
			public void run() {
				System.out.println("BarrierAction 2 executed: All threads reached barrier 2");
			}
		};
		this.barrier1 = new CyclicBarrier(this.nbWorkers+1, ba1runnable);
		this.barrier2 = new CyclicBarrier(1+this.nbWorkers, ba2runnable); //nb of workers

	}
	
	public int getNbWorkers() {
		return nbWorkers;
	}

	/**
	 * 
	 * @param arr: array of squared i: contain i², i²+i, i²+2i, etc...
	 * @return working range for each worker thread. For example:
	 * Example 1: k=1 => return = {(0, arrayOfSquaredI.size()-1)} <== this worker starts at index 0 of arrayOfSquaredI
	 * Example 2: k=2, arrayOfSquaredI has 5 elements => return = {(0, 2), (3, 4)} <== worker1 starts at index 0,
	 * worker 2 starts at index 3
	 */
	public ArrayList<Couple> dispatch(ArrayList<Integer> arr) {
		ArrayList<Couple> result = new ArrayList<Couple>();
		if(this.getNbWorkers() == 1) {
			result.add(new Couple(0, arr.size()-1));
		}else {
			int sizeOfEachRange = 
					(int) Math.round(((double) arr.size()) / this.getNbWorkers());
			assert(sizeOfEachRange > 1);
			System.out.println("sizeOfEachRange = " + sizeOfEachRange);
			int numberOfIteration = 0;
			for(int i = 0; numberOfIteration < this.getNbWorkers(); numberOfIteration++) { //number of iteration = number of ranges = number of threads
				if( (i + sizeOfEachRange - 1) >= arr.size()) { //also final couple to add
					Couple c = new Couple(i, arr.size()-1);
					result.add(c);
				}else {
					Couple c = new Couple(i, i + sizeOfEachRange - 1);
					result.add(c);
					i = i + sizeOfEachRange;
				}
			}
		}
		
		//test
		String s = "dispatch list: ";
		for(Couple c : result ) {
			s = s + c.toString() + ", ";
		}
		System.out.println(s);
		return result;
	}
	
	/**
	 * 
	 * @param n: upper bound of your primes
	 */
	public void runMultithreadedAlgorithm() {
		boolean[] booleanArrA = new boolean[this.getN()];
		for(int i=0; i<this.getN(); i++) {
			booleanArrA[i] = true;
		}
		booleanArrA[0] = false; //case 1 = FALSE
		
		//create k threads
		Worker t;
		for(int i = 0; i < this.getNbWorkers(); i++) {
			t = new Worker(this.barrier1, this.barrier2, booleanArrA);
			this.listOfThreads.add(t);
		}
		System.out.println(Thread.currentThread().getName() +
                " created " + this.getNbWorkers() + " worker threads");
		//fin create k threads
		
		
		double sqrt_n = Math.ceil(Math.sqrt(this.getN())); //square root then round up
		for(int i=2; i<sqrt_n; i++) {
			
			System.out.println(Thread.currentThread().getName() +
	                " launched " + this.getNbWorkers() + " worker threads");
			if(booleanArrA[i]) {
				for(Worker w : this.listOfThreads) {
					w.start();
				}
				
				System.out.println("      current i = " + i); //test
				System.out.println("      current tableOfBool: " 
						+ MyUtils.boolArrayToString(booleanArrA));//test

				//distribute work among the k worker threads
				ArrayList<Integer> jArr = new ArrayList<Integer>();
				int j = (int) Math.pow(i, 2);
				while(j <= this.getN()) {
					jArr.add(j);
					j = j + i;
				}
				System.out.println("      current jArr: " 
						+ MyUtils.arrayListIntegerToString(jArr));				
				ArrayList<Couple> rangeArr = dispatch(jArr);
				Iterator<Couple> iterator = rangeArr.iterator();
				for(Worker w : this.listOfThreads) {
					Couple c = iterator.next();
					w.setStartNb(c.getFirst());
					w.setEndNb(c.getSecond());
					w.setjArr(jArr);
				}
				System.out.println(Thread.currentThread().getName() +
                        " distributed work among " + this.getNbWorkers() + " threads");
				//fin dist.
				
				try {
					System.out.println(Thread.currentThread().getName() +
	                        " waiting at barrier 1");
					barrier1.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
				//all worker threads do their tasks...
				
				try {
					System.out.println(Thread.currentThread().getName() +
                            " waiting at barrier 2: "
                            + "wait for all worker threads to complete their iteration");
					barrier2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
			} //fin if(booleanArrA[i]) {
			
			//MAJ des threads avec le nouveau contenu de booleanArrA
			for(int ii = 0; ii < this.getNbWorkers(); ii++) {
				t = new Worker(this.barrier1, this.barrier2, booleanArrA);
				this.listOfThreads = new ArrayList<Worker>();
				this.listOfThreads.add(t);
			}
			
		}//fin loop for with every i
		
		for(Worker w : this.listOfThreads) {
			try {
				w.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() +
                " interrupted " + this.getNbWorkers() + " worker threads");
		
		//update result to be printed:
		for(int i=2; i<booleanArrA.length; i++) {
			if(booleanArrA[i]) {
				primesArr.add(i);
			}
		}
	}
	
	
	public class Worker extends Thread {
		private final CyclicBarrier barrier1;
		private final CyclicBarrier barrier2;
		private boolean[] iArr;
		private int startNb;
		private int endNb;
		private ArrayList<Integer> jArr;
		
		
		public Worker(CyclicBarrier barrier1, CyclicBarrier barrier2,
				boolean[] rawNumbers) {
			super();
			this.barrier1 = barrier1;
			this.barrier2 = barrier2;
			this.iArr = rawNumbers;
			this.startNb = -1;
			this.endNb = -1;
			this.jArr = new ArrayList<Integer>();
		}
		
		public boolean[] getRawNumbers() {
			return iArr;
		}

		public void setRawNumbers(boolean[] rawNumbers) {
			this.iArr = rawNumbers;
		}

		public int getStartNb() {
			return startNb;
		}

		public void setStartNb(int startNb) {
			this.startNb = startNb;
		}

		public int getEndNb() {
			return endNb;
		}

		public void setEndNb(int endNb) {
			this.endNb = endNb;
		}

		public ArrayList<Integer> getjArr() {
			return jArr;
		}

		public void setjArr(ArrayList<Integer> jArr) {
			this.jArr = jArr;
		}

		public void run() {
			try {
				while(!Thread.currentThread().isInterrupted()) {
					try {
						System.out.println(Thread.currentThread().getName() +
								" waiting at barrier 1: wait for the "
								+ "main thread to initialize the work to be done");
						this.barrier1.await();


						for(int j = 0; j < this.jArr.size(); j++) {
							this.iArr[this.getjArr().get(j)] = false;
							System.out.println("      current tableOfBool: " 
									+ MyUtils.boolArrayToString(iArr));
						}
						System.out.println(Thread.currentThread().getName() +
								" finished the work");

						System.out.println(Thread.currentThread().getName() +
								" waiting at barrier 2");
						this.barrier2.await();
					} catch (BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName() + " terminated");
			}
		}

	}//fin class Worker
	
	public class Couple {
		
		private int first;
		private int second;
		
		public Couple(int first, int second) {
			super();
			this.first = first;
			this.second = second;
		}

		public int getFirst() {
			return first;
		}

		public int getSecond() {
			return second;
		}
		
		public String toString() {
			return "(" + first + ", " + second + ")";
		}
		
		
	}

}
