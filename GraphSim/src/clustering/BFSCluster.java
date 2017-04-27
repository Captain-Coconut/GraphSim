package clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import graph.UnweightedGraphTrove;
import graph.WeightedUndirectedGraph;

public class BFSCluster {

	/**
	 * Give a graph, a starting node, return the BFS track in limited steps.
	 * Possible memory problem with naive linkedlist
	 * @param UnweightedGraphTrove us: the graph
	 * @param s: the starting node
	 * @param MAX: the maximum size of the cluster
	 * @return
	 */	
	 public static TIntList BFS(UnweightedGraphTrove ug, int s, int MAX)
	    {
			TIntObjectMap<TIntList> map = ug.map;

			TIntList visited = new TIntArrayList ();
	        TIntList res = new TIntArrayList ();
	        
	        LinkedList<Integer> queue = new LinkedList<Integer>();
	 
	        visited.add(s);
	        queue.add(s);
	 
	        while (queue.size() != 0)
	        {
	        	//System.out.println(queue);
	            s = queue.poll();
	            if(res.size() == MAX) return res;
	            res.add(s);
	 
	            TIntList neighbors = map.get(s);
	            if(neighbors == null || neighbors.size() == 0) continue;
	            TIntIterator i = neighbors.iterator();
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
	
		/** BFS on weighted graph --> simply treat it as an unweighted graph
		 * Give a graph, a starting node, return the BFS track in limited steps.
		 * @param WeightedGraphTroveZirder g: the graph
		 * @param s: the starting node
		 * @param MAX: the maximum size of the cluster
		 * @return
		 */	
		 public static TIntList BFS(WeightedUndirectedGraph g, int s, int MAX)
		    {
				TIntObjectMap<TIntList> map = g.map;

				TIntList visited = new TIntArrayList ();
		        TIntList res = new TIntArrayList ();
		        
		        LinkedList<Integer> queue = new LinkedList<Integer>();
		 
		        visited.add(s);
		        queue.add(s);
		 
		        while (queue.size() != 0)
		        {
		            s = queue.poll();
		            if(res.size() == MAX) return res;
		            res.add(s);
		 
		            TIntList neighborsWeight = map.get(s);
		            // Extract id list
		            TIntList neighbors = new TIntArrayList ();
		            TIntIterator tii = neighborsWeight.iterator();
		            while(tii.hasNext()){
		            	neighbors.add(tii.next());
		            	tii.next(); //skip the weights
		            }
		            
		            if(neighbors == null || neighbors.size() == 0) continue;
		            TIntIterator i = neighbors.iterator();
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
	 
	public static void main(String [] args) throws IOException{
//		UnweightedGraphTrove ug = new UnweightedGraphTrove("src/data/simple", "src/data/tracker");
//		System.out.println(ug.map.toString());
//		System.out.println(BFSCluster.BFS(ug, 1, 5));
		System.out.println("Testing BFS...");
		WeightedUndirectedGraph g = new WeightedUndirectedGraph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		int i = 0;
		while(i++ < 10){
			System.out.println("Test Index:" + i);
			BFSCluster.BFS(g, i, 500);
		}
	}

}
