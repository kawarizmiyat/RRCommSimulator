package com.protocol.colorbased;

import java.util.ArrayList;

public class NeighborLists {

	public ArrayList<Integer> neighborNodes;
	public ArrayList<Integer> activeNeighborNodes;
	public ArrayList<Integer> neighborTags;
	public ArrayList<Integer> activeNeighborTags;

	
	public NeighborLists() { 
		neighborNodes = new ArrayList<Integer>();
		activeNeighborNodes = new ArrayList<Integer>();
		neighborTags = new ArrayList<Integer>(); 
		activeNeighborTags = new ArrayList<Integer>();
	}
	
}
