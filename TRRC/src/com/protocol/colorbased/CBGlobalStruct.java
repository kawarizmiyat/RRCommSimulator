package com.protocol.colorbased;

import java.util.ArrayList;


public class CBGlobalStruct {

	public boolean[][] nodesGraph; 
	public ArrayList<NeighborLists> graphList;
	
	public CBGlobalStruct(int numNodes) {
		
		nodesGraph = new boolean[numNodes][numNodes]; 
		graphList = new ArrayList<NeighborLists>();
		
		for (int i = 0; i < numNodes; i++) { 
			graphList.add(new NeighborLists());
		}
		
	}

	public void setNeighborReaders(ArrayList<ArrayList<Integer>> rrGraph) { 
		for (int i = 0; i < rrGraph.size(); i++) { 
			graphList.get(i).neighborNodes.addAll(rrGraph.get(i));
			graphList.get(i).activeNeighborNodes.addAll(rrGraph.get(i));
		}
 	}

	public void setNeighborTags(ArrayList<ArrayList<Integer>> rtGraph) { 
		for (int i = 0; i < rtGraph.size(); i++) { 
			graphList.get(i).neighborTags.addAll(rtGraph.get(i));
			graphList.get(i).activeNeighborTags.addAll(rtGraph.get(i));
		}		
	}
	
}
