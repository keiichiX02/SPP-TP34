package main;

import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.*;


public class EratoSieveJUnitTest {

		
	@Test
	public void testMonothreadedEratoSieveWith10() throws InstantiationException, IllegalAccessException, IOException {
		//EratoSieve es = createEratoSieve();
		EratoSieve es = new EratoSieve();
		es.runMonothreadedAlgorithm(10);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		//System.out.println(es.foundPrimesToString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith100() throws InstantiationException, IllegalAccessException, IOException {
		//EratoSieve es = createEratoSieve();
		EratoSieve es = new EratoSieve();
		es.runMonothreadedAlgorithm(100);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		//System.out.println(es.foundPrimesToString());
		assertTrue(result);
	}
	
	@Test
	public void testMonothreadedEratoSieveWith1000() throws InstantiationException, IllegalAccessException, IOException {
		//EratoSieve es = createEratoSieve();
		EratoSieve es = new EratoSieve();
		es.runMonothreadedAlgorithm(1000);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		//System.out.println(es.foundPrimesToString());
		assertTrue(result);
	}
	
	@Test
	public void testMultithreadedN10K1() throws IOException {
		EratoSieve es = new EratoSieve(1);
		es.runMultithreadedAlgorithm(10, 1);
		boolean result = es.checkMasterStringForSubstring(es.toString());
		//System.out.println(es.foundPrimesToString());
		assertTrue(result);
	}
	
	
}
