package com.protocol.rrecom;

public class RREComValue {

	
	
	protected int weight;
	protected int id;

	public RREComValue(int weight, int id) {
		this.weight = weight; 
		this.id = id;
	}

	public int compareTo(RREComValue s) {
		if (this.weight > s.weight) return 1; 
		if (this.weight == s.weight) { 
			if (this.id > s.id ) return 1; 
			if (this.id == s.id) return 0;
		}
		
		// otherwise
		return -1;
	}

}
