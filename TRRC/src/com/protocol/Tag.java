package com.protocol;

public class Tag {

	public int id, owner; 
	
	public static final int NOT_INIT = -1; 
	public static final int NOT_ME = 0; 
	public static final int ME = 1;
	
	public Tag(int id) { 
		this.id = id;
		this.owner = -1;
	}
	
}
