package com.protocol.rrecom;

import java.util.ArrayList;

import com.simulator.SimSystem;

public class RRESimSystem extends SimSystem {

	RREGlobalStruct globalStruct; 
	ArrayList<ArrayList<Integer> > rrGraph, rtGraph; 
	
	
	public RRESimSystem(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph) { 
		
		
		super();
		
		this.rrGraph = rrGraph; 
		this.rtGraph = rtGraph; 
		globalStruct = new RREGlobalStruct(rrGraph.size());
		
		
		setupProtocol(rrGraph, rtGraph);
	}
	
//	public void setRTGraph(String algorithm, ArrayList<ArrayList<Integer>> rtGraph) {
//
//
//		int numReaders = rtGraph.size();
//		initiateReaders(numReaders, algorithm);
//
//		for (int i = 0; i < g.size(); i++) { 
//
//			for (int j = 0; j < g.get(i).size(); j++) { 
//				nodesTable.get(i).addNodeNeighbor(rtGraph.get(i).get(j));
//			}
//
//		}
//		
//		for (int i = 0; i < nodesTable.size(); i ++ ) { 
//			setInitiator(i);
//		}
//
//	}
	
	@Override
	protected void setupProtocol(ArrayList<ArrayList<Integer>> rrGraph,
			ArrayList<ArrayList<Integer>> rtGraph) {
		
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
			nodesTable.add(new RREComNode(this, i, globalStruct));
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
	}

}
