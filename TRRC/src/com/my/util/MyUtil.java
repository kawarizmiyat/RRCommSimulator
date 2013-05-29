package com.my.util;

import java.util.ArrayList;

public class MyUtil {

	

	public  static ArrayList<Integer> interesect(ArrayList<Integer> f, ArrayList<Integer> s) { 
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		int i = 0, j = 0; 
		while (i != f.size() && j != s.size()) { 
			

			
			if (f.get(i) < s.get(j)) {
				i ++;
			} else if (f.get(i) > s.get(j)) { 
				j ++;
			} else { 
				
				res.add(f.get(i)); 
				i ++;  j ++;
			}
		}
		
		
		return res; 
	}

	public static ArrayList<Integer> setDifference(ArrayList<Integer> f, ArrayList<Integer> s) {

		ArrayList<Integer> result = new ArrayList<Integer>(); 
		
		int i = 0, j = 0; 
		while (i != f.size() && j != s.size()) { 
			
			if (f.get(i) < s.get(j)) { 
				result.add(f.get(i)); i ++; 
			} else if (f.get(i) > s.get(j)) { 
				j ++; 
			} else { 
				i ++; j++;
			}
			
		}
		
		// 
		// f.clear();
		// f.addAll(result);
		return result;
		
	}
	
	
	public static ArrayList<Integer> setSymmetricDifference(ArrayList<Integer> f, ArrayList<Integer> s) { 
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		int i = 0, j = 0; 
		while (true) { 
			if (i == f.size()) { copy(s, j, result); return result; } 
			if (j == s.size()) { copy(f, i, result); return result; }
			
			if (f.get(i) < s.get(j)) { 
				result.add(f.get(i));
				i ++;
			
			} else if (s.get(j) < f.get(i)) { 
				result.add(s.get(j)); 
				j ++;
			}
			
			
		}
		
	}
	
	private static void copy(ArrayList<Integer> s, int j,
			ArrayList<Integer> result) {

		for (int k = j; k < s.size(); k++) 
			result.add(s.get(k));
		
		
	}
	
//	  while (true)
//	  {
//	    if (first1==last1) return std::copy(first2,last2,result);
//	    if (first2==last2) return std::copy(first1,last1,result);
//
//	    if (*first1<*first2) { *result=*first1; ++result; ++first1; }
//	    else if (*first2<*first1) { *result = *first2; ++result; ++first2; }
//	    else { ++first1; ++first2; }
//	  }



	public static String toString(ArrayList list) {

		String str = "["; 
		for (int i = 0; i < list.size(); i ++ ) { 
			str += list.get(i).toString(); 
			if (i < list.size() - 1 ) { 
				str += ", ";
			}
		}
		str += "]";
		return str; 
	}

	
	
}
