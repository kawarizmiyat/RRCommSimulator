package com.test;

import com.protocol.rrecom.RREGraphEntity;

public class Tester {

	
	public Tester() { 
		
		int n = 5; 
		RREGraphEntity[][] arr = new RREGraphEntity[n][n];
		for (int i = 0; i < arr.length; i++) { 
			for (int j = 0; j < arr[i].length; j++) { 
				arr[i][j] = new RREGraphEntity();
			}
		}
		
		System.out.println("hellow");
		
		
	}
	
}

