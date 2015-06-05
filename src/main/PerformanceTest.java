package main;

import java.io.IOException;

public class PerformanceTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MyUtils program;

		long startTime = System.nanoTime();
		program = new Monothreaded(4000000);
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
	}

}
