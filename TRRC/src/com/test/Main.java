package com.test;

import java.util.ArrayList;

import com.filefunctions.GraphExtractor;
import com.protocol.colorbased.ColorBasedSimSystem;
import com.protocol.directbased.DirectBasedSimSystem;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// new Tester();
		// System.exit(0);
		
		
		ArrayList< ArrayList<Integer> > rtGraph; 
		rtGraph = GraphExtractor.readFile("graphs/rt_sample_7.dat");
		
		ArrayList< ArrayList<Integer> > rrGraph; 
		rrGraph = GraphExtractor.readFile("graphs/rr_sample_7.dat");
		
		// DirectBasedSimSystem rs = new DirectBasedSimSystem(
		//		rrGraph, rtGraph, "RANDOM");
		
		ColorBasedSimSystem rs = new ColorBasedSimSystem(rrGraph, rtGraph, "GDE");
		rs.run();
		
		
	}

}
