package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import file.GetFileOperator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class CorrelationGraph {
	
	public TIntObjectMap<TIntList> map = new TIntObjectHashMap<>();
	public int MAX_ID = Integer.MIN_VALUE;
	public int MIN_ID = Integer.MAX_VALUE;
	public int numsofnodes = 0;
	public int numsofedges = 0;
	public int pedges = 0;
	public double [] svm_w; 	// Don't need it yet
	
	public int pflag = 0;
	public int nflag = 0;

	public CorrelationGraph(String filepath, double svm_b, int pflag, int nflag) throws IOException{
		if(pflag > 0 || nflag > 0) System.err.println("pflag and nflag have to be negative");
		this.pflag = pflag;
		this.nflag = nflag;
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		//Get rid of header
		br.readLine();
		int countLines = 0;
		String [] lines;
		int id1,id2;
		double weight;

		// If correlation = -1, the edge is +, if -2, the edge is -
		int correlation;
		
		while((line = br.readLine()) != null){
			
			countLines++;
			lines = line.split(",");
			id1 = Integer.parseInt(lines[0]);
			id2 = Integer.parseInt(lines[1]);
			weight = Double.parseDouble(lines[2]);
			if(weight >= svm_b) {
				correlation = pflag; // the edge is '+'
				this.pedges ++;
			}
			else correlation = nflag; // the edge is '-'
			
			MAX_ID = MAX_ID > Math.max(id1, id2)? MAX_ID: Math.max(id1, id2);
			MIN_ID = MIN_ID < Math.min(id1, id2)? MIN_ID: Math.min(id1, id2);
			
			TIntList th1 = map.containsKey(id1) ? map.get(id1) : new TIntArrayList();
			th1.add(id2); // this is neighbor
			th1.add(correlation); // this is edge indicator
			map.put(id1, th1);

			TIntList th2 = map.containsKey(id2) ? map.get(id2) : new TIntArrayList();
			th2.add(id1); // this is neighbor
			th2.add(correlation); // this is edge indicator
			map.put(id2, th2);
			
		}
		
		this.numsofnodes = map.size();
		this.numsofedges = countLines;
		br.close();		
	}
	
	public static void main(String [] args) throws IOException, IOException{
		CorrelationGraph cg = new CorrelationGraph(args[0], Double.parseDouble(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));  
		System.out.println("MIN_ID:" + cg.MIN_ID);
		System.out.println("MAX_ID:" + cg.MAX_ID);
		System.out.println("numsofnodes:" + cg.numsofnodes);
		System.out.println("numsofedges:" + cg.numsofedges);
		System.out.println("pedges:" + cg.pedges);

	}

}
