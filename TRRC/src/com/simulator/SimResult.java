package com.simulator;

import java.util.ArrayList;

public class SimResult {

	public int numReaders, nonRedundant; 
	public int rounds; 
	public ArrayList<Integer> readersPerRound; 
	public ArrayList<Integer> nonRedundantReaders; 
	public int numOverWrites, numReads;
	public int numTags;
	public int numOwnedTags; 
	
	public SimResult() { 
		readersPerRound = new ArrayList<Integer>();
		numOwnedTags = 0;
	}

	@Override
	public String toString() {
		return "SimResult: \n[numTags=" + numTags + ", \n" +
				" numOwnedTags=" + numOwnedTags + ", \n" +
				" numReaders=" + numReaders + "\n nonRedundant="
				+ nonRedundant + "\n rounds=" + rounds + "\n readersPerRound="
				+ print(readersPerRound) + "\n nonRedundantReaders="
				+ print(nonRedundantReaders) + "\n numOverWrites=" + numOverWrites
				+ "\n numReads=" + numReads + "]";
	}

	private String print(ArrayList<Integer> r) {
		String s = "";
		for (int i = 0; i < r.size(); i++) { 
			s += r.get(i) + " "; 
		}
		return s; 
	}

	
	
	
	
}
