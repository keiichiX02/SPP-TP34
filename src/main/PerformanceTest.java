package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PerformanceTest {

	/**
	 * 
	 * @param arr: the list that contains the result
	 * @param fileName: output ==> output.txt dans project workspace
	 */
	public static void printToFile(ArrayList<Long> arr, String fileName) {
		FileWriter writer;
		try {
			writer = new FileWriter(fileName + ".txt");

			for(Long str: arr) {
				writer.write(str+"\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ArrayList<Integer> primeBelow = new ArrayList<Integer>();
		primeBelow.add(500000);
		primeBelow.add(1000000);
		primeBelow.add(2000000);
		primeBelow.add(4000000);
		
		/*
		//test monothreaded
		int numberOfSampleTest = 5;
		for(int j=0; j<numberOfSampleTest; j++) {
			
			ArrayList<Long> sample = new ArrayList<Long>();
			for(int i=0; i<primeBelow.size(); i++){
				int limit = primeBelow.get(i);
				long startTime = System.nanoTime();
				Monothreaded program = new Monothreaded(limit);
				program.runAlgorithm();
				long stopTime = System.nanoTime();
				long elapsedTime = stopTime - startTime;
				sample.add(elapsedTime);
			}
			PerformanceTest.printToFile(sample, "mono_" + j);
		}*/
		
		
		
		
		//test multithreaded
		int[] nbT = {1, 5, 10};
		for(int j=0; j<nbT.length; j++) {
			ArrayList<Long> sampleMulti = new ArrayList<Long>();
			for(int i=0; i<primeBelow.size(); i++){
				int limit = primeBelow.get(i);
				long startTime = System.nanoTime();
				Multithreaded programMulti = new Multithreaded(limit, nbT[j]);
				programMulti.runAlgorithm();
				long stopTime = System.nanoTime();
				long elapsedTime = stopTime - startTime;
				sampleMulti.add(elapsedTime);
			}
			PerformanceTest.printToFile(sampleMulti, "multi_" + nbT[j] + "_threads");
			
		}
		/*
		 * singular test
		 */ /*
		long startTime = System.nanoTime();
		Multithreaded programMulti = new Multithreaded(1000, 2);
		programMulti.runAlgorithm();
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
		*/
		
	}

}
