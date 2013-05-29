 package com.simulator; 

import java.util.Random;



public class Event implements Comparable<Event> {
	

	public double time;
	public double randomBreaker;
	public int id;
	
	public int action;
	public double totalDelay;
	
	// The message contains to which node this event is directed.
	public Message message;

	public void setId(int currentElementId) {
		id = currentElementId;
	} 

	
	public void setRandomBreaker() { 
		randomBreaker = new Random().nextDouble();
	}


	@Override
	public int compareTo(Event o) {
		double ot = o.time; 
		double or = o.randomBreaker;
		
		if (this.time > ot || 
				this.time == ot && (this.randomBreaker > or)) { 
			return 1; 
		}
		
		if (this.time == ot && this.randomBreaker == or) { 
			return 0; 
		}

		return -1; 
	}
	
	
}
