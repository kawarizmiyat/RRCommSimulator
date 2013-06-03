package com.test;

import java.util.ArrayList;

import com.filefunctions.GraphExtractor;
import com.protocol.rrecom.DirectBasedSimSystem;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// new Tester();
		// System.exit(0);
		
		
		ArrayList< ArrayList<Integer> > rtGraph; 
		rtGraph = GraphExtractor.readFile("graphs/rt_sample_2.dat");
		
		ArrayList< ArrayList<Integer> > rrGraph; 
		rrGraph = GraphExtractor.readFile("graphs/rr_sample_2.dat");
		
		DirectBasedSimSystem rs = new DirectBasedSimSystem(
				rrGraph, rtGraph, "RANDOM");
		rs.run();
		
		
	}

}
