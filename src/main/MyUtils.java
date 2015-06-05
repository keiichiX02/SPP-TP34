package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyUtils {
	
	static String pathToFile = 
			"C:\\Users\\Fulhelmknight\\Desktop\\SPP-TP56ver19May"
					+ "\\SPP-TP56v19May\\primes-to-100k.txt";
	private int n;
	protected ArrayList<Integer> primesArr;
	protected String primesTo100k;
	

	/**
	 * 
	 * @param n upper limit of the prime numbers
	 */
	public MyUtils(int n) {
		try {
			this.n = n;
			this.primesArr = new ArrayList<Integer>();
			this.primesTo100k = MyUtils.readFile(MyUtils.pathToFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public int getN() {
		return n;
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
	
	public static String arrayListIntegerToString(ArrayList<Integer> arr) {
		String resul = "";
		for(int element : arr) {
			resul = resul + " " + element;
		}
		return resul;
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
}
