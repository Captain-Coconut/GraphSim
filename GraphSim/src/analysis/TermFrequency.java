package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import file.GetFileOperator;

public class TermFrequency {

	public static void getTF(String input) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input);
		String line;
		//clusterID to frequency
		Map <Integer, Integer> map = new HashMap <>();
		while((line = br.readLine()) != null){
			int clusterID = Integer.parseInt(line);
			if(map.containsKey(clusterID)) {
				int freq = map.get(clusterID);
				map.put(clusterID, ++freq);
			} else{
				map.put(clusterID, 1);
			}
		}
		
		List <Integer> sizesList = new ArrayList <Integer> (map.values());
		// cluster size to frequency
		Map <Integer, Integer> map2 = new HashMap <>();
		for(Integer i: sizesList){
			if(map2.containsKey(i)) {
				int freq = map2.get(i);
				map2.put(i, ++freq);
			} else{
				map2.put(i, 1);
			}
		}
		
		MapHelper mh = new MapHelper();
		mh.writeMap("null", map2, input+"_clusterSize");
		
	}
	
	public static void main (String [] args) throws IOException{
		TermFrequency.getTF(args[0]);
	}
}
