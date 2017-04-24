package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import file.GetFileOperator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.*;

public class WeightedGraphTrove {
	
	public TIntObjectMap <TIntFloatMap> map = new TIntObjectHashMap<>();
	public int MAX_ID = Integer.MIN_VALUE;
	public int MIN_ID = Integer.MAX_VALUE;
	public int numsofnodes=0;
	public int numsofedges=0;


	/**
	 * Construct a weighted graph from a local file USING TROVE
	 * file format: id1,id2,weight 
	 * @param filepath
	 * @param Tracker: We write the number of lines we have read to a file to track the progress
	 * @throws IOException
	 */
	public WeightedGraphTrove(String filepath, String Tracker) throws IOException{
		
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		//Get rid of header
		br.readLine();
		int countLines = 0;
		Writer w = gfo.getWriter("Tracker.progress");
		String [] lines;
		int id1,id2;
		float weight;
		while((line = br.readLine()) != null){
			
			countLines++;
			lines = line.split(",");
			id1 = Integer.parseInt(lines[0]);
			id2 = Integer.parseInt(lines[1]);
			weight = Float.parseFloat(lines[2]);
			
			MAX_ID = MAX_ID > Math.max(id1, id2)? MAX_ID: Math.max(id1, id2);
			MIN_ID = MIN_ID < Math.min(id1, id2)? MIN_ID: Math.min(id1, id2);
			
			TIntFloatMap th1 = map.containsKey(id1) ? map.get(id1) : new TIntFloatHashMap();
			th1.put(id2, weight);
			map.put(id1, th1);

			TIntFloatMap th2 = map.containsKey(id2) ? map.get(id2) : new TIntFloatHashMap();
			th2.put(id1, weight);
			map.put(id2, th2);
			
			w.write(countLines+"\n");

		}
		
		this.numsofnodes = map.size();
		this.numsofedges = countLines;
		br.close();
		w.flush();w.close();
	}
}
