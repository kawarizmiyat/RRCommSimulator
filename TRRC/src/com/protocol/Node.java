package com.protocol;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.simulator.Message;
import com.simulator.Event;
import com.simulator.EventType;
import com.simulator.SimSystem;

public abstract class Node {

	public SimSystem sim; 
	public int id, weight; 
	
	public HashMap<Integer, Tag> neighborsTags; 
	public HashMap<Integer, Neighbor> neighborsNodes;
	
	public int numNeighborTags; 
	public int numNeighborReaders;
	public int round;
	
	public boolean redundant = false;
	
	// Variables needed to any Event handler in this simulation
	protected double now;
	protected PrintStream log = System.out; 
	private boolean D = false;
	
	public String status; 
	public ArrayList<Integer> ownedTags;
	
	
	public static final String MSG_INIT = "MSG_INIT";

	
	public Node(SimSystem sim, int id) { 
		this.sim = sim; 
		this.id = id; 
		this.weight = 0;
		neighborsTags = new HashMap<Integer, Tag>();
		neighborsNodes = new HashMap<Integer, Neighbor>();
		numNeighborTags= 0;
		numNeighborReaders = 0;
		ownedTags = new ArrayList<Integer>();
		round = 0;
	}

	
	
	public void handleEvent(Event e) {
		
		if (D) { 
			log.printf("reader %d handling event at time %f \n", this.id, e.time);
		}
		
		if (e.time < now) { 
			log.printf("error at reader %d: event in past at handleEvent ",
					this.id);
		}
		
		updateTimer(e.time);
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			if (D) { 
				log.printf("reader %d is goint to handle received message \n", this.id);
			}
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			
			log.printf("error at reader %d: Only messages are allowed in handleEvent", 
					this.id);
			System.exit(0);
			break; 
		}
			

	}

	
	protected abstract void initProtocol() ;
	protected abstract void handleReceivedMessage(Message message);
	public abstract boolean isValidStatus(String str);
	public abstract boolean isTerminatedStatus(String str) ;
	
	protected  void updateTimer(double d) { 
		if (d < now) { 
			log.printf("error at reader %d: event in past at updateTimer ",
					this.id);
		}
		now = d;
	}
	
	protected void sendMessage(Message msg) { 
		
		if (D) { 
			

			
			log.printf("node %d sends message to node %d \n",  
					msg.senderId, 
					msg.receiverId);
		}
		
		Event e = new Event(); 
		e.action = EventType.MESSAGE; 
		e.message = msg; 
		e.time = now + msgDelay();
		
		sim.future.enter(e);
	}
	
	// there should be a better implementation to this function
	protected void scheduleTimer(double d, String timerType) {
		
		
		if (D) { 
			log.printf(
					"reader %d schedules a timer of type %s for %f time slots \n", 
					this.id, timerType, d);
		}
		
		// Message m  = new Message(this.id, this.id, timerType, null, 'r', 'r');
		
		Message m = new Message(this.id, this.id, timerType, null);
		
		Event e = new Event(); 
		e.action = EventType.MESSAGE; 
		e.time = now + d; 
		e.message = m;
		sim.future.enter(e);
		
	}

	
	protected double msgDelay() { 
		return 1.0;
	}
	
	
	// I dont like these two functions. 
	public void addNodeNeighbor(int i) {
		
		neighborsNodes.put(i, new Neighbor(i));
		numNeighborReaders ++;
	}

	public void addTagNeighbor(int i) { 
		neighborsTags.put(i, new Tag(i));
		numNeighborTags ++; 
		
	}
	
	protected void changeStatus(String str) {
		
		if (D) { 
			log.printf("at change status to %s \n", str);
		}
		
		if (isValidStatus(str)) { 
			
			if (D) { 
				log.printf("reader %d changed status from: %s " +
						" to %s \n", this.id, this.status, str);
			}
			
			
			status = str; 
			if (isTerminatedStatus(str)) { 
				log.printf("reader %d terminated at %f \n",
						this.id, this.now);
			}
		} else { 
			log.printf("error at reader %d: " +
					"status %s is not a valid state \n", this.id, str);
			System.exit(0);
		}
 	}

	public boolean isTerminated() {
		return isTerminatedStatus(status);
	}
	
	public boolean ownTag(int tag) {
		
		if (D) { 
			log.printf("reader %d owns tag %d \n", 
					this.id, tag);
		}
		
		for (int j = 0; j < ownedTags.size(); j++) { 
			if (ownedTags.get(j) == tag) { 
				log.printf("error: reader %d cannot own tag %d twice !", this.id, tag);
				return false;
			}
		}
		
		ownedTags.add(tag);
		return true;
	}

	public void abort() { 
		log.printf("program aborted. \n"); 
		System.exit(0);
	}
	
}
