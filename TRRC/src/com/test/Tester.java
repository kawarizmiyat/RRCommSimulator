package com.test;

import com.protocol.directbased.DBGraphEntity;

public class Tester {

	
	public Tester() { 
		
		int n = 5; 
		DBGraphEntity[][] arr = new DBGraphEntity[n][n];
		for (int i = 0; i < arr.length; i++) { 
			for (int j = 0; j < arr[i].length; j++) { 
				arr[i][j] = new DBGraphEntity();
			}
		}
		
		System.out.println("hellow");
		
		
	}
	
}

