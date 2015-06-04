package main;

import java.io.IOException;


public class Monothreaded extends MyUtils {

	
	public Monothreaded() {
		super();
	}

	/**
	 * 
	 * @param n: the upper bound of the numbers
	 */
	public void runAlgorithm(int n) {
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
					}//prevent debordement
				}
			}
		}
		
		for(int i=2; i<tableOfBool.length; i++) {
			if(tableOfBool[i]) {
				primesArr.add(i);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Monothreaded program = new Monothreaded();
		program.runAlgorithm(10);
		program.checkMasterStringForSubstring(program.toString());
	}
	
}
