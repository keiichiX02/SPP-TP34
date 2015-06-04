package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class EratoSieve {

	static String pathToFile = 
			"C:\\Users\\Fulhelmknight\\Desktop\\SPP-TP56ver19May"
					+ "\\SPP-TP56v19May\\primes-to-100k.txt";

	private ArrayList<Integer> primesArr;
	private String primesTo100k;
	private ArrayList<Worker> listOfThreads = new ArrayList<Worker>();
	private CyclicBarrier barrier1 = new CyclicBarrier(1); 
	private CyclicBarrier barrier2 = new CyclicBarrier(1);
	private int nbWorkers;
	
	public EratoSieve() throws IOException {
		primesArr = new ArrayList<Integer>();
		primesTo100k = EratoSieve.readFile(EratoSieve.pathToFile);
	}
	
	/**
	 * pour tester le cas multithread
	 * @param parties nombre de workers
	 * @throws IOException
	 */
	public EratoSieve(int parties) throws IOException {
		primesArr = new ArrayList<Integer>();
		primesTo100k = EratoSieve.readFile(EratoSieve.pathToFile);
		this.nbWorkers = parties;
		Runnable ba1runnable = new Runnable() {
			public void run() {
				System.out.println("BarrierAction 1 executed ");
			}
		};
		this.barrier1 = new CyclicBarrier(this.nbWorkers+1, ba1runnable); //?
		Runnable ba2runnable = new Runnable() {
			public void run() {
				System.out.println("BarrierAction 2 executed ");
			}
		};
		this.barrier2 = new CyclicBarrier(1+this.nbWorkers, ba2runnable); //nb of workers

	}

	/**
	 * 
	 * @param n: the upper bound of the numbers
	 */
	public void runMonothreadedAlgorithm(int n) {
		boolean[] tableOfBool = new boolean[n];
		for(int i=0; i<n; i++) {
			tableOfBool[i] = true;
		}

		double rc_of_n = Math.ceil(Math.sqrt(n)); //square root then round up		
		for(int i=2; i<rc_of_n; i++) {
			if(tableOfBool[i]) {
				int sq_of_i = (int)Math.pow(i, 2);
				for(int j = sq_of_i; j < Math.pow(rc_of_n, 2); j = j + i) {
					if(j < tableOfBool.length) {
						tableOfBool[j] = false; 
					}//preventing debordement
				}
			}
		}
		
		for(int i=2; i<tableOfBool.length; i++) {
			if(tableOfBool[i]) {
				primesArr.add(i);
			}
		}
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
	
	public static String boolArrayToString(boolean[] arr) {
		String result = "";
		for(int i=0; i<arr.length; i++) {
			if(arr[i]) {
				result = result + " T";
			}else {
				result = result + " F";
			}
			
		}
		return result;
	}
	
	public static String arrayListIntegerToString(ArrayList<Integer> arr) {
		String resul = "";
		for(int element : arr) {
			resul = resul + " " + element;
		}
		return resul;
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
                " finished creating k worker threads");
		//fin create k threads and launch them
		
		
		double rc_of_n = Math.ceil(Math.sqrt(n)); //square root then round up
		for(int i=2; i<rc_of_n; i++) {
			
//			for(Worker w : this.listOfThreads) {
//				w.start();
//			}
			System.out.println(Thread.currentThread().getName() +
	                " finished launching k worker threads");
			
			if(tableOfBool[i]) {
				
				System.out.println("i = " + i);
				System.out.println(EratoSieve.boolArrayToString(tableOfBool));//test
				
				//distribute work among the k worker threads
				ArrayList<Integer> rangeArr = dispatchRangeForWorkerThreads(k, (int)rc_of_n);
				System.out.println(EratoSieve.arrayListIntegerToString(rangeArr)); //test
				int indice_range = 0;
				for(Worker thr : this.listOfThreads) {
					thr.setStartNb(rangeArr.get(indice_range));
					thr.setEndNb(rangeArr.get(indice_range + 1));
					thr.setIndice_i(i);
					indice_range++;
				}
				System.out.println(Thread.currentThread().getName() +
                        " finished distributing work among k threads (Main)");
				//fin dist
				
				try {
					System.out.println(Thread.currentThread().getName() +
	                        " waiting at barrier 1 (Main thread)");
					barrier1.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("Main Thread interrupted!");
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					System.out.println("Main Thread interrupted!");
					e.printStackTrace();
				}
				
				//all worker threads do their iteration...
				
				try {
					System.out.println(Thread.currentThread().getName() +
                            " waiting at barrier 2 (Main thread)");
					barrier2.await(); //main thread comes here and got stuck!
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
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
	 * output the found primes to string
	 * @return
	 */
	public String toString() {
		String result = "";
		for(int primeNumber : primesArr) {
			result = result + primeNumber + "\n";
		}
		return result;
	}
	
	public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName)); 
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    }finally {
	        br.close();
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
		/*EratoSieve es = new EratoSieve();
		es.runMonothreadedAlgorithm(10);
		String s = es.foundPrimesToString();
		boolean result = es.checkMasterStringForSubstring(s);
		System.out.println(s + " result = " + result + "\n");
		*/
		/*
		int n = 12; 
		int k = 3;
		System.out.println("max primes n = " + n + "; nb of threads = " + k);
		ArrayList<Integer> arr = es.produceRangesForKWorkers(k, n);
		for(int i : arr) {
			System.out.print(i + " ");
		}
		*/
		EratoSieve esMulti = new EratoSieve(1);
		esMulti.runMultithreadedAlgorithm(10, 1);
		esMulti.checkMasterStringForSubstring(esMulti.toString());
	}
	
	
	public class Worker extends Thread {
		private final CyclicBarrier cyclicBarrier;
		private final CyclicBarrier cyclicBarrier2;
		private boolean[] rawNumbers;
		private int startNb;
		private int endNb;
		private int indice_i; //remember j = i^2
		
		public Worker(CyclicBarrier cb, CyclicBarrier cb2, boolean[] rn, int s, int e, int i) {
			this.cyclicBarrier = cb;
			this.cyclicBarrier2 = cb2;
			this.rawNumbers = rn;
			this.startNb = s; this.endNb = e;
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
					this.cyclicBarrier.await();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				for(int j = (int)Math.pow(this.getIndice_i(), 2); 
						(this.getStartNb() <= j) && (j <= this.getEndNb()); j = j + this.indice_i) {
					this.rawNumbers[j] = false;
				}
				System.out.println(Thread.currentThread().getName() +
	                    " finished the iteration work (Worker)");
				
				
				try {
					System.out.println(Thread.currentThread().getName() +
	                        " waiting at barrier 2");
					this.cyclicBarrier2.await();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				System.out.println("interrupted ???");
				e.printStackTrace();
			}
			
		}
		
		
	}//fin class Worker

}
