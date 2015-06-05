package main;

import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.*;


public class MyJUnitTest {

		
	@Test
	public void testMonothreadedN10() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(10);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedN100() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(100);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedN1000() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(1000);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN5K1() throws IOException {
		Multithreaded es = new Multithreaded(5, 1);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN7K1() throws IOException {
		Multithreaded es = new Multithreaded(7, 1);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN10K1() throws IOException {
		Multithreaded es = new Multithreaded(10, 1);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN10K2() throws IOException {
		Multithreaded es = new Multithreaded(10, 2);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN17K3() throws IOException {
		Multithreaded es = new Multithreaded(17, 3);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN100K5() throws IOException {
		Multithreaded es = new Multithreaded(100, 5);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN1000K1() throws IOException {
		Multithreaded es = new Multithreaded(1000, 1);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN1000K2() throws IOException {
		Multithreaded es = new Multithreaded(1000, 2);
		es.runAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	
}
