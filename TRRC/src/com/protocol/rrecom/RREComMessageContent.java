package com.protocol.rrecom;

public class RREComMessageContent {

	
	public int type;
	public int weight;
	
	public RREComMessageContent(int type) { 
		this.type = type;
	}

	public RREComMessageContent(int type, int weight) { 
		this.type = type;
		this.weight = weight;
	}
	
}
