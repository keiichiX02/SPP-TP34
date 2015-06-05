package main;


public class Monothreaded extends MyUtils {

	/**
	 * 
	 * @param n limit of your prime
	 */
	public Monothreaded(int n) {
		super(n);
	}

	/**
	 * 
	 */
	public void runAlgorithm() {
		boolean[] arrA = new boolean[this.getN()];
		for(int i=0; i<this.getN(); i++) {
			arrA[i] = true;
		}

		double rc_of_n = Math.ceil(Math.sqrt(this.getN())); //square root then round up		
		for(int i=2; i<rc_of_n; i++) {
			if(arrA[i]) {
				int sq_of_i = (int)Math.pow(i, 2);
				for(int j = sq_of_i; j < Math.pow(rc_of_n, 2); j = j + i) {
					if(j < arrA.length) {
						arrA[j] = false; 
					}//prevent debordement
				}
			}
		}
		
		for(int i=2; i<arrA.length; i++) {
			if(arrA[i]) {
				primesArr.add(i);
			}
		}
	}
	
}
