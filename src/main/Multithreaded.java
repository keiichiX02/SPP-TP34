package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;


public class Multithreaded extends MyUtils {
	
	private int nbWorkers;	
	private ArrayList<Worker> listOfThreads = new ArrayList<Worker>();
	private CyclicBarrier barrier1;
	private CyclicBarrier barrier2;
	boolean[] arrA = new boolean[this.getN()];
	static ReentrantLock myLock = new ReentrantLock();
	
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
				//System.out.println("BarrierAction 1 executed: All threads reached barrier 1");
			}
		};
		Runnable ba2runnable = new Runnable() {
			public void run() {
				//System.out.println("BarrierAction 2 executed: All threads reached barrier 2");
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
	 * Example 1: k=1 => return = {(0, arr.size()-1)} <== this worker starts at index 0 of arr
	 * Example 2: k=2, arr has 5 elements => return = {(0, 2), (3, 4)} <== worker1 starts at index 0 of arr,
	 * worker 2 starts at index 3 of arr
	 */
	public ArrayList<Couple> dispatch(ArrayList<Integer> arr) {
		ArrayList<Couple> result = new ArrayList<Couple>();
		if(this.getNbWorkers() == 1) {
			result.add(new Couple(0, arr.size()-1));
		}else {
			int sizeOfEachRange = 
					(int) Math.round(((double) arr.size()) / this.getNbWorkers());
			assert(sizeOfEachRange > 1);
			int numberOfIteration = 0;
			for(int i = 0; numberOfIteration < this.getNbWorkers(); numberOfIteration++) {
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
		
		return result;
	}
	
	/**
	 * 
	 * @param n: upper bound of your primes
	 */
	public void runMultithreadedAlgorithm() {
		for(int i=0; i<this.getN(); i++) {
			arrA[i] = true;
		}
		arrA[0] = false; //case 1 = FALSE
		
		//create k threads
		Worker t;
		for(int i = 0; i < this.getNbWorkers(); i++) {
			t = new Worker(this, this.barrier1, this.barrier2);
			//t = new Worker(this.barrier1, this.barrier2, arrA);
			this.listOfThreads.add(t);
		}
		//fin create k threads
		
		
		double sqrt_n = Math.ceil(Math.sqrt(this.getN())); //square root then round up
		for(int i=2; i<sqrt_n; i++) {
			
			if(arrA[i]) {
				System.out.println("i = " + i);
				for(Worker w : this.listOfThreads) {
					w.start();
				}
				

				//distribute work among the k worker threads
				ArrayList<Integer> jArr = new ArrayList<Integer>();
				int j = (int) Math.pow(i, 2);
				while(j <= this.getN()) {
					jArr.add(j);
					j = j + i;
				}
				
				ArrayList<Couple> rangeArr = dispatch(jArr);
				Iterator<Couple> iterator = rangeArr.iterator();
				for(Worker w : this.listOfThreads) {
					Couple c = iterator.next();
					w.setStartNb(c.getFirst());
					w.setEndNb(c.getSecond());
					w.setjArr(jArr);
				}
				//fin dist.
				
				try {
					barrier1.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
				//all worker threads do their tasks...
				
				try {
					barrier2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
			} //fin if(booleanArrA[i]) {
			
			System.out.println("i (fin de if) = " + i);
			
			//MAJ des threads avec le nouveau contenu de booleanArrA
			for(int ii = 0; ii < this.getNbWorkers(); ii++) {
				t = new Worker(this, this.barrier1, this.barrier2);
				//t = new Worker(this.barrier1, this.barrier2, arrA);
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
		
		//update result to be printed:
		for(int i=0; i<arrA.length; i++) {
			if(arrA[i]) {
				primesArr.add(i+1);
			}
		}
	}
	
	
	public class Worker extends Thread {
		private Multithreaded mainThread;
		private final CyclicBarrier barrier1;
		private final CyclicBarrier barrier2;
		private int startIndexOfjArr;
		private int endIndexOfjArr;
		private ArrayList<Integer> jArr;
		
		
		public Worker(Multithreaded mainThread, CyclicBarrier barrier1, CyclicBarrier barrier2) {
			super();
			this.mainThread = mainThread;
			this.barrier1 = barrier1;
			this.barrier2 = barrier2;
			this.startIndexOfjArr = -1;
			this.endIndexOfjArr = -1;
			this.jArr = new ArrayList<Integer>();
		}

		public int getStartNb() {
			return startIndexOfjArr;
		}

		public void setStartNb(int startNb) {
			this.startIndexOfjArr = startNb;
		}

		public int getEndNb() {
			return endIndexOfjArr;
		}

		public void setEndNb(int endNb) {
			this.endIndexOfjArr = endNb;
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
						this.barrier1.await();

						//for(int j = 0; j < this.jArr.size(); j++) {
						System.out.println(Thread.currentThread().getName() +
								"  current jArr: " 
								+ MyUtils.arrayListIntegerToString(this.jArr) +
								"\n    start=" + this.startIndexOfjArr +
								"    end=" + this.endIndexOfjArr);
						int j=this.startIndexOfjArr;
						while(j<=this.endIndexOfjArr) {
							int index = this.getjArr().get(j);
							System.out.println("  index=" + index);
							//myLock.lock();
							this.mainThread.arrA[index-1] = false;
							//this.iArr[index] = false;
							//myLock.unlock();
							j++;
							//j = j + this.incrementationRange;
							System.out.println("this.mainThread.arrA: " 
									+ MyUtils.boolArrayToString(this.mainThread.arrA));
						}

						this.barrier2.await();
					} catch (BrokenBarrierException e) {
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
