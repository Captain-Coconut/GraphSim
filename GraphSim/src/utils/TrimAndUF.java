package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import file.GetFileOperator;

public class TrimAndUF {
	
	private int[] id;

	public TrimAndUF(int N) {
		id = new int[N];
		for (int i = 0; i < N; i++)
			id[i] = i;
	}
	
	public int root(int i) {
		while (i != id[i]) {
			id[i] = id[id[i]]; // compression technique
			i = id[i];
		}
		return i;
	}

	public boolean find(int p, int q) {
		return root(p) == root(q);
	}

	public void unite(int p, int q) {
		int i = root(p);
		int j = root(q);
		id[i] = j;
	}
	
	/**
	 * Trim the graph, eliminate edges with low weight.
	 * It can be combined with union find.
	 * Here I wrote them separately so we can save the intermediate results
	 * @param graph: path for the original graph
	 * @param weightIndex: column index for the weight
	 * @param newGraph: path for the new compressed graph ()
	 * @param SVM_b: SVM weights
	 * @throws IOException
	 */
	public static void trim(String graph, int weightIndex, String newGraph, double SVM_b) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(graph);
		Writer w = gfo.getWriter(newGraph);
		
		//Write the header
		w.write(br.readLine() + "\n");
		String line;
		
		while((line = br.readLine()) != null){
			if(Double.parseDouble(line.split(",")[weightIndex]) > SVM_b) w.write(line + "\n");
		}
		
		br.close();
		w.flush();
		w.close();
	}
	
	/**
	 * Do a union find
	 * @param edgeListPath
	 * @param N
	 * @param componentListPath
	 * @throws IOException 
	 */
	public static void uf(String edgeListPath, int N, String componentListPath) throws IOException{
		TrimAndUF uf = new TrimAndUF(N);
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(edgeListPath);
		Writer w = gfo.getWriter(componentListPath);
		
		br.readLine();
		String line = new String();
		
		// Do the union based on the graph file
		while((line = br.readLine()) != null){
			String [] lines = line.split(",");
			Integer id1 = Integer.parseInt(lines[1]);
			Integer id2 = Integer.parseInt(lines[2]);
			uf.unite(id1, id2);
		}
		
		// Go through the array again to find root for every node.
		for(int i = 0; i < N; i++){
			uf.id[i] = uf.root(i);
		}
		
		// Put components in a HashMap
		Map <Integer, Set <Integer>> map = new HashMap <>();
		for(int i = 0; i < N; i++){
			Set <Integer> hs = map.containsKey(uf.id[i]) ? map.get(uf.id[i]) : new  HashSet <Integer> ();
			hs.add(i);				
			map.put(uf.id[i], hs);
		}
		
		// Write results to a file
		for(Map.Entry<Integer, Set <Integer>> entry : map.entrySet()){
			w.write(entry.getKey()+":"+entry.getValue().toString()+"\n");
		}
		
		br.close();
		w.flush();
		w.close();
	}
	
	public static void main(String [] args) throws IOException{
		
		String graph = args[0];
		int weightIndex = Integer.parseInt(args[1]);
		String newGraph = graph + "java_SVM_Trim.csv";
		double SVM_b = Double.parseDouble(args[2]);
		trim(graph, weightIndex, newGraph, SVM_b);
		
		int N = Integer.parseInt(args[3]);
		String componentListPath = args[4];
		uf(newGraph, N, componentListPath);

	}

}
