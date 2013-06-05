package com.protocol.colorbased;

import java.io.PrintStream;

public class CBWeight {

	private PrintStream log;
	
	public int weight; 
	public int id; 
	public int round; 
	
	public CBWeight(int weight, int id, int round) {
		this.weight = weight;
		this.id = id; 
		this.round = round;
	}

	public int compareTo(CBWeight other) {
		
		if (this.round != other.round) { 
			log.printf("error: shouldn't rounds match in these algorithms \n"); 
			System.exit(0);
		}
		
		if (this.weight > other.weight || 
				((this.weight == other.weight) && (this.id > other.id)) 
				) {
			return 1; 
		} 
		
		if (this.weight == other.weight && this.id == other.id) { 
			return 0; 
		}
		 
		return -1; 
		
		
	}

}
