package com.protocol.colorbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.my.util.MyUtil;
import com.protocol.Node;
import com.protocol.Tag;
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
	private static final String STAT_EX_TX_TERM = "STAT_EX_TX_TERM";
	private static final String STAT_EX_RX_TERM = "STAT_EX_RX_TERM";
	
	// messages: 
	private static final String MSG_WEIGHT = "MSG_WEIGHT";
	private static final String MSG_LOCAL_MAX = "MSG_LOCAL_MAX";
	private static final String MSG_START_TX_1 = "MSG_START_TX_1";
	private static final String MSG_START_TX_2 = "MSG_START_TX_2";
	private static final String MSG_DECISION_MAKING = "MSG_DECISION_MAKING";
	private static final String MSG_START_TX_TERM = "MSG_START_TX_TERM";
	private static final String MSG_TERMINATING = "MSG_TERMINATING";

	
	// global variables 
	private CBGlobalStruct globalViewer;
	private boolean D = true;
	private String algorithm;
	private HashMap<Integer, Boolean> visited; 
	private boolean localMaximum = false;
	
	
	// constructor
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
		scheduleTimer(0, ColorBasedNode.MSG_DECISION_MAKING);

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
			
			handleStatusEXTX2(message);
			
		} else if (status == ColorBasedNode.STAT_EX_RX_2) { 
			
			handleStatusEXRX2(message);
			
		} else if (status == ColorBasedNode.STAT_EX_TX_TERM) { 
			
			handleStatusEXTXTerm(message);
			
		} else if (status == ColorBasedNode.STAT_EX_RX_TERM) { 
			
			handleStatusEXRXTerm(message);
			
		} else { 
			log.printf("error: node %d received message from %d at status %s \n", 
					this.id, message.senderId, status);
			
			log.printf("error at node %d. status %s is not recognized \n", this.id, status);
			abort();
		}
		
		
	}





	// INIT x message (message: msg_init is from an outsider)
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

	
	// DECISION_MAKING x message
	private void handleStatusDecisionMaking(Message message) {
		
		if (message.msgType == ColorBasedNode.MSG_DECISION_MAKING) { 
			
			if (D) { 
				log.printf("node %d is making a decision at round %d \n", this.id, round); 
			}
			
			// Q. two options or three options ? 
			// 1) if local maximum, then your neighbors would deactivate you
			// so all you need to do is to go to terminate directly. 
			// -- also: if you are a local maximum, then all your neighbors tags 
			//    are deactivated.  -- 
			
//			if (round > 4) { 
//				log.printf("error at node %d \n", this.id);
//				abort();
//			}
			
			
			if (localMaximum) { 
				// note: we deactivate the neighbor tags here (and own them), 
				// because we avoid situations where other nodes may use the 
				// global graphList
				
				if (D) { 
					log.printf("node %d decision: local maximum so, i m terminating \n", 
							this.id);
				}
				
				
				deactivateAndOwnMyActiveTags(); 
				color = round; 
				terminate();
				return; 
			}
			

			// there is still one case: 
			// if all my active neighbor readers were deactivated local maximums. 
			if (this.getActiveNeighborNodes().size() == 0) { 
				if (D) { 
					log.printf("node %d decision: all my neighbors are local maximums\n", 
							this.id);
				}
				
				
				deactivateAndOwnMyActiveTags(); 
				color = round + 1;
				terminate();
				return; 
				
			}
			
			
			if (isActive()) { 
				// schedule for a new communication round.
				
				if (D) { 
					log.printf("node %d decision: I am still active ! \n", 
							this.id);
				}
				
				changeStatus(ColorBasedNode.STAT_EX_TX_1);
				scheduleTimer(0, ColorBasedNode.MSG_START_TX_1);
			} else { 
				
				if (D) { 
					log.printf("node %d decision: no more neighbor tags ! I am terminating \n", 
							this.id);
				}
								
				changeStatus(ColorBasedNode.STAT_EX_TX_TERM); 
				scheduleTimer(0, ColorBasedNode.MSG_START_TX_TERM);
				

			}
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
		}
		
	}

	// EX TX1 x message (msg_start_tx_1 is a timer)
	private void handleStatusEXTX1(Message message) {
		
		if (message.msgType == ColorBasedNode.MSG_START_TX_1) {
			
			
			initIteration();
			
			
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



	// EX RX1 x message
	private void handleStatusEXRX1(Message message) {
		
		
		if (message.msgType == ColorBasedNode.MSG_WEIGHT) {
			

			// treat message. 
			// get shared tags. 
			// if shared tags > 0. 
			// then: if sender_weight > my_weight 
			// change ownership (temporarily). 
			// if I own all my neighbor active tags. 
			// then I am local maxima. 
			// I decativate all the active tags, 
			// and i announce this in the next round. 
			
			if (D) { 
				log.printf("node %d received msg weight from %d \n", 
						this.id, message.senderId);
			}
			
			
			directEdge(message);
			
			

			
		} else if (message.msgType == ColorBasedNode.MSG_TERMINATING) {	
		
			// 1) remove neighbor from active neighbors, 
			// 2) mark visited. 
			
			if (D) { 
				log.printf("node %d received terminating messsage from %d at status %s \n",
						this.id, message.senderId, status);
			}
			
			deactivateNeighborNodeById(message.senderId); 
			setVisitedById(message.senderId);
			
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
		}
		
		
		if (allVisited()) { 
			changeStatus(ColorBasedNode.STAT_EX_TX_2);
			scheduleTimer(this.msgDelay(), 
					ColorBasedNode.MSG_START_TX_2);
		}
		
	}

	private void handleStatusEXTX2(Message message) {
		if (message.msgType == ColorBasedNode.MSG_START_TX_2) { 
			
			if (D) { 
				log.printf("node %d starting status %s at round %d \n",
						this.id, status, round);
			}
			
			
			localMaximum = new Boolean(isLocalMaximum());
			ArrayList<Integer> nlist = this.getActiveNeighborNodes(); 
			for (int i = 0; i < nlist.size(); i++ ) { 
				
				Message msg = new Message(this.id, 
						nlist.get(i), 
						ColorBasedNode.MSG_LOCAL_MAX, 
						localMaximum);
				
				sendMessage(msg);
			}
			
			

			
			
			// initialize the visited array.
			visited = new HashMap<Integer, Boolean>();
			for (int i = 0; i < nlist.size(); i++ ) { 
				visited.put(nlist.get(i), false);
			}
			
			
			changeStatus(ColorBasedNode.STAT_EX_RX_2);
			
			
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();
		}
		
	}


	private void handleStatusEXRX2(Message message) {
		if (message.msgType == ColorBasedNode.MSG_LOCAL_MAX) { 
			
			if (D) { 
				log.printf("node %d received %s from %d at status %s \n", this.id, 
						message.msgType, message.senderId, status);
			}
			
			
			int senderId = message.senderId; 
			Boolean senderDecision = (Boolean) message.msgContent; 
			if (senderDecision == true) { 
				
				if (D) { 
					log.printf("node %d found that its neighbor %d is local maximum, " +
							"now it will deactivate it and all its active tags \n",
							this.id, senderId);
				}
				
				deactivateSenderActiveTags(senderId);
				boolean r = deactivateNeighborNodeById(senderId);
				if (! r) { 
					log.printf("error: node %d trying to deactivate neighbor that " +
							"does not exist !\n", this.id); 
					abort();
				}
			}
			
			this.setVisitedById(senderId); 
			
			if (this.allVisited()) { 
				changeStatus(ColorBasedNode.STAT_DECISION_MAKING);
				scheduleTimer(this.msgDelay(), 
						ColorBasedNode.MSG_DECISION_MAKING);				
			}
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();
		}
		
	}
	




	private void handleStatusEXTXTerm(Message message) {

		if (message.msgType == ColorBasedNode.MSG_START_TX_TERM) { 

			
		ArrayList<Integer> nlist = this.getActiveNeighborNodes(); 
		for (int i = 0; i < nlist.size(); i++ ) { 
			
			Message msg = new Message(this.id, 
					nlist.get(i), 
					ColorBasedNode.MSG_TERMINATING, 
					null);
			
			if (D) { 
				log.printf("node %d send terminating msg to %d \n", 
						this.id, nlist.get(i)); 
			}
			
			sendMessage(msg);
		}
		
		
		// initialize the visited array.
		visited = new HashMap<Integer, Boolean>();
		for (int i = 0; i < nlist.size(); i++ ) { 
			visited.put(nlist.get(i), false);
		}
		
		
		changeStatus(ColorBasedNode.STAT_EX_RX_TERM);
		
		} else { 
			
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
			
		}
		
	}



	private void handleStatusEXRXTerm(Message message) {

		// there are two types of messaegs here: 
		// the response should be "mark visited" .. once 
		// all visited .. go to terminate.

		
		// 1) remove neighbor from active neighbors, 
		// 2) mark visited. 

		
		if (message.msgType == ColorBasedNode.MSG_WEIGHT || 
				message.msgType == ColorBasedNode.MSG_TERMINATING) { 
		
			if (D) { 
				log.printf("node %d received terminating messsage from %d at status %s \n",
					this.id, message.senderId, status);
			}
		
			setVisitedById(message.senderId);
			
			if (this.allVisited()) { 
				// maybe it would be better to update the clock. 
				// it is not necessary though.
				terminate();
			}
			
		} else { 

			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();			
			
		}
		
	}
	
	private void deactivateAndOwnMyActiveTags() { 
		
		// 0. own all my neighbor tags (that are active).
		// 1. set all my neighbor tags to not active. 
		// 2. remove my neighbor tags from graphList. 
		
		ArrayList<Integer> nlist = this.getActiveNeighborTags();
		ArrayList<Integer> copyNlist = new ArrayList<Integer>();
		copyNlist.addAll(this.getActiveNeighborTags()); 
		
		for (int i = 0; i < copyNlist.size(); i++) { 
			this.ownTag(copyNlist.get(i));
			boolean r = MyUtil.removeFromList(nlist, copyNlist.get(i));
			if (r == false) { 
				log.printf("error: at node %d removing a tag %d from my global active" +
						" list that is not avaialble \n", 
						this.id, copyNlist.get(i)); 
			}
		}
		
		
		Iterator<Entry<Integer, Tag>> it = this.neighborsTags.entrySet().iterator(); 
		while (it.hasNext()) { 
			Entry<Integer, Tag> entry = it.next(); 
			entry.getValue().active = false;
		}
		
		
	}
	
	
	private void deactivateSenderActiveTags(int senderId) {
		
		ArrayList<Integer> saTags = new ArrayList<Integer>(); 
		saTags.addAll(globalViewer.graphList.get(senderId).activeNeighborTags);
			
		
		// Note: there could be some tags not in myTags; 
		// these tags should be ignored. there is no error therefore.
		for (int i = 0; i < saTags.size(); i++) { 
			boolean r = deactivateNeighborTagById(saTags.get(i));
			if (D) { 
				log.printf("node %d deactivate tag %d ", 
						this.id, saTags.get(i)); 
				log.print(r + " \n");
			}
		}
		
	}



	private boolean isLocalMaximum() {
		
		// there are only three choices of owners:
		// me, not me, not init. (me and not init) means that I owns the tag. 
		// not me then certainly i dont own it. 
		
		Iterator<Entry<Integer, Tag>> it = this.neighborsTags.entrySet().iterator();
		while (it.hasNext()) { 
			Entry<Integer, Tag> entry = it.next(); 
			
			if (entry.getValue().active && 
					entry.getValue().owner == Tag.NOT_ME) { 
				return false;
			}
		}
		
		if (D) { 
			log.printf("node %d is found to be a local maximum \n", this.id);
		}
		
		return true;
	}



	private void initIteration() {
		round ++; 		// round here is for the whole iteration. 
						// (not only one communication round) 
						// TODO: change its naming then.
		
		// TODO: when  deactivating a tag - we should do two things ! 
		// all active tags must be set to not_init. 
		Iterator<Entry<Integer, Tag>> it = this.neighborsTags.entrySet().iterator();
		while (it.hasNext()) { 
			Entry<Integer, Tag> entry = it.next(); 
			Tag t = entry.getValue(); 
			
			if (t.active) { 
				t.owner = Tag.NOT_INIT;
			}
		}
		
		if (D) { 
			log.printf("node %d is starting new round (%d) \n", this.id, round); 
		}
		
	}


	
	private void directEdge(Message message) {

		int senderId = message.senderId;
		CBMessageContent mc = ((CBMessageContent) message.msgContent);
		CBWeight senderWeight = mc.weight;
		
		// set sender id: 
		setVisitedById(senderId);
		
		// find shared tags. 
		ArrayList<Integer> senderActiveTags = globalViewer.graphList.get(senderId).activeNeighborTags; 
		ArrayList<Integer> myActiveTags = getActiveNeighborTags();
		ArrayList<Integer> sharedTags = MyUtil.intersect(
				senderActiveTags, myActiveTags);

		
		// direct the edges according to the weight function.
		for (int i = 0; i < sharedTags.size(); i++) { 
			switch (neighborsTags.get(sharedTags.get(i)).owner) { 
			case Tag.NOT_INIT: 

				if (senderWeight.compareTo(getWeight()) > 0) { 
					
					neighborsTags.get(sharedTags.get(i)).owner = Tag.NOT_ME; 
				} else { 
					
					if (D) { 
						log.printf("node %d temporarily owns tag %d at round %d \n", 
								this.id, sharedTags.get(i), round);
					}
					
					neighborsTags.get(sharedTags.get(i)).owner = Tag.ME; 
				}
										
				break; 
				
			case Tag.ME: 
					if (senderWeight.compareTo(getWeight()) > 0) { 
						neighborsTags.get(sharedTags.get(i)).owner = Tag.NOT_ME; 
					}

					if (D) { 
						log.printf("node %d does not own tag %d anymore \n", 
								this.id, sharedTags.get(i));
					}
					
				break; 
				
			case Tag.NOT_ME: 
				// do nothing. - if it is owned by another node, 
				// then it will never be owned by you.
				break; 
			default: 
				log.printf("error at %d: cannot accept this as owner value \n");
				abort();
			}

		}
		
	}





	private void setVisitedById(int id) {

		boolean dIn = true;
		if (! visited.get(id)) {
			visited.put(id, true);
			
			if (D && dIn) { 
				log.printf("node %d marked %d as visited in status %s \n", 
						this.id, id, status);
			}
			
			return; 

		} else { 
			log.printf("error: node %d trying to mark as visited a node that is not " +
					"in visited \n", this.id);
			abort();
			
		}
		
	}



	private boolean allVisited() {
		
		boolean dIn = false;
		
		Iterator<Entry<Integer, Boolean>> it = visited.entrySet().iterator(); 
		while (it.hasNext()) { 
			Entry<Integer, Boolean> entry = it.next(); 
			if (! entry.getValue()) { 
				
				if (D && dIn) { 
					log.printf("node %d found that at least %d is " +
							"not visited in status %s\n ", this.id, entry.getKey(), 
							status);
				}
				
				return false; 
			}
		}
		
		if (D && dIn) {
			log.printf("node %d -- is all visited at status %s \n", this.id, status);
		}
		
		return true;
	}



	private CBMessageContent prepareMessage(int round) {
		return new CBMessageContent(getWeight());
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
				str == ColorBasedNode.STAT_DECISION_MAKING || 
				str == ColorBasedNode.STAT_EX_TX_TERM || 
				str == ColorBasedNode.STAT_EX_RX_TERM 
				);
	
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		return (str == ColorBasedNode.STAT_TERMINATE);
	}


	private void terminate() {
		
		if (D) {
			log.printf("node %d is terminating at round %d \n", 
					this.id, round);
		}
		
		makeRedundancyDecision();
		
		changeStatus(ColorBasedNode.STAT_TERMINATE);
	}


	
	private void makeRedundancyDecision() {
		
		
		if (D) { 
			log.printf("node %d checks whether it is redundant or not. \n", this.id);
		}
		
		// check whether you are redundant or not.
		if (this.ownedTags.size() > 0) { 
			if (D) { 
				log.printf("node %d is found non-redundant \n", this.id);
			}
			
			this.redundant = false;
		} else { 
			this.redundant = true;
		}
		
	}



	protected void setNeighborNodes() { 
		// do we need local neighbor lists ?
	}
	
	protected void setNeighborTags() { 
		ArrayList<Integer> globalNTags = this.getNeighborTags(); 
		for (int i = 0; i < globalNTags.size(); i++) { 
			neighborsTags.put(globalNTags.get(i), new Tag(globalNTags.get(i)));
		}
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
	
	
	// there is an error here: 
	public boolean deactivateNeighborTagById(int id) { 

		boolean c0 = true;
		if (neighborsTags.get(id) == null ) { 
			c0  = false;
		} else { 
			this.neighborsTags.get(id).active = false;
		}
		
		
		
		boolean c1 = MyUtil.removeFromList(
				globalViewer.graphList.get(this.id).activeNeighborTags, 
				id);
		
//		if (! c1) { 
//			log.printf("tag %d is not found in the global active tags of %d \n", 
//					id, this.id);
//			for (int i = 0; i < this.getActiveNeighborTags().size(); i++) { 
//				log.printf("(%d)", this.getActiveNeighborTags().get(i));
//			}
//			log.printf("\n");
//			abort();
//		}
		
		if (D) { 
			log.printf("result of deactivateNeighborTag at node %d : c0: %b, c1:%b \n", 
					this.id, c0, c1);
		}
		
		return (c0 && c1); 
	}
	
}
