package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Multithreaded extends MyUtils {
	
	private int nbWorkers;	
	private ArrayList<Worker> listOfThreads = new ArrayList<Worker>();
	private CyclicBarrier barrier1; // = new CyclicBarrier(1); 
	private CyclicBarrier barrier2; // = new CyclicBarrier(1);
	
	public Multithreaded(int parties) throws IOException {		
		super();
		this.nbWorkers = parties;
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
	
	/**
	 * example: 3 threads, n = 11 produces 0 5 10 11
	 * example 2: 3 threads, n = 12, produces 0 6 12 12
	 * example 3: 3 threads, n = 13, produces 0 6 12 13
	 * @param k: number of worker threads
	 * @param n: upper bound of primes
	 */
	public ArrayList<Integer> dispatchRangeForWorkerThreads(int k, int n) {
		assert(k > 0);
		if(k == 1) {
			ArrayList<Integer> ranges = new ArrayList<Integer>();
			ranges.add(0);
			ranges.add(n);
			
			return ranges;
			
		}else {
			ArrayList<Integer> ranges = new ArrayList<Integer>();
			//int inum = (int)(long) Math.floorDiv(n, k-1); //non compatible avec Java 7
			int inum = (int) Math.round(((double) n) / (k-1));
			//comportement attendu: 2 threads => 2 ranges (y inclu dernier rnage)
			for(int i = 0; (i * inum) <= n; i++) {
				ranges.add(i * inum);
			}
			ranges.add(n);
			
			return ranges;
		}//fin else
	}
	
	/**
	 * 
	 * @param n: upper bound of your primes
	 * @param k: number of threads
	 */
	public void runMultithreadedAlgorithm(int n, int k) {
		boolean[] tableOfBool = new boolean[n];
		for(int i=0; i<n; i++) {
			tableOfBool[i] = true;
		}
		
		//create k threads and launch them
		Worker t;
		for(int i = 0; i < k; i++) {
			t = new Worker(this.barrier1, this.barrier2, tableOfBool, -1, -1, -1);
			this.listOfThreads.add(t);
			t.start();
		}
		System.out.println(Thread.currentThread().getName() +
                " finished creating k worker threads and launch them");
		//fin create k threads and launch them
		
		
		double rc_of_n = Math.ceil(Math.sqrt(n)); //square root then round up
		for(int i=2; i<rc_of_n; i++) {
//			for(Worker w : this.listOfThreads) {
//				w.start();
//			}
//			System.out.println(Thread.currentThread().getName() +
//	                " finished launching k worker threads");
			if(tableOfBool[i]) {
				System.out.println("      current i = " + i); //test
				System.out.println("      current tableOfBool: " 
						+ MyUtils.boolArrayToString(tableOfBool));//test

				//distribute work among the k worker threads
				ArrayList<Integer> rangeArr = dispatchRangeForWorkerThreads(k, (int)rc_of_n);
				System.out.println("      current rangeArr: " + 
						MyUtils.arrayListIntegerToString(rangeArr)); //test
				int indice_range = 0;
				for(Worker thr : this.listOfThreads) {
					thr.setStartNb(rangeArr.get(indice_range));
					thr.setEndNb(rangeArr.get(indice_range + 1));
					thr.setIndice_i(i);
					indice_range++;
				}
				System.out.println(Thread.currentThread().getName() +
                        " finished distributing work among k threads");
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
                            " waiting at barrier 2");
					barrier2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
			}
			
			for(Worker w : this.listOfThreads) {
				try {
					Thread.sleep(3000);
					w.interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() +
	                " finished stopping (interrupt) k worker threads");
			
			for(int ii = 0; ii < k; ii++) {
				t = new Worker(this.barrier1, this.barrier2, tableOfBool, -1, -1, -1);
				this.listOfThreads = new ArrayList<Worker>();
				this.listOfThreads.add(t);
				t.start();
			}
			
		}//fin loop for with i
		
		
		for(int i=2; i<tableOfBool.length; i++) {
			if(tableOfBool[i]) {
				//result = result + i + "\n";
				primesArr.add(i);
			}
		}
	}
	
	
	
	/**
	 * return True if mother has son as a substring, False otherwise
	 * @param son
	 */
	public boolean checkMasterStringForSubstring(String son) {
		int lastIndex = primesTo100k.indexOf(son, 0);
		//assertTrue("\ntest passed", (lastIndex != -1));
		if(lastIndex != -1) {
			return true;
		}else {
			return false;
		}
	}
	
	public static void main(String[] args) throws IOException {
		/*
		//Test dispatchRange:
		int n = 12; 
		int k = 3;
		System.out.println("max primes n = " + n + "; nb of threads = " + k);
		ArrayList<Integer> arr = es.produceRangesForKWorkers(k, n);
		for(int i : arr) {
			System.out.print(i + " ");
		}
		*/
		Multithreaded program = new Multithreaded(1);
		program.runMultithreadedAlgorithm(10, 1);
		program.checkMasterStringForSubstring(program.toString());
	}
	
	public class Worker extends Thread {
		private final CyclicBarrier barrier1;
		private final CyclicBarrier barrier2;
		private boolean[] rawNumbers;
		private int startNb;
		private int endNb;
		private int indice_i; //remember j = i^2
		
		public Worker(CyclicBarrier cb, CyclicBarrier cb2, boolean[] rn, int s, int e, int i) {
			this.barrier1 = cb;
			this.barrier2 = cb2;
			this.rawNumbers = rn;
			this.startNb = s; 
			this.endNb = e;
			this.indice_i = i;
		}
		
		public int getIndice_i() {
			return indice_i;
		}

		public void setIndice_i(int indice_i) {
			this.indice_i = indice_i;
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

		public boolean[] getRawNumbers() {
			return rawNumbers;
		}

		public void setRawNumbers(boolean[] rawNumbers) {
			this.rawNumbers = rawNumbers;
		}

		public void run() {
			try {
				try {
					System.out.println(Thread.currentThread().getName() +
	                        " waiting at barrier 1");
					this.barrier1.await();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
				for(int j = (int)Math.pow(this.getIndice_i(), 2); 
						(this.getStartNb() <= j) && (j <= this.getEndNb()); j = j + this.indice_i) {
					this.rawNumbers[j] = false;
				}
				System.out.println(Thread.currentThread().getName() +
	                    " finished the work");
				
				
				try {
					System.out.println(Thread.currentThread().getName() +
	                        " waiting at barrier 2");
					this.barrier2.await();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				System.out.println("interrupted ???");
				e.printStackTrace();
			}
			
		}
		
		
	}//fin class Worker

}
