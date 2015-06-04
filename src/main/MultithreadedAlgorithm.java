package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import main.EratoSieve.Worker;

public class MultithreadedAlgorithm {
	
	private ArrayList<Integer> primesArr;
	private String primesTo100k;
	private ArrayList<Worker> listOfThreads = new ArrayList<Worker>();
	private CyclicBarrier barrier1 = new CyclicBarrier(1); 
	private CyclicBarrier barrier2 = new CyclicBarrier(1);
	private int nbWorkers;
	
	public MultithreadedAlgorithm(int parties) throws IOException {
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

}
