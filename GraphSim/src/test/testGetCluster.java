package test;

import java.io.IOException;
import analysis.ClusterAnalysis;

public class testGetCluster {

	public static void main(String[] args) throws IOException {

		// test get Cluster
		ClusterAnalysis.getClusters("IntSpacemetisLargestComponentConsec.graph.part.100000");

	}

}
