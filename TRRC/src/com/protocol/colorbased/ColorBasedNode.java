package com.protocol.colorbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.my.util.MyUtil;
import com.protocol.Node;
import com.protocol.directbased.DBMessageContent;
import com.protocol.directbased.DirectBasedComNode;
import com.simulator.Message;
import com.simulator.SimSystem;

public class ColorBasedNode extends Node {

	
	// status
	private static final String STAT_INIT = "STAT_INIT";
	private static final String STAT_TERMINATE = "STAT_TERMINATE";
	private static final String STAT_EX_TX_1 = "STAT_EX_TX_1";
	private static final String STAT_EX_RX_1 = "STAT_EX_RX_1";
	private static final String STAT_EX_TX_2 = "STAT_EX_TX_2";
	private static final String STAT_EX_RX_2 = "STAT_EX_RX_2";
	private static final String STAT_DECISION_MAKING = "STAT_DECISION_MAKING";

	
	// messages: 
	private static final String MSG_WEIGHT = "MSG_WEIGHT";
	private static final String MSG_DECISION = "MSG_DECISION";
	private static final String MSG_START_TX_1 = "MSG_START_TX_1";
	private static final String MSG_START_TX_2 = "MSG_START_TX_2";

	
	
	// global variables 
	private CBGlobalStruct globalViewer;
	private boolean D = true;
	private String algorithm;
	private HashMap<Integer, Boolean> visited; 
	
	public ColorBasedNode(SimSystem sim, int id, CBGlobalStruct g, String alg) {
		super(sim, id);

		this.status = ColorBasedNode.STAT_INIT; 
		this.globalViewer = g;
		this.algorithm = alg;
		
		setNeighborNodes(); 
		setNeighborTags();
		
		
	}


	
	@Override
	protected void initProtocol() {

		if (D) { 
			log.printf("scheduling for decision making \n");
		}
		
		
		// move to decision making. 
		changeStatus(ColorBasedNode.STAT_DECISION_MAKING);
		scheduleTimer(0, ColorBasedNode.MSG_DECISION);

	}

