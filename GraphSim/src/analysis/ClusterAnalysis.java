package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import clustering.BFSCluster;
import clustering.Conductance;
import clustering.RandomWalk;
import file.GetFileOperator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import graph.WeightedGraphTroveZorder;

public class ClusterAnalysis {
	
	/**
	 * Get a random element from a TIntList
	 * @param l : the list
	 * @return a random element
	 */
	public static int getRandom(TIntList l) {
		Random rand = new Random();
		int index = rand.nextInt(l.size());
		return l.get(index);
	}
	
	/**
	 * Read Metis output to get a list of clusters
	 * @param MetisOutput
	 * @return a list where each element represents a list of ids in the same cluster
	 * @throws IOException
	 */
	public static List <TIntList> getClusters(String MetisOutput) throws IOException{
		List <TIntList> res = new ArrayList <TIntList> ();
		
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(MetisOutput);
		String line = new String();
		int nodeID = 0;
		int clusterID = 0;
		
		TIntObjectMap <TIntList> cluster_id = new TIntObjectHashMap<>();

		while((line = br.readLine()) != null){
			nodeID++;
			clusterID = Integer.parseInt(line);
			TIntList cur = cluster_id.containsKey(clusterID) ? cluster_id.get(clusterID) : new TIntArrayList();
			cur.add(nodeID);
			cluster_id.put(clusterID, cur);
		}
		
		TIntObjectIterator<TIntList> mapIterator = cluster_id.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			TIntList tl = mapIterator.value();
			res.add(tl);
		}
		return res;
	}
	
	// limit the times of experiments by ite
	public static void compareMetisBFSRandomWalk(WeightedGraphTroveZorder wg, String MetisOutput, String Results, String OverallResults, int iterations, double sampleRate, int ite) throws IOException{
		List <TIntList> clusters = ClusterAnalysis.getClusters(MetisOutput);
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(Results);
		Writer wOverall = gfo.getWriter(OverallResults);
		// third parameter: sample rate 0-1
		// double OverallMetisMinConductance = Conductance.getMinConductanceWeightedTrove(wg, clusters, sampleRate);
		// double OverallMetisAVGConductance = Conductance.getAVGConductanceWeightedTrove(wg, clusters, sampleRate);
		// wOverall.write("OverallMetisMinConductance:" + OverallMetisMinConductance +"\n");
		// wOverall.write("OverallMetisAVGConductance:" + OverallMetisAVGConductance +"\n");
		w.write("Metis,BFS,RandomWalk"+"\n");

		double minRWConduct = Double.MAX_VALUE;
		double minBFSConduct = Double.MAX_VALUE;
		double minMetisConduct = Double.MAX_VALUE;
		
		double AVGRWConduct = 0;
		double AVGBFSConduct = 0;
		double AVGMetisConduct = 0;
		
		int j = 0;
		
		double totalWeight = Conductance.getTotalWeightedTrove(wg); 
		for(TIntList Metiscluster: clusters){
			double METISConduct = Conductance.getConductanceWeightedTroveNEW(wg, Metiscluster, totalWeight);
			
			// Do the experiment for multiple times with different seed nodes for BFS and RW
			int i = 0;
			double BFSConduct = 0.0;
			double RWConduct = 0.0;
			while(i < iterations){
				// Get a random node from MethisCluster as the seed node for BFS
				i++;
				int seed = ClusterAnalysis.getRandom(Metiscluster);
				TIntList BFScluster = BFSCluster.BFS(wg, seed, Metiscluster.size());
				TIntList RWcluster = RandomWalk.RandomWalkCluster(wg, seed, Metiscluster.size());
				BFSConduct += Conductance.getConductanceWeightedTroveNEW(wg, BFScluster, totalWeight);	
				RWConduct += Conductance.getConductanceWeightedTroveNEW(wg, RWcluster, totalWeight);
			}
			// Take the average of the experiments
			BFSConduct = 1.0 * BFSConduct / iterations;
			RWConduct = 1.0 * RWConduct / iterations;
			
			// update min
			minMetisConduct = minMetisConduct < METISConduct ? minMetisConduct: METISConduct;
			minBFSConduct = minBFSConduct < BFSConduct ? minBFSConduct : BFSConduct;
			minRWConduct = minRWConduct < RWConduct ? minRWConduct : RWConduct;

			// update AVG
			AVGMetisConduct += METISConduct;
			AVGBFSConduct += BFSConduct;
			AVGRWConduct += RWConduct;
			
			w.write(METISConduct + "," + BFSConduct + "," + RWConduct + "\n");
			w.flush();
			
			if(++j > ite) break;
		}
//		AVGBFSConduct /= clusters.size();
//		AVGRWConduct /= clusters.size();
//		AVGMetisConduct /= clusters.size();
		
		// divide by the number of experiments
		AVGBFSConduct /= j;
		AVGRWConduct /= j;
		AVGMetisConduct /= j;
		
		wOverall.write("OverallBFSMinConductance:" + minBFSConduct +"\n");
		wOverall.write("OverallBFSAVGConductance:" + AVGBFSConduct +"\n");

		wOverall.write("OverallRWMinConductance:" + minRWConduct +"\n");
		wOverall.write("OverallRWAVGConductance:" + AVGRWConduct +"\n");
		
		wOverall.write("OverallMetisMinConductance:" + minMetisConduct +"\n");
		wOverall.write("OverallMetisAVGConductance:" + AVGMetisConduct +"\n");

		
		w.flush();
		w.close();
		wOverall.flush();
		wOverall.close();
	}
	
