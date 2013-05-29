package com.simulator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class GraphExtractor {


	// Functions: 
	// Read a given file, create a set of readers with 
	// their neighbor tags. 

	public static ArrayList< ArrayList<Integer> > readFile(String filename) { 


		ArrayList< ArrayList<Integer> > graph = 
				new ArrayList< ArrayList<Integer> >();

		try   {
			FileInputStream in = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int iteration = 0; 
			while((strLine = br.readLine())!= null)
			{
				processLine(strLine, iteration++, graph);
			}

			br.close();
			in.close();

		} catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}



		return graph;	

	}


	public static void printGraph(ArrayList< ArrayList<Integer> > g) { 
		for (int i = 0; i < g.size(); i++) { 
			for (int j = 0; j < g.get(i).size(); j++) { 
				System.out.printf("%d ", g.get(i).get(j));
			}
			System.out.printf("\n");
		}

	}

	private static void processLine(String strLine, int i, 
			ArrayList< ArrayList<Integer> > graph) {



		ArrayList<Integer> vLine = new ArrayList<Integer>();

		Pattern p = Pattern.compile("\\s");
		String tokens[] = p.split(strLine);

		try { 
			for (int ti = 0; ti < tokens.length; ti++) { 
				// System.out.printf("s: %s \n", tokens[ti]); 
				if (! tokens[ti].equals(""))
					vLine.add(new Integer(tokens[ti]));
			}
		} catch (Exception e) { 
			System.out.printf("Apparantly there is an error \n"); 
			e.printStackTrace();
			System.exit(0);
		}
		
		if (i != graph.size()) { 
			System.out.printf("A mismatch between i and " +
					"the graph size !\n");
			System.exit(0);
		}
		graph.add(vLine);

	}

	public static int getTagsSize(ArrayList<ArrayList<Integer>> g) {
		// An easy way to implement this is to find the 
		// maximum id of tags. Here of course, we assume 
		// that the tags are serially identified from 0 to n - 1 . 
		
		int max = -1; 
		for (int i = 0; i < g.size(); i++) { 
			for (int j = 0; j < g.get(i).size(); j++) { 
				if (g.get(i).get(j) > max) { 
					max = g.get(i).get(j);
				}
			}
		}
		
		return max + 1 ;
	}


}
