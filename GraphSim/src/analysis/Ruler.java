package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import file.GetFileOperator;
import graph.UnweightedGraph;

public class Ruler {
	
	/**
	 * Get a cluster surrouding a starting node
	 * @param graph
	 * @param start: the starting node of BFS
	 * @param MAX: used only if we need to limit the size of the cluster
	 * @return
	 */
	public static Set <Integer> getBFSCluster(Map <Integer, Set <Integer>> graph, int start, int MAX){
		Set <Integer> visited = new HashSet <>();
		Queue <Integer> queue = new LinkedList <>();
		queue.add(start);
		
		while(visited.size() <= MAX && visited.size() != graph.size()){
			Queue <Integer> tmp = new LinkedList <>();
			while(!queue.isEmpty()){
				int v = queue.poll();
				tmp.add(v);
				if(visited.size() == MAX) return visited;
				visited.add(v);
				System.out.println(visited);
			}
			System.out.println("here");
			while(!tmp.isEmpty()){
				Set <Integer> neighbors = graph.get(tmp.poll());
				System.out.println(neighbors);
				if(neighbors == null) continue;
				for(int i: neighbors){
					if(visited.size() == MAX) return visited;
					if(visited.contains(i)){ // We have visited this node
					}
					else{ // if we haven't, we put this node to the queue
						queue.add(i);
					}
				}
			}
		}		
		return visited;
		
	}
	
	/** The right one
	 * 
	 * @param graph
	 * @param s
	 * @param MAX
	 * @return
	 */
    public static Set<Integer> BFS(Map <Integer, Set <Integer>> graph, int s, int MAX)
    {
        Set <Integer> visited = new HashSet <Integer>();
        Set <Integer> res = new HashSet <>();
        
        LinkedList<Integer> queue = new LinkedList<Integer>();
 
        visited.add(s);
        queue.add(s);
 
        while (queue.size() != 0)
        {
            s = queue.poll();
            if(res.size() == MAX) return res;
            res.add(s);
 
            Set <Integer> neighbors = graph.get(s);
            if(neighbors == null || neighbors.size() == 0) continue;
            Iterator<Integer> i = graph.get(s).iterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited.contains(n))
                {
                    visited.add(n);
                    queue.add(n);
                }
            }
        }
        
        return res;
    }
	
	
	/**
	 * Get the conductance given an UNWEIGHTED graph, and a subgraph in it
	 * @param ug
	 * @param cluster: id sets of the subgraph
	 * @return
	 */
	public static double getConductanceUnweighted(UnweightedGraph ug, Set <Integer> cluster){
		
		int countwithinEdges = 0;
		for(int i: cluster){
			Set <Integer> neighbours = ug.map.get(i);
			if(neighbours == null || neighbours.size() == 0) continue;
			for(int j: neighbours){
				if(cluster.contains(j)) {
					// System.out.println("edge" + i + "----" + j);
					countwithinEdges++;
				}
			}
		}
		countwithinEdges = countwithinEdges/2;
		System.out.println(countwithinEdges);
		
		int countCuts = 0;
		for(int i: cluster){
			Set <Integer> neighbours = ug.map.get(i);
			if(neighbours == null || neighbours.size() == 0) continue;
			for(int j: neighbours){
				if(!cluster.contains(j)) countCuts++;
			}
		}
		
		System.out.println(countCuts);
		
		// return error message
		if(countwithinEdges == 0) return Double.MAX_VALUE; 
		
		return 1.0*countCuts/countwithinEdges;
	}
	
	public static void compareMetisBFS(String metisOutputPath, String graphPath, int maxIteration, String analysisPath) throws IOException{
		UnweightedGraph ug = new UnweightedGraph(graphPath,0);
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(analysisPath);
		Writer w2 = gfo.getWriter("compareExcel");
		
		// record the clustering in a hashmap
		// key: cluster ID, Value: a set of node IDs in that cluster
		Map <Integer, Set<Integer>> clusterMap = new HashMap <>();
		BufferedReader br = gfo.getBR(metisOutputPath);
		String line = new String();
		int nodeID = 1;
		
		while((line = br.readLine()) != null){
			int ClusterID = Integer.parseInt(line);
			Set <Integer> s = (Set<Integer>) (clusterMap.containsKey(ClusterID) ? clusterMap.get(ClusterID) : new HashSet<>());
			s.add(nodeID++);
			clusterMap.put(ClusterID, s);
		}
	
		int iteration = 0;
		w2.write("metisConduct,BFSConduct" + "\n");
		for(Map.Entry<Integer, Set<Integer>> entry : clusterMap.entrySet()){
			System.out.println("-----------------");
			if(iteration++ > maxIteration) break;
			
			// this is the cluster made by metis
			Set <Integer> metisCluster = entry.getValue();
			// calculate conductance of this clustering
			double metisConduct = Ruler.getConductanceUnweighted(ug, metisCluster);
			w.write("Metis Cluster ID:"+ entry.getKey() + "  contains nodes:" + metisCluster + "   conduct:" + metisConduct+"\n");
			
			
			// this is the cluster made by BFS starting from a random node in the metis cluster
			Set <Integer> BFSCluster = Ruler.BFS(ug.map, metisCluster.iterator().next(), metisCluster.size());			
			double BFSConduct = Ruler.getConductanceUnweighted(ug, BFSCluster);
			w.write("BFS Cluster ID:"+ entry.getKey() + "  contains nodes:" + BFSCluster + "   conduct:" + BFSConduct +"\n");
			
			w2.write(metisConduct+ "," + BFSConduct + "\n");
			
			
		}
		w.flush();
		w.close();
		br.close();
		w2.flush();
		w2.close();
	}
	
	public static void main(String [] args) throws IOException{
		Map <Integer, Set <Integer>> graph = new HashMap<>();
		
		graph.put(6, new HashSet<Integer> (Arrays.asList(8)));
		graph.put(7, new HashSet<Integer> (Arrays.asList(8)));
		graph.put(8, new HashSet<Integer> (Arrays.asList(6,7,9,10)));
		graph.put(9, new HashSet<Integer> (Arrays.asList(8,12)));
		graph.put(10, new HashSet<Integer> (Arrays.asList(8,13)));
		graph.put(12, new HashSet<Integer> (Arrays.asList(9)));
		graph.put(13, new HashSet<Integer> (Arrays.asList(10)));
		
		//UnweightedGraph ug = new UnweightedGraph (graph);
		//System.out.println(Ruler.BFS(graph, 8, 5));
		//System.out.println(Ruler.getConductanceUnweighted(ug, new HashSet <>(Arrays.asList(6,7,8))));
		Ruler.compareMetisBFS("trimMetisNEW.graph.part.100000", "largestTrim.csv", 500, "metisAnalysis");
	}
	
}
