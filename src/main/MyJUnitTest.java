package main;

import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.*;


public class MyJUnitTest {

		
	@Test
	public void testMonothreadedEratoSieveWith10() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(10);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith100() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(100);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith1000() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded(1000);
		es.runAlgorithm();
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN5K1() throws IOException {
		Multithreaded es = new Multithreaded(5, 1);
		es.runMultithreadedAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN7K2() throws IOException {
		Multithreaded es = new Multithreaded(7, 2);
		es.runMultithreadedAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN17K4() throws IOException {
		Multithreaded es = new Multithreaded(17, 4);
		es.runMultithreadedAlgorithm();
		System.out.println("Resultat :\n\n\n" + es.toString());
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	
}
