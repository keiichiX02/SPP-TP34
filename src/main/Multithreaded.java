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
	boolean[] arrA = new boolean[this.getN() + 1];
	//ex. n=5 => arrA.size()=6 avec index rangé comme 0, 1, 2, 3, 4, 5
	PauseControl myPauseControl;
	
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
				//System.out.println("All threads reached barrier 1");
			}
		};
		Runnable ba2runnable = new Runnable() {
			public void run() {
				//System.out.println("All threads reached barrier 2");
			}
		};
		this.barrier1 = new CyclicBarrier(this.nbWorkers+1, ba1runnable);
		this.barrier2 = new CyclicBarrier(1+this.nbWorkers, ba2runnable);
		
		this.myPauseControl = new PauseControl();

	}
	
	public int getNbWorkers() {
		return nbWorkers;
	}
	
	/**
	 * exemple: 7/3=3; 8/3=3; 9/3=3; 17/4=5;
	 * @param a diviseur
	 * @param b divisé
	 * @return
	 */
	public static int roundUpDoubleToIntClosestToPositiveInfinity(double a, int b) {
		double mod = a % b;
		if(mod == 0) {
			return (int) (a / b);
		}else {
			int quot = ((int) (a-mod))  / b;
			return quot + 1;
		}	
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
					Multithreaded.roundUpDoubleToIntClosestToPositiveInfinity((double) arr.size(), this.getNbWorkers());
			//System.out.println("range size = " + sizeOfEachRange);
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
	public void runAlgorithm() {
		for(int i=0; i<arrA.length; i++) {
			arrA[i] = true;
		}
		arrA[0] = false; //index 0: 0 isn't prime
		arrA[1] = false; //index 1: 1 isn't prime
		
		//create k threads
		Worker t;
		for(int i = 0; i < this.getNbWorkers(); i++) {
			t = new Worker(this, this.barrier1, this.barrier2);
			//t = new Worker(this.barrier1, this.barrier2, arrA);
			this.listOfThreads.add(t);
		}
		//fin create k threads
		
		for(Worker w : this.listOfThreads) {
			w.start();
			//System.out.println("  " + w.getName() + " launched");
		}
		
		double sqrt_n = Math.ceil(Math.sqrt(this.getN())); //square root then round up
		//System.out.println("sqrt n = " + sqrt_n);
		for(int i=2; i<sqrt_n; i++) {

			for(int nbOfWorkerWaiting = 0; nbOfWorkerWaiting<this.nbWorkers; nbOfWorkerWaiting++) {
				this.myPauseControl.unpause();
			}
			
			if(arrA[i]) {
				
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
				
				
				try {
					barrier2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
				
			} //fin if(booleanArrA[i]) {

			//System.out.println("i = " + i + " passed");
			
		}//fin loop for with every i
		
		//terminate all worker threads:
		for(Worker w : this.listOfThreads) {
			try {
				//System.out.println(w.getName() + " is terminated by main thread");
				w.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//update result to be printed:
		for(int i=0; i<arrA.length; i++) {
			if(arrA[i]) {
				primesArr.add(i);
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

//						System.out.println(Thread.currentThread().getName() 
//								+ " takes care of start=" + this.startIndexOfjArr +
//								"    end=" + this.endIndexOfjArr);
						int j=this.startIndexOfjArr;
						while(j<=this.endIndexOfjArr) {
							int index = this.getjArr().get(j);
							//System.out.println("  j=" + index);
							//myLock.lock();
							this.mainThread.arrA[index] = false;
							//myLock.unlock();
							j++;
							
						}
//						System.out.println(
//								"  used jArr = " + MyUtils.arrayListIntegerToString(this.jArr)
//								+ " to obtain arrA = "
//								+ MyUtils.boolArrayToString(this.mainThread.arrA));
						
						this.barrier2.await();
						
						
						
					} catch (BrokenBarrierException e) {
						//e.printStackTrace();
					}
				}
				
				//pause this worker thread, to be notified when it has new task
//				System.out.println("  " + Thread.currentThread().getName()
//						+ " paused");
				this.mainThread.myPauseControl.pause();
				this.mainThread.myPauseControl.pausePoint();

			} catch (InterruptedException e) {
				//System.err.println(Thread.currentThread().getName() + " terminated");
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
