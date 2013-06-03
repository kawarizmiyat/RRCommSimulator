package com.protocol.directbased;

import java.util.ArrayList;

import com.protocol.Node;
import com.simulator.SimSystem;

public class DirectBasedSimSystem extends SimSystem {

	DBGlobalStruct globalStruct; 
	ArrayList<ArrayList<Integer> > rrGraph, rtGraph; 
	
	
	public DirectBasedSimSystem(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph, String algorithm) { 
		
		
		super();
		
		this.rrGraph = rrGraph; 
		this.rtGraph = rtGraph; 
		globalStruct = new DBGlobalStruct(rrGraph.size());
		
		
		setupProtocol(rrGraph, rtGraph, algorithm);
	}
	
	
	@Override
	protected void setupProtocol(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph, String alg) {
		
		// In RRE: we require a global structure. 
		// + every node is an initiator. 
		for (int i = 0; i < rrGraph.size(); i++) { 
			
			for (int j = 0; j < rrGraph.get(i).size(); j++) { 
				globalStruct.nodesGraph[i][rrGraph.get(i).get(j)].edge = true;
			}
			
		}
		
		// make this as our global rt graph. 
		// there will be no change in any cse. 
		globalStruct.neighborsTagsTable = rtGraph; 
		


		// initiate the nodes. 
		
		for (int i = 0; i < rrGraph.size(); i++ ) { 
			nodesTable.add(new DirectBasedComNode(this, i, globalStruct, alg));
		}

		// initiate the reader-tag relationship at each node. 
		for (int i = 0; i < rtGraph.size(); i++) { 

			for (int j = 0; j < rtGraph.get(i).size(); j++) { 
				nodesTable.get(i).addTagNeighbor(rtGraph.get(i).get(j));
			}

		}
		
		// initiate the reader-reader relationships.
		for (int i = 0; i < rrGraph.size(); i++) { 
			for (int j = 0; j < rrGraph.get(i).size(); j++) { 
				nodesTable.get(i).addNodeNeighbor(rrGraph.get(i).get(j));
			}
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
				log.printf("); \n");
			}
		}
	}

}
