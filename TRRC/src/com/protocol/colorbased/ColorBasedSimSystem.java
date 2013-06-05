package com.protocol.colorbased;



import java.util.ArrayList;

import com.protocol.Node;
import com.simulator.SimSystem;

public class ColorBasedSimSystem extends SimSystem {

	CBGlobalStruct globalStruct; 
	ArrayList<ArrayList<Integer> > rrGraph, rtGraph; 
	
	
	public ColorBasedSimSystem(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph, String algorithm) { 
		
		
		super();
		
		this.rrGraph = rrGraph; 
		this.rtGraph = rtGraph; 
		globalStruct = new CBGlobalStruct(rrGraph.size());
		
		
		setupProtocol(rrGraph, rtGraph, algorithm);
	}

	
	@Override
	protected void setupProtocol(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph, String alg) { 
		
		globalStruct.setNeighborReaders(rrGraph); 
		globalStruct.setNeighborTags(rtGraph);
		
		for (int i = 0; i < rrGraph.size(); i++) { 
			nodesTable.add(new ColorBasedNode(this, i, globalStruct, "GDE"));
		}
		
		setupInitiator();
		
	}
	


	private void setupInitiator() {
		// every node is an initiator. 
		for (int i = 0; i < this.rrGraph.size(); i++) { 
			this.setInitiator(i);
		}	
		
		// Note: you can specify only the nodes that you want.
	}

	@Override
	protected void analyzeResults() {
		Node n;
		for (int i = 0; i < nodesTable.size(); i++) { 
			
			n = nodesTable.get(i);
			
			if (! nodesTable.get(i).redundant) { 
				log.printf("node %d is not redundant: it owns (", nodesTable.get(i).id);
				for (int j = 0; j < n.ownedTags.size(); j++)  {
					log.printf("%d ", n.ownedTags.get(j));
				}
				log.printf(") color: %d; \n", nodesTable.get(i).color);
			}
		}
	}

}
