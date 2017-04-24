package graph;

import java.util.HashMap;
import java.util.Map;

import file.GetFileOperator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

// Replaced with WeightedGraphQuickIntTrove
public class WeightedGraphQuickInt {

	// 1: [2,3,4,5] means for node 1. we have node 2 and node 4 as neighbors and the weight is 3 and 5 respectively.
	
	public Map <Integer, List<Integer>> map = new HashMap<>(2000000000);
	public int numOfNodes;
	public int numOfEdges;
		
	public WeightedGraphQuickInt(String filepath) throws IOException{
		
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line = new String();
		Writer w = gfo.getWriter("Tracker.progress");
		//Get rid of header
		br.readLine();
		int countLines = 0;
		while((line = br.readLine()) != null){
			countLines++;
			String[] lines = line.split(",");
			int id1 = Integer.parseInt(lines[0]);
			int id2 = Integer.parseInt(lines[1]);
			int weight = Integer.parseInt(lines[2]);
			
			List <Integer> l1 = map.containsKey(id1) ? map.get(id1) : new ArrayList<Integer>();
			l1.add(id2);
			l1.add(weight);
			map.put(id1, l1);
			
			// It is an undirected graph so we need to add id2, id1 as well
			List <Integer> l2 = map.containsKey(id2) ? map.get(id2) : new ArrayList<Integer>();
			l2.add(id1);
			l2.add(weight);
			map.put(id2, l2);
			w.write(countLines+"\n");
		}
		
		this.numOfEdges = countLines;
		this.numOfNodes = map.size();
		br.close();
	}
}
