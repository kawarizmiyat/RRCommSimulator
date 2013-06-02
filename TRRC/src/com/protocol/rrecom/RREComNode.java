package com.protocol.rrecom;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.my.util.MyUtil;
import com.protocol.Neighbor;
import com.protocol.Node;
import com.protocol.Tag;
import com.simulator.Message;
import com.simulator.SimSystem;

public class RREComNode extends Node {


	private static final String STAT_INIT = "STAT_INIT";
	private static final String STAT_TERMINATE = "STAT_TERMINATE";
	private static final String STAT_EX_TX = "EX_TX";
	private static final String STAT_EX_RX = "EX_RX";
	
	private static final String  MSG_EX = "MSG_EX";
	
	// sub-types of message type (MSG_TYPE_EX_0)
	private static final int MSG_TYPE_EX_0 = 0;	// in RRE: this is only 
												// for direction only. 

	
	
	private RREComValue myWeight;
	private final boolean D = true;
	private RREGlobalStruct globalViewer; 
	
	public RREComNode(SimSystem sim, int id, RREGlobalStruct g) {
		super(sim, id);
		
		this.status = RREComNode.STAT_INIT; 
		this.globalViewer = g;
	}

	@Override
	protected void initProtocol() {
		if (D) { 
			log.printf("node %d initiates the algorithm \n", this.id);
		}
		
		myWeight = new RREComValue(this.numNeighborTags, this.id);
		startRound();
	}



	private void startRound() {

		
		if (D) { 
			log.printf("node %d starting a round \n", this.id);
		}
		
		
		// start the sending part of the communication round.
		changeStatus(RREComNode.STAT_EX_TX); 
		
		
		// iterate over all your neighbros - and send a message msg_ex. 
		// no content. they know what to do. (this is just for simulation of course).
		Iterator<Entry<Integer, Neighbor>> it = neighborsNodes.entrySet().iterator();
		while (it.hasNext()) {
				Entry<Integer, Neighbor> pairs = it.next();
	        
				Neighbor n = pairs.getValue(); 
		        if (n.active) { 
		        	
					RREComMessageContent mc = prepareMessage(round);
					Message msg = new Message(this.id, n.id, RREComNode.MSG_EX, mc);
					
					sendMessage(msg);
					
					if (D) { 
						log.printf("node %d sending a message to %d \n", this.id, n.id);
					}
		        }
		        
		}
		
		// start at the receiving part of the communication round.
		changeStatus(RREComNode.STAT_EX_RX);

		
	}

	private RREComMessageContent prepareMessage(int r) {
		
		if (r == 0) { 
			return new RREComMessageContent(RREComNode.MSG_TYPE_EX_0, numNeighborTags);
		} else {
			return null; 
		}
	
	}

