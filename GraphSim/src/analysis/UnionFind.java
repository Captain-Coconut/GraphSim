package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import file.GetFileOperator;

public class UnionFind {
	
	// pay attention to node 0.... It will always have a self loop
	private int[] id;

	public UnionFind(int N) {
		id = new int[N];
		for (int i = 0; i < N; i++)
			id[i] = i;
	}
	
	// N actually equals to num_of_nodes + 1
	public static void findComponentsUF(String edgeListPath, int N, String componentListPath) throws IOException{
		UnionFind uf = new UnionFind(N);
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(edgeListPath);
		Writer w = gfo.getWriter(componentListPath);
		
		br.readLine();
		String line = new String();
		
		// Do the union based on the graph file
		while((line=br.readLine()) != null){
			String [] lines = line.split(",");
			Integer id1 = Integer.parseInt(lines[0]);
			Integer id2 = Integer.parseInt(lines[1]);
			uf.unite(id1, id2);
		}
		
		// Go through the array again to find root for every node.
		for(int i = 0; i < N; i++){
			uf.id[i] = uf.root(i);
		}
		
		// Put components in a hashmap
		Map <Integer, Set <Integer>> map = new HashMap <>();
		for(int i = 0; i < N; i++){
			Set <Integer> hs = map.containsKey(uf.id[i]) ? map.get(uf.id[i]) : new  HashSet <Integer> ();
			hs.add(i);				
			map.put(uf.id[i], hs);
		}
		
		// write results
		for(Map.Entry<Integer, Set <Integer>> entry : map.entrySet()){
			w.write(entry.getKey()+":"+entry.getValue().toString()+"\n");
		}
		
		
		br.close();
		w.flush();
		w.close();
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
	 * After we get componentList file, find the largest components to write it to file
	 * @param originalGraph: path for original graph
	 * @param ComponentsList: path the componentList file. Format in every line: "107615518:[107615518, 106477144]"
	 */
	public static void getComponentsFiles(String originalGraph, String ComponentsList, String tallyoutput, String output) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br_Graph = gfo.getBR(originalGraph);
		BufferedReader br_Components = gfo.getBR(ComponentsList);
		Writer w = gfo.getWriter(output);
		Writer w_tally = gfo.getWriter(tallyoutput);
		w.write(br_Graph.readLine()+ "\n");
		
		// find the longest line
		String components_line = new String();
		int longest_len = Integer.MIN_VALUE;
		String longest_line = new String();;
		String list = new String();
		while((components_line = br_Components.readLine()) != null){
			
			list = components_line.split(":")[1];
			list = list.substring(1, list.length() - 1);
			int thisLength = list.split(",").length;
			if(thisLength > longest_len) {
				longest_len = thisLength;
				longest_line = list;
			}
			
		}
		w_tally.write("longest length:"+ longest_len + "\n");
		w_tally.write(longest_line);
		
		// Get a hash set that contains IDs for the largest components.
		String [] nodes = longest_line.split(",");
		HashSet <Integer> component = new HashSet <>();
		for(String l : nodes){
			component.add(Integer.parseInt(l.trim()));
		}
		
		String graph_line = new String();
		while((graph_line = br_Graph.readLine()) != null){
			String [] lines = graph_line.split(",");
			int id1 = Integer.parseInt(lines[0]);
			int id2 = Integer.parseInt(lines[1]);
			boolean flag1 = component.contains(id1);
			boolean flag2 = component.contains(id2);
			if( flag1 == true && flag2 == true) w.write(graph_line + "\n");
			else if (flag1 == false && flag2 == false)  {// Do nothing
				}
				else{ // There is error in union find!!
					w_tally.write("False and True Mixed Up! Error in Union Find!!!!");
				}
		}
		
		w_tally.flush();
		w_tally.close();
		w.flush();
		w.close();
	}
	
	public static void tallyComponentSize(String ComponentPath, String output) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(ComponentPath);
		Writer w = gfo.getWriter(output);
		String line = new String();
		int single = 0;
		int two2ten = 0;
		int ten2hundred = 0;
		int hundred2Thousand = 0;
		int thousand2tenk=0;
		int tenk2hundredk = 0;
		
		while((line = br.readLine()) != null){
			String list = line.split(":")[1];
			list = list.substring(1, list.length() - 1);
			int thisSize = list.split(",").length;
			if(thisSize == 1) single++;
			else if(thisSize > 1 && thisSize <= 10) two2ten++;
			else if(thisSize > 10 && thisSize <= 100) ten2hundred++;
			else if(thisSize > 100 && thisSize <= 1000) hundred2Thousand++;
			else if(thisSize > 1000 && thisSize <= 10000) thousand2tenk++;
			else if(thisSize > 10000 && thisSize <= 100000) tenk2hundredk++;
			else w.write("Large Component Found! Size: "+ thisSize);
		}
		w.write("\n size of components\n");
		w.write("1:"+ single + "\n");
		w.write("2-10:"+ two2ten + "\n");
		w.write("10-100:"+ ten2hundred + "\n");
		w.write("100-1000:"+ hundred2Thousand + "\n");
		w.write("1000-10000:"+ thousand2tenk + "\n");
		w.write("10000-100000"+ tenk2hundredk + "\n");

		w.flush();
		w.close();
	}
	
	public static void main (String [] args) throws IOException {
		//UnionFind.findComponentsUF("SVMTotalTrim_Full275_all_edges_R.csv", 117367283 , "SVMFull275_ComponentsList");
		//UnionFind.getComponentsFiles("totalTrim_Full275_all_edges_R.csv", "Full275_ComponentsList", "tallyOutput", "largestComponent");
		// UnionFind.tallyComponentSize("Full275_ComponentsList","ComponentSizeCompact.csv");
		UnionFind.getComponentsFiles("Full275_all_edges_R_noIndex.csv", "SVMFull275_ComponentsList", "tallyOutput", "largestComponent.csv");

	}
}
