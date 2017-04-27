package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import file.GetFileOperator;

public class WeightedGraph {
	
	public Map <Integer, HashMap <Integer, Double>> map = new HashMap<>();
	public int MAX_ID = 0;
	public int MIN_ID = 0;
	public int numsofnodes=0;
	public int numsofedges=0;

	/**
	 * Construct a weighted graph from a local file
	 * file format: id1,id2,weight 
	 * We don't consider weights now so it doesn't matter what comes in the third column
	 * if flag = 0, this is undirected!! If flag = 1, this is directed.
	 * @param filepath
	 * @throws IOException
	 */
	public WeightedGraph(String filepath, int flag) throws IOException{
		
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		//Get rid of header
		br.readLine();
		int countLines = 0;
		while((line = br.readLine()) != null){
			
			countLines++;
			String [] lines = line.split(",");
			int id1 = Integer.parseInt(lines[0]);
			int id2 = Integer.parseInt(lines[1]);
			double weight = Double.parseDouble(lines[2]);
			
			MAX_ID = MAX_ID > Math.max(id1, id2)? MAX_ID: Math.max(id1, id2);
			
			//Map <Integer, HashMap <Integer, Double>> map
			HashMap <Integer, Double> h1 = (HashMap <Integer, Double>) (map.containsKey(id1) ? map.get(id1) : new HashMap <Integer, Double>());
			h1.put(id2, weight);
			map.put(id1, h1);
			
			// we also need to put id2,id1 in the graph if this is undirected.
			if(flag == 0){
				
				HashMap <Integer, Double> h2 = (HashMap <Integer, Double>) (map.containsKey(id2) ? map.get(id2) : new HashMap <Integer, Double>());
				h2.put(id1, weight);
				map.put(id2, h2);
			}
		}
		
		this.numsofnodes = map.size();
		
		int edgeNum = 0;
		//count the number of edges in a different way
		Iterator <Integer> it = map.keySet().iterator();
		while(it.hasNext()){
			edgeNum += map.get(it.next()).size();
		}
		
		this.numsofedges = edgeNum/2;
		br.close();
	}
}
