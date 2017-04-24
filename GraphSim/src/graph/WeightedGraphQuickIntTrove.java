package graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import file.GetFileOperator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.list.array.TIntArrayList;

public class WeightedGraphQuickIntTrove {
	
	// 1: [2,3,4,5] means for node 1. we have node 2 and node 4 as neighbors and the weight is 3 and 5 respectively.
	TIntObjectMap <TIntArrayList> tmap = new TIntObjectHashMap <TIntArrayList> ();
	public int numOfNodes;
	public int numOfEdges;
		
	public WeightedGraphQuickIntTrove(String filepath) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line = new String();
		Writer w = gfo.getWriter("Tracker.progress");
		//Get rid of header
		br.readLine();
		int countLines = 0;
		String[] lines;
		while((line = br.readLine()) != null){
			countLines++;
			
			lines = line.split(",");
			int id1 = Integer.parseInt(lines[0]);
			int id2 = Integer.parseInt(lines[1]);
			int weight = Integer.parseInt(lines[2]);
			
			TIntArrayList l1 = tmap.containsKey(id1) ? tmap.get(id1) : new TIntArrayList();
			l1.add(id2);
			l1.add(weight);
			
			
			// It is an undirected graph so we need to add id2, id1 as well
			TIntArrayList l2 = tmap.containsKey(id2) ? tmap.get(id2) : new TIntArrayList();
			l2.add(id1);
			l2.add(weight);
			tmap.put(id2, l2);
			w.write(countLines+"\n");
		}
		
		this.numOfEdges = countLines;
		this.numOfNodes = tmap.size();
		br.close();
		
	}
	
	public static void main(String [] args){
		System.out.println("testing Trove....");
//		System.out.println("testing StringBuilder....");
		StringBuilder sb = new StringBuilder ();
//		String s = "12,12,32";
//		String [] ss = s.split(",");
//		System.out.println(ss[0]);
//		String snew= "23,2,3";
//		String [] ssnew = snew.split(",");
//		System.out.println(ssnew[0]);
		TIntObjectMap <TIntArrayList> maptest = new TIntObjectHashMap <TIntArrayList> ();
		TIntArrayList tl1= new TIntArrayList();
		tl1.add(1); tl1.add(2);
		maptest.put(1, tl1);
		System.out.println(maptest);
		
		TIntArrayList tl2= new TIntArrayList();
		tl2.add(11); tl2.add(22);
		maptest.put(2, tl2);
		System.out.println(maptest);

		Double d = 5.95;
		Integer i = d.intValue(); // i becomes 5
		System.out.println(i);

		 System.out.println(Math.round(1.15));

		
	}
		

}