	@Override
	protected void handleReceivedMessage(Message message) {

		if (this.id != message.receiverId) { 
			log.printf("Error: received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, message.receiverId);
			abort();
		}
		
		if (status == RREComNode.STAT_INIT) { 
			handleStatusInit(message);
			
		} else if (status == RREComNode.STAT_EX_RX) { 
			handleStatusExRx(message);
		
		} else { 
			
			log.printf("error: node %d received message from %d at status %s \n", 
					this.id, message.senderId, status);
			
			log.printf("error at node %d. status %s is not recognized \n", this.id, status);
			abort();
		}
		
		
		
	}

	private void handleStatusExRx(Message message) {
		
		if (message.msgType == RREComNode.MSG_EX) { 
			
			if (D) { 
				log.printf("node %d received message MSG_EX at status %s \n", this.id, status);
			}
			
			
			RREComMessageContent rmc =  (RREComMessageContent) message.msgContent; 


			if (rmc.type == RREComNode.MSG_TYPE_EX_0) { 
				
				if (D) { 
					log.printf("node %d received msg_type_ex 0 ... \n", this.id);
				}
				
				// direct the edge (myself, sender)
				directEdge(message);
				
				// test if all visited - to terminate the communication round.
				if (allVisited(rmc.type)) { 
					makeRedundancyDecision(); 
					terminate();
				}
				
			} else { 
				log.printf("error at %d: unrecognized msg sub-type . \n"); 
				abort();
			}
			
			
		} else { 
			log.printf("error at node %d - cannot received " +
					"%s at status %s. \n", this.id, 
					message.msgType, status);
			abort();
		}
		
	}


	private void makeRedundancyDecision() {
		
		if (D) { 
			log.printf("node %d checks whether it is redundant or not. \n", this.id);
		}
		
		// check which tags you own. This can be taken only after the communication round 
		// is over.
		// iterate over a hash table.
		Tag t; 
		Iterator<Entry<Integer, Tag>> it = neighborsTags.entrySet().iterator();
		while (it.hasNext()) {
				Entry<Integer, Tag> pairs = it.next();
	        
				t = pairs.getValue(); 
				if (t.owner == Tag.ME) {
					
					if (D) { 
						log.printf("node %d owns tag %d. reasong: Tag.ME \n", this.id, t.id);
					}
					
					ownTag(t.id);
				}
				
				// a special case: if the owner of this tag is "not init", then 
				// this tag is owned by me. 
				// why ? because I am sure that I have visited all my neighbors. 
				// if I've shared t with any other, its owner would have been changed. 
				// therefore, t is a neighbor to me only !
				// Note: according to our assumption that tc > 2 x ts, if two nodes 
				// share a tag than thy can communicate with each other. 
				// However, this assumption does not say that all my tags are covered by 
				// my neighbor readers. 
				if (t.owner == Tag.NOT_INIT) { 

					if (D) { 
						log.printf("node %d owns tag %d. reasong: Tag.NON_INIT \n", this.id, t.id);
					}
					
					ownTag(t.id);
				}
				
		        
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

	// additionalInfo is used to differentiate the sub-statuses.
	private boolean allVisited(int additionalInfo) {
		
		
		if (status == RREComNode.STAT_EX_RX) { 
		
			RREGraphEntity[] t = globalViewer.nodesGraph[this.id];
		
			for (int i = 0; i < t.length; i++) { 
				
				if (D) { 
					if (t[i].edge) { 
						log.printf("node %d at all visited checking neighbor %d (linkvisited: " + 
								t[i].linkVisited + ")\n", 
								this.id, i);
					}
				}
				
				if ((t[i].edge) && (!t[i].linkVisited)) {
					
					if (D) { 
						log.printf("at node %d: neighbor is not visited. \n", this.id, i);
					}
					return false;
				}
			}

			// i.e. all neighbors are visisted. 
			if (D) { 
				log.printf("node %d visited all its nodes \n", this.id);
			}
			return true;

		} else { 
			log.printf("error at node %d allVisisted does not decide \n",
					this.id);
			abort();
			
			return false;
		}
		
	}

	private void directEdge(Message message) {
		
		
		
		int sender = message.senderId;
		int senderWeight = ((RREComMessageContent) message.msgContent).weight;
		RREComValue sw = new RREComValue(senderWeight, sender);
		
		
		if (D) { 
			log.printf("node %d in directEdge with node %d \n", this.id, sender);
			
		}
		
		// to eliminate visiting a neigbhor twice. 
			
			// setting the link visited state to true. .
			globalViewer.nodesGraph[this.id][sender].linkVisited = true;
			// globalViewer.nodesGraph[sender][this.id].linkVisited = true;
			
			if (D) { 
				log.printf("switching the state of (%d,%d) and (%d,%d) to true \n", 
						this.id, sender, sender, this.id);
			}
			
			
			// finding shared tags between sender and myself.
			ArrayList<Integer> sharedTags = MyUtil.interesect(
				globalViewer.neighborsTagsTable.get(sender), 
				globalViewer.neighborsTagsTable.get(this.id) ); 
			
			
			if (D) { 
				log.printf("node %d share %d tags with node %d \n",
						this.id, sharedTags.size(), sender);
			}			
			
			// check whether the two nodes share at least one tag. 
			if (sharedTags.size() > 0) { 
				globalViewer.nodesGraph[this.id][sender].shareTag = true;
				globalViewer.nodesGraph[sender][this.id].shareTag = true;
			}		
			
			
			// if they share a tag then:
			if (globalViewer.nodesGraph[this.id][sender].shareTag) { 
		
				for (int i = 0; i < sharedTags.size(); i++) { 
					
					if (D)  {
						log.printf("node %d testing shared tag %d \n", this.id, sharedTags.get(i));
					}
					
					switch (neighborsTags.get(sharedTags.get(i)).owner) { 
					case Tag.NOT_INIT: 

						if (sw.compareTo(getWeight()) > 0) { 
							
							neighborsTags.get(sharedTags.get(i)).owner = Tag.NOT_ME; 
						} else { 
							
							if (D) { 
								log.printf("node %d temporarily owns tag %d \n", 
										this.id, sharedTags.get(i));
							}
							
							neighborsTags.get(sharedTags.get(i)).owner = Tag.ME; 
						}
												
						break; 
						
					case Tag.ME: 
							if (sw.compareTo(getWeight()) > 0) { 
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

		
		
		
		
	}
	
	
	
	
	private RREComValue getWeight() {
		return myWeight;
	}
	

	private void handleStatusInit(Message message) {
		initProtocol();
	}

	@Override
	public boolean isValidStatus(String str) {
		return (str == RREComNode.STAT_INIT || 
				str == RREComNode.STAT_TERMINATE || 
				str == RREComNode.STAT_EX_RX || 
				str == RREComNode.STAT_EX_TX); 
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		return (str == RREComNode.STAT_TERMINATE);
	}
	

	private void terminate() {
		
		changeStatus(RREComNode.STAT_TERMINATE);
	}

}