//	public static void compareRandomWalk(WeightedGraphTroveZorder wg, String MetisOutput, String RWResults, String OverallResults, int iterations) throws IOException{
//		List <TIntList> clusters = ClusterAnalysis.getClusters(MetisOutput);
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(RWResults);
//		Writer wOverall = gfo.getWriter(OverallResults);		
//		double OverallMetisMinConductance = Conductance.getMinConductanceWeightedTrove(wg, clusters);
//		double OverallMetisAVGConductance = Conductance.getAVGConductanceWeightedTrove(wg, clusters);
//
//		wOverall.write("OverallMetisMinConductance:" + OverallMetisMinConductance +"\n");
//		wOverall.write("OverallMetisAVGConductance:" + OverallMetisAVGConductance +"\n");
//		w.write("Metis,BFS"+"\n");
//
//		double minBFSConduct = Double.MAX_VALUE;
//		double AVGBFSConduct = 0;
//		for(TIntList Metiscluster: clusters){
//			double currentMetisClusterConductance = Conductance.getConductanceWeightedTrove(wg, Metiscluster);
//			
//			// Do the experiment for multiple times with different seed nodes for BFS
//			int i = 0;
//			double BFSConduct = 0.0;
//			while(i++ < iterations){
//				// Get a random node from MethisCluster as the seed node for BFS
//				int seed = ClusterAnalysis.getRandom(Metiscluster);
//				TIntList BFScluster = BFSCluster.BFS(wg, seed, Metiscluster.size());
//				BFSConduct += Conductance.getConductanceWeightedTrove(wg, BFScluster);	
//			}
//			// Take the average
//			BFSConduct = 1.0 * BFSConduct / iterations;
//			
//			minBFSConduct = minBFSConduct < BFSConduct ? minBFSConduct : BFSConduct;
//			AVGBFSConduct += BFSConduct;
//			
//			w.write(currentMetisClusterConductance + "," + BFSConduct + "\n");
//		}
//		AVGBFSConduct /= clusters.size();
//		
//		wOverall.write("OverallBFSMinConductance:" + minBFSConduct +"\n");
//		wOverall.write("OverallBFSAVGConductance:" + AVGBFSConduct +"\n");
//
//		
//		w.flush();
//		w.close();
//		wOverall.flush();
//		wOverall.close();
//	}
	
	public static void compare(String graphPath, String MetisOutput, String Results, int amplifier, int ite) throws IOException{
		Random r = new Random();
		WeightedGraphTroveZorder wg = new WeightedGraphTroveZorder(graphPath, graphPath + MetisOutput + "_ReadTrack" + r.nextFloat(), amplifier);
		ClusterAnalysis.compareMetisBFSRandomWalk(wg, MetisOutput, Results, Results+"_Overview", 10, 1, ite);
		//ClusterAnalysis.compareRandomWalk(wg, MetisOutput, Results+"_RW", Results+"_RW_Overall", 5);
	}
	
	public static void main(String [] args) throws IOException{
		String graphPath = args[0];
		int amplifier = Integer.parseInt(args[1]);
		String MetisOutput = args[2];
		String Results = args[3];
		int ite = Integer.parseInt(args[4]);
		ClusterAnalysis.compare(graphPath, MetisOutput, Results, amplifier, ite);
	}

}