	@Override
	protected void handleReceivedMessage(Message message) {

		if (this.id != message.receiverId) { 
			log.printf("Error: received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, message.receiverId);
			abort();
		}
		
		if (status == ColorBasedNode.STAT_INIT) { 
			
			handleStatusInit(message);
			
		} else if (status == ColorBasedNode.STAT_DECISION_MAKING) {
			
			handleStatusDecisionMaking(message);
			
		} else if (status == ColorBasedNode.STAT_EX_TX_1) { 
			
			handleStatusEXTX1(message);
			
			
		} else if (status == ColorBasedNode.STAT_EX_RX_1) { 
			
			handleStatusEXRX1(message); 
			
		} else if (status == ColorBasedNode.STAT_EX_TX_2) { 
			
		} else if (status == ColorBasedNode.STAT_EX_RX_2) { 
			
		} else { 
			log.printf("error: node %d received message from %d at status %s \n", 
					this.id, message.senderId, status);
			
			log.printf("error at node %d. status %s is not recognized \n", this.id, status);
			abort();
		}
		
		
	}






	private void handleStatusInit(Message message) {
		
		if (message.msgType == Node.MSG_INIT) { 
			
			initProtocol();
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();
		}
		
	}

	
	private void handleStatusDecisionMaking(Message message) {
		
		if (message.msgType == ColorBasedNode.MSG_DECISION) { 
			
			if (isActive()) { 
				// schedule for a new communication round.
				changeStatus(ColorBasedNode.STAT_EX_TX_1);
				scheduleTimer(0, ColorBasedNode.MSG_START_TX_1);
			} else { 
				terminate();
			}
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
		}
		
	}

	
	private void handleStatusEXTX1(Message message) {
		
		if (message.msgType == ColorBasedNode.MSG_START_TX_1) {
			
			
			// start a new round; 
			round ++;
			
			// send to all your neighbors.
			ArrayList<Integer> nlist = this.getActiveNeighborNodes(); 
			
			for (int i = 0; i < nlist.size(); i++) { 
				// prepare message and send it.
				CBMessageContent mc = prepareMessage(round);
				Message msg = new Message(this.id, 
						nlist.get(i), 
						ColorBasedNode.MSG_WEIGHT, 
						mc);
				
				sendMessage(msg);
				
			}
	
			// initialize a visited array here: 
			// which neighbors you will be expecting to receive
			// from. 
			// TODO: we need arraylist visited.
			visited = new HashMap<Integer, Boolean>();
			for (int i = 0; i < nlist.size(); i++ ) { 
				visited.put(nlist.get(i), false);
			}
			
			
			changeStatus(ColorBasedNode.STAT_EX_RX_1);
			
			
			
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();				
		}
		
	}

	
	private void handleStatusEXRX1(Message message) {
		
		
		if (message.msgType == ColorBasedNode.MSG_WEIGHT) {
			
			// TODO:
			// tre at message. 
			// get shared tags. 
			// if shared tags > 0. 
			// then: if sender_weight > my_weight 
			// change ownership (temporarily). 
			// if I own all my neighbor active tags. 
			// then I am local maxima. 
			// I decativate all the active tags, 
			// and i announce this in the next round. 
			
			if (allVisited()) { 
				changeStatus(ColorBasedNode.STAT_EX_TX_2);
				scheduleTimer(this.msgDelay(), 
						ColorBasedNode.MSG_START_TX_2);
			}
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
		}
		
	}


	
	private boolean allVisited() {
		Iterator<Entry<Integer, Boolean>> it = visited.entrySet().iterator(); 
		while (it.hasNext()) { 
			Entry<Integer, Boolean> entry = it.next(); 
			if (! entry.getValue()) { 
				return false; 
			}
		}
		return true;
	}



	private CBMessageContent prepareMessage(int round) {
		return new CBMessageContent(this.getWeight());
	}



	private CBWeight getWeight() {
		if (algorithm == "GDE") { 
			return new CBWeight(this.getActiveNeighborTags().size(), 
					this.id, 
					this.round);
			
		} else { 
			
			log.printf("algorithm is not recognized at getweight() \n"); 
			abort();
			
			return null;
		}
	}



	private boolean isActive() {
		return (getActiveNeighborTags().size() > 0);
	}



	@Override
	public boolean isValidStatus(String str) {
		return (str == ColorBasedNode.STAT_INIT || 
				str == ColorBasedNode.STAT_TERMINATE || 
				str == ColorBasedNode.STAT_EX_TX_1 || 
				str == ColorBasedNode.STAT_EX_RX_1 || 
				str == ColorBasedNode.STAT_EX_TX_2 || 
				str == ColorBasedNode.STAT_EX_RX_2 || 
				str == ColorBasedNode.STAT_DECISION_MAKING
				);
	
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		return (str == ColorBasedNode.STAT_TERMINATE);
	}


	private void terminate() {
		changeStatus(ColorBasedNode.STAT_TERMINATE);
	}


	
	protected void setNeighborNodes() { 
		// do we need local neighbor lists; 
	}
	
	protected void setNeighborTags() { 
		// do we need local neighbor lists; 
	}
	
	public ArrayList<Integer> getNeighborNodes() { 
		return globalViewer.graphList.get(this.id).neighborNodes;
	}
	
	public ArrayList<Integer> getActiveNeighborNodes() { 
		return globalViewer.graphList.get(this.id).activeNeighborNodes; 
	}
	
	public ArrayList<Integer> getNeighborTags() { 
		return globalViewer.graphList.get(this.id).neighborTags; 
	}
	
	public ArrayList<Integer> getActiveNeighborTags() { 
		return globalViewer.graphList.get(this.id).activeNeighborTags;
	}
	
	public boolean deactivateNeighborNodeById(int id) { 
		
		return MyUtil.removeFromList(
				globalViewer.graphList.get(this.id).activeNeighborNodes, 
				id);
	}
	
	public boolean deactivateNeighborTagById(int id) { 
		
		return MyUtil.removeFromList(
				globalViewer.graphList.get(this.id).activeNeighborTags, 
				id);
	}
	
}
