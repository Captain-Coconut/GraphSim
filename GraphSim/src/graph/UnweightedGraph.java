package graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import file.GetFileOperator;

public class UnweightedGraph {
	
	/*We store the deterministic graph in ajacent list format*/
	/*We used SET in order to eliminate abnormal dataset*/
	public Map <Integer, Set <Integer>> map = new HashMap<>();
	// Node id starting from 0;
	public int MAX_ID = 0;
	public int MIN_ID = 0;
	public int numsofnodes=0;
	public int numsofedges=0;
	
	/**
	 * Construct a graph from a local file
	 * file format: id1	id2 
	 * We don't consider weights now so it doesn't matter what comes in the third column
	 * if flag = 0, this is undirected!! If flag = 1, this is directed.
	 * @param filepath
	 * @throws IOException
	 */
	public UnweightedGraph(String filepath, int flag) throws IOException{
		
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
			MAX_ID = MAX_ID > Math.max(id1, id2)? MAX_ID: Math.max(id1, id2);
			Set <Integer> s1 = (Set<Integer>) (map.containsKey(id1) ? map.get(id1) : new HashSet<>());
			s1.add(id2);
			map.put(id1, s1);
			
			// we also need to put id2,id1 in the graph if this is undirected.
			if(flag == 0){
				Set <Integer> s2 = (Set<Integer>) (map.containsKey(id2) ? map.get(id2) : new HashSet<>());
				s2.add(id1);
				map.put(id2, s2);
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
	
	//Another Constructor
	public UnweightedGraph(Map <Integer, Set <Integer>> graph ) throws IOException{
		this.map = graph;
		this.numsofnodes = graph.size();
		int countEdges = 0;
		for(Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()){
			countEdges += entry.getValue().size();
		}
		this.numsofedges = countEdges;
	}
	
	
	public void printMapofGraph(UnweightedGraph g){
		Map <Integer, Set <Integer>> map = this.map;
		for(Map.Entry<Integer, Set<Integer>> entry: map.entrySet()){
			System.out.print(entry.getKey());
			StringBuilder sb = new StringBuilder();
			for(Integer i : entry.getValue()) sb.append(","+i);
			System.out.println(sb);
		}
	}
	
	public void checkMapofGraph(String output) throws IOException{
		Map <Integer, Set <Integer>> map = this.map;
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		for(Map.Entry<Integer, Set<Integer>> entry: map.entrySet()){
			w.write(entry.getKey() +": " + entry.getValue().toString() +"\n");
		}
		w.flush();
		w.close();
	}
	
	/**
	 * Get a list of sampled worlds(size: number) from an uncertain graph ug
	 * @param ug
	 * @param number
	 * @return
	 */
	//TODO
	public List <UnweightedGraph> getSampledWorlds(UncertainGraph ug, int number){
		return null;
		
	}
	
	public static void main(String [] args) throws IOException{
		UnweightedGraph g = new UnweightedGraph("all_edges_deID_OnlyIDandTotal.csv",0);
		System.out.println("num_nodes:"+g.map.size() + " MAX_ID:" + g.MAX_ID);
		// Write this to a file to check
		g.checkMapofGraph("checkGraphMap.csv");
	}
	
}
