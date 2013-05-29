package com.protocol.rrecom;

import java.util.ArrayList;

public class RREGlobalStruct {

	public RREGraphEntity[][] nodesGraph;
	public ArrayList <ArrayList<Integer> > neighborsTagsTable;
	
	public RREGlobalStruct(int numNodes) { 
		
		// initialization. so that each reader writes its 
		// value without a null pointer excpetion.
		
		neighborsTagsTable = new ArrayList<ArrayList<Integer> > ();
		for (int i = 0; i < numNodes; i++) { 
			nodesGraph[i] = new RREGraphEntity[numNodes];
			neighborsTagsTable.add(new ArrayList<Integer>());
		}
		
	}

}
