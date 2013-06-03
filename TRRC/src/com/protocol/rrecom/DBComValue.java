package com.protocol.rrecom;

public class DBComValue {

	
	
	protected int weight;
	protected int id;

	public DBComValue(int weight, int id) {
		this.weight = weight; 
		this.id = id;
	}

	public int compareTo(DBComValue s) {
		if (this.weight > s.weight) return 1; 
		if (this.weight == s.weight) { 
			if (this.id > s.id ) return 1; 
			if (this.id == s.id) return 0;
		}
		
		// otherwise
		return -1;
	}

	@Override
	public String toString() {
		return "(" + weight + ", " + id + ")";
	}

	
	
	
}
