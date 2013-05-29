package com.protocol;

public class Neighbor {

	public int id; 
	public boolean deleted; 
	public boolean active; 
	
	public Neighbor(int id) { 
		this.id = id; 
		this.deleted = false;
		this.active = true;
	}
	
}
