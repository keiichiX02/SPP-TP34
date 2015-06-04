package main;

import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.*;


public class MyJUnitTest {

		
	@Test
	public void testMonothreadedEratoSieveWith10() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded();
		es.runAlgorithm(10);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith100() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded();
		es.runAlgorithm(100);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith1000() throws InstantiationException, IllegalAccessException, IOException {
		Monothreaded es = new Monothreaded();
		es.runAlgorithm(1000);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN10K1() throws IOException {
		Multithreaded es = new Multithreaded(1);
		es.runMultithreadedAlgorithm(10, 1);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		assertTrue(result);
	}
	
	
}
