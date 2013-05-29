package com.protocol.rrecom;




import java.util.ArrayList;

import com.my.util.MyUtil;
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

		changeStatus(RREComNode.STAT_EX_TX); 
		
		for (int i = 0; i < neighborsNodes.size(); i++) { 
			if (neighborsNodes.get(i).active) { 

				RREComMessageContent mc = prepareMessage(round);
				Message msg = new Message(this.id, neighborsNodes.get(i).id, RREComNode.MSG_EX, mc);
				sendMessage(msg);
				
			}
		}
		
		
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
			
		} if (status == RREComNode.STAT_EX_RX) { 
			handleStatusExRx(message);
		
		} else { 
			
			log.printf("error at node %d. status %s is not recognized \n", this.id, status);
			abort();
		}
		
		
		
	}

	private void handleStatusExRx(Message message) {
		
		if (message.msgType == RREComNode.MSG_EX) { 
			
			RREComMessageContent rmc =  (RREComMessageContent) message.msgContent; 

			if (rmc.type == RREComNode.MSG_TYPE_EX_0) { 
				directEdge(message);
				
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

	private void terminate() {
		changeStatus(RREComNode.STAT_TERMINATE);
	}

	private void makeRedundancyDecision() {
		
		
		// used for more detailed debugginh
		boolean innerD = true;
		
		for (int i = 0; i < neighborsTags.size(); i++) { 
			if (neighborsTags.get(i).owner == Tag.ME) {
				
				if (D && innerD)  { 
					log.printf("node %d owns tag %d \n", 
							this.id, neighborsTags.get(i).id);
				}
				
				this.ownTag(neighborsTags.get(i).id);

			}

		}

		if (this.ownedTags.size() > 0) { 
			if (D) { 
				log.printf("node %d is found non-redundant \n", this.id);
			}
		} else { 
			this.redundant = false;
		}

	}

	// additionalInfo is used to differentiate the sub-statuses.
	private boolean allVisited(int additionalInfo) {
		
		
		if (status == RREComNode.STAT_EX_RX) { 
		
			RREGraphEntity[] t = globalViewer.nodesGraph[this.id];
		
			for (int i = 0; i < t.length; i++) { 
				if (t[i].linkVisited == false) { 
					return false;
				}
			}

			// i.e. all neighbors are visisted. 
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
		
		
		if (! globalViewer.nodesGraph[this.id][sender].linkVisited) { 
		
			globalViewer.nodesGraph[this.id][sender].linkVisited = true;
			globalViewer.nodesGraph[sender][this.id].linkVisited = true;
			
			ArrayList<Integer> sharedTags = MyUtil.interesect(
				globalViewer.neighborsTagsTable.get(sender), 
				globalViewer.neighborsTagsTable.get(this.id) ); 
			
			
		
			if (sharedTags.size() > 0) { 
				
				if (D) { 
					log.printf("node %d share %d tags with node %d",
							this.id, sharedTags.size(), sender);
				}
				
				globalViewer.nodesGraph[this.id][sender].shareTag = true;
				globalViewer.nodesGraph[sender][this.id].shareTag = true;
				
			}		
			
			if (globalViewer.nodesGraph[this.id][sender].shareTag) { 
		
				for (int i = 0; i < sharedTags.size(); i++) { 
					
					switch (neighborsTags.get(sharedTags.get(i)).owner) { 
					case Tag.NOT_INIT: 

						if (sw.compareTo(getWeight()) > 0) { 
							
							neighborsTags.get(sharedTags.get(i)).owner = Tag.NOT_ME; 
						} else { 
							
							if (D) { 
								log.printf("node %d temp owns tag %d \n", 
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
				str == RREComNode.STAT_TERMINATE); 
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		// TODO Auto-generated method stub
		return false;
	}

}
