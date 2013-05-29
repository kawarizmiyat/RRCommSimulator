package com.simulator; 

import java.util.PriorityQueue;


// Note that thsi implementations of the Queue does not
// takes into consideration the time of the event. 
// TODO: use a priorityQueue. 
// TODO: let Event implements Comparable. -- Add a 
// random value in each event, that initiated at the 
// constructor. The Comparable interface needs to use the 
// paired value (time, rand) to sort the elements (events) in 
// priority queue. 
// There is no need to make changes on the names of the functions.
public class EventQueue {

	PriorityQueue<Event> pq; 
	int currentElementId; 

	private boolean D = false;

	public EventQueue() { 
		pq = new PriorityQueue<Event>(); 
		currentElementId = 0;
	}

	public void enter(Event e){

		if (D) { 
			System.out.printf("a new event entered - scheduled at %f \n", e.time);
		}

		e.setId(currentElementId); 
		e.setRandomBreaker();
		currentElementId ++;
		pq.add(e);

	}

	public boolean isEmpty(){return pq.isEmpty(); }
	public int length() {return pq.size();}

	public Event getNext(){
		return pq.poll();
	}

	public int getNbEvents() {
		return length();
	}

}

