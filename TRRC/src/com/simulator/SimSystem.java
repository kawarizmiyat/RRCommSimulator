package com.simulator; 

import java.io.PrintStream;
import java.util.ArrayList;

import com.protocol.Node;





public abstract class SimSystem  {
	// public FutureEventList future;	// Note that we used herein FutureEventList
	// instead of EventQueue.

	public EventQueue future;

	// for keeping statistics
	private final double END = 20000; // marks the end of the simulation period
	protected ArrayList<Node> nodesTable;

	private static final boolean D = false; 
	private final PrintStream log = System.out; 

	
	public SimSystem() { 
		setupSimulator(); 
	}


	protected abstract void setupProtocol(
			ArrayList<ArrayList<Integer> > rrGraph,
			ArrayList<ArrayList<Integer> > rtGraph);


	protected void setInitiator(int i) {
		
		if (D) { 
			log.printf("reader %d is set to be initiator \n", i);
		} 

		Event e = new Event(); 
		e.time = 0; 
		e.action = EventType.MESSAGE; 
		Message m = new Message(-1, i, Node.MSG_INIT, "");
		e.message = m;
		
	
		this.future.enter(e);
	}

	public void setupSimulator() { 
		future = new EventQueue();
	}

	public void run(){
		System.out.println("**** simulation starts **** ");		


		while(true){

			if(0 == future.getNbEvents()) { 

				if (correctTermination()) { 
					log.printf("*** simulation terminated *** \n");
					analyzeResults();
				} else { 
					log.printf("error: some readers did not terminate \n");
				}

				break;
			}

			Event nextE = future.getNext();


			if(nextE == null) {
				System.out.println("null pointer"); 
				break;
			}

			if(nextE.time > END) {
				System.out.println("END time = " + nextE.time); 
				break;
			}

			if (nextE.action == EventType.MESSAGE) {
				int handlerId = nextE.message.receiverId;
				nodesTable.get(handlerId).handleEvent(nextE);
			}


		}

	}


	private boolean correctTermination() {
		for (int i = 0; i < this.nodesTable.size(); i++) { 
			if (! nodesTable.get(i).isTerminated()) { 
				log.printf("at least Reader %d did not terminate \n",
						i);
				return false;
			}
		}
		return true;
	}


	// This is a simple graph extractor. The graph 
	// contains all the reader-tag relationship. 
	// We assume that line i contains all the neighbor tags 
	// of reader i.
//	public void setRTGraph(String algorithm, ArrayList<ArrayList<Integer>> g) {
//
//		numTags = getTagsSize(g);
//		numReaders = g.size();
//		initiateReaders(numReaders, numTags, algorithm);
//
//		for (int i = 0; i < g.size(); i++) { 
//
//			for (int j = 0; j < g.get(i).size(); j++) { 
//				nodesTable.get(i).addTagNeighbor(g.get(i).get(j));
//			}
//
//		}
//		
//		for (int i = 0; i < nodesTable.size(); i ++ ) { 
//			setInitiator(i);
//		}
//
//	}





	private void analyzeResults() { }







}


