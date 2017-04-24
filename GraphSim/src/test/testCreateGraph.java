package test;

import java.io.IOException;

import graph.WeightedGraph;
import graph.WeightedGraphQuickIntTrove;
import graph.WeightedGraphTroveZorder;

public class testCreateGraph {
	
	public static void main(String [] args) throws IOException{
		//Test if the weighted graph could be created.
		WeightedGraphTroveZorder g = new WeightedGraphTroveZorder("largestComponent_ConsecutiveID", "1", 1000);
		System.out.println(g.numsofnodes + ":" + g.numsofedges);
	}
}
