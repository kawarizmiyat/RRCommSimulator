package com.protocol.directbased;

import java.util.ArrayList;

public class DBGlobalStruct {

	public DBGraphEntity[][] nodesGraph;
	public ArrayList <ArrayList<Integer> > neighborsTagsTable;
	
	public DBGlobalStruct(int numNodes) { 
		
		// initialization. so that each reader writes its 
		// value without a null pointer excpetion.
		
		neighborsTagsTable = new ArrayList<ArrayList<Integer> > ();
		nodesGraph = new DBGraphEntity[numNodes][numNodes];
		
		for (int i = 0; i < numNodes; i++) { 
			
			neighborsTagsTable.add(new ArrayList<Integer>());
			
			for (int j = 0; j < numNodes; j++) { 
				nodesGraph[i][j] = new DBGraphEntity();
			}
		}
		
		
	}

}
