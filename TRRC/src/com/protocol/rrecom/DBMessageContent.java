package com.protocol.rrecom;

public class DBMessageContent {

	
	public int type;
	public int weight;
	
	public DBMessageContent(int type) { 
		this.type = type;
	}

	public DBMessageContent(int type, int weight) { 
		this.type = type;
		this.weight = weight;
	}
	
}
