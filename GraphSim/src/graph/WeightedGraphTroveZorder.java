package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import file.GetFileOperator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

// This is the best implementation so far
public class WeightedGraphTroveZorder {
	
	// I decide to construct the graph in a different way.
	// It is a hashmap, Integer: List
	// 1: 2 - 45 - 3 - 67
	// meaning node 1 has two neighbors, 2 and 3, the weight is 0.45*100, 0.67*100.
	
	public TIntObjectMap <TIntList> map = new TIntObjectHashMap<>();
	public int MAX_ID = Integer.MIN_VALUE;
	public int MIN_ID = Integer.MAX_VALUE;
	public int numsofnodes=0;
	public int numsofedges=0;
	public int amplifier = 1;

	/**
	 * Construct a weighted graph from a local file USING TROVE
	 * file format: id1,id2,weight 
	 * @param filepath
	 * @param Tracker: We write the number of lines we have read to a file to track the progress
	 * @param amplifier: to make the weight an integer, suppose 1000.
	 * @throws IOException
	 */
	public WeightedGraphTroveZorder(String filepath, String Tracker,  int amplifier) throws IOException{
		this.amplifier = amplifier;
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		//Get rid of header
		br.readLine();
		int countLines = 0;
		Writer w = gfo.getWriter(Tracker);
		String [] lines;
		int id1,id2;
		int weight;
		while((line = br.readLine()) != null){
			
			countLines++;
			lines = line.split(",");
			id1 = Integer.parseInt(lines[0]);
			id2 = Integer.parseInt(lines[1]);
			weight = Math.round(Float.parseFloat(lines[2])*amplifier);
			
			MAX_ID = MAX_ID > Math.max(id1, id2)? MAX_ID: Math.max(id1, id2);
			MIN_ID = MIN_ID < Math.min(id1, id2)? MIN_ID: Math.min(id1, id2);
			
			TIntList th1 = map.containsKey(id1) ? map.get(id1) : new TIntArrayList();
			th1.add(id2); // this is neighbor
			th1.add(weight); // this is weight*amplifier
			map.put(id1, th1);

			TIntList th2 = map.containsKey(id2) ? map.get(id2) : new TIntArrayList();
			th2.add(id1); // this is neighbor
			th2.add(weight); // this is weight*amplifier
			map.put(id2, th2);
			
			w.write(countLines+"\n");

		}
		
		this.numsofnodes = map.size();
		this.numsofedges = countLines;
		br.close();
		w.flush();w.close();
	}
	
	public static void main (String [] args) throws IOException{
		// Check the graph files.
		// Lines of edges.
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter("NodesAndEdges");
		WeightedGraphTroveZorder wgtz = new WeightedGraphTroveZorder("largestComponent_ConsecutiveID", "Tracker", 1000);
		TIntObjectMap <TIntList> map = wgtz.map;
		System.out.println("number of nodes:" + wgtz.numsofnodes);
		System.out.println("number of edges (based on lines in edge files):" + wgtz.numsofedges);
		System.out.println(wgtz.MIN_ID);
		System.out.println(wgtz.MAX_ID);
		System.out.println("calculating number of edges based on the map:");
		long countEdges = 0; 
		long countNodes = 0;
		int [] keys = map.keys();
		//TIntObjectIterator toi = map.iterator();
		//TIntIterator it = map.keySet().iterator();
		int degree;
		w.write("nodeID, Degree \n");
		for(int key: keys){
			countNodes++;
			// remember to divide by 2
			degree = map.get(key).size()/2;
			countEdges += degree;
			w.write(key + "," + degree + "\n");
		}
		System.out.println("Nodes:" + countNodes + "    Edges:" + countEdges/2);
		w.flush();
		w.close();
	}

}
