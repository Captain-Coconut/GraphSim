package clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import graph.UnweightedGraph;
import graph.UnweightedGraphTrove;
import graph.WeightedUndirectedGraph;

public class Conductance {
	
	/** WRONG and out dated. DO NOT USE!
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
	
	/** Wikipedia definition.  CutCount/ min (a(S), a(~S))
	 * Get the conductance given an UNWEIGHTED graph in Trove style, and a subgraph in it
	 * @param ug
	 * @param cluster: id sets of the subgraph
	 * @return
	 */
	public static double getConductanceUnweightedTrove(UnweightedGraphTrove ug, TIntList cluster){
		
		TIntObjectMap<TIntList> map = ug.map;

		int edgeNum = ug.numsofedges;
		TIntIterator it1 = cluster.iterator();
		int aS = 0;
		while(it1.hasNext()){
			aS += map.get(it1.next()).size();
		}
		
		int aNotS = 2 * edgeNum - aS;
		
		it1 = cluster.iterator();

		int countwithinEdges = 0;
		int countCuts = 0;

		while(it1.hasNext()){
			int i = it1.next();
			TIntList neighbours = map.get(i);
			if(neighbours == null || neighbours.size() == 0) continue;
			TIntIterator it2 = neighbours.iterator();
			while(it2.hasNext()){
				int j = it2.next();
				if(cluster.contains(j)) {
					countwithinEdges++;
				} else {
					countCuts++;
				}
			}
		}
		// in cluster edges will be counted twice
		countwithinEdges = countwithinEdges/2;
		//System.out.println(countwithinEdges);
		//System.out.println(countCuts);
		
		// indicate exception.
		if(countwithinEdges == 0) return Double.MAX_VALUE; 
		
		return 1.0*countCuts/Math.min(aS, aNotS);
	}
	
	/**
	 * Get minimum conductance in the whole graph w.r.t a clustering
	 * @param ug
	 * @param cluster
	 * @return
	 */
	public static double getMINConductanceUnweightedTrove(UnweightedGraphTrove ug, List <TIntList> clusters){
		double minConductance = Double.MAX_VALUE;
		for(TIntList til: clusters){
			double currentConductance = Conductance.getConductanceUnweightedTrove(ug, til);
			minConductance = currentConductance < minConductance ? currentConductance : minConductance;
		}
		return minConductance;
	}
	
	
	/**
	 * Get average conductance in the whole graph w.r.t a clustering
	 * @param ug
	 * @param cluster
	 * @return average conductance
	 */
	public static double getAVGConductanceUnweightedTrove(UnweightedGraphTrove ug, List <TIntList> clusters){
		double ConductanceSum = 0;
		for(TIntList til: clusters){
			double currentConductance = Conductance.getConductanceUnweightedTrove(ug, til);
			ConductanceSum += currentConductance;
		}
		return 1.0 * ConductanceSum/clusters.size();
	}
	
	/**
	 * Get the conductance given an weighted graph, represented in Trove style, and a subgraph in it
	 * @param ug
	 * @param cluster: id/weight sets of a cluster
	 * @return
	 */
	public static double getConductanceWeightedTrove(WeightedUndirectedGraph ug, TIntList cluster){
		
		int amplifier = ug.amplifier;
		
		// begin calculating total weights
		double totalWeight = 0.0;
		TIntObjectMap <TIntList> map = ug.map;
		TIntObjectIterator<TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			TIntList tl= mapIterator.value();
			TIntIterator tli = tl.iterator();
			while(tli.hasNext()){
				tli.next(); // get rid of id
				totalWeight += tli.next();
			}
		}
		totalWeight = 1.0 * totalWeight / amplifier;
		totalWeight /= 2;
		
		double aSweight = 0.0;
		TIntIterator tii = cluster.iterator();
		while(tii.hasNext()){
			int id = tii.next();
			TIntList neighboridweights = map.get(id);
			TIntIterator niwi = neighboridweights.iterator();
			while(niwi.hasNext()){
				niwi.next(); // get rid of id
				int neighborweight = niwi.next();
				aSweight += neighborweight;
			}
		}
		aSweight = 1.0 * aSweight / amplifier;
		double aNotSweight = 2 * totalWeight - aSweight;
		
		
		double weightCuts = 0;
		tii = cluster.iterator();
		TIntList neighborList = new TIntArrayList();
		TIntList weightList = new TIntArrayList();
		while(tii.hasNext()){
			int id = tii.next();
			TIntList neighboridweights = map.get(id);
			weightList.clear(); neighborList.clear();
			TIntIterator niwi = neighboridweights.iterator();
			
			// separate id and weight
			while(niwi.hasNext()){
				neighborList.add(niwi.next());
				weightList.add(niwi.next());
			}
			if(neighborList == null || neighborList.size() == 0) continue;
			
			TIntIterator neighborIDiterator = neighborList.iterator();
			while(neighborIDiterator.hasNext()){
				int j = neighborIDiterator.next();
				// If this neighbor is out of this cluster,
				// Then it is an edge cut
				if(!cluster.contains(j)) {
					// Find corresponding index of node j in nodesList
					// Then get the weight from weightList according to the 
					weightCuts += weightList.get(neighborList.indexOf(j));
				}
			}
		}
		weightCuts = 1.0 * weightCuts / amplifier;
				
		return 1.0*weightCuts/ Math.min(aSweight, aNotSweight);
	}
	
	/**
	 * Get overall conductance in the whole graph w.r.t a clustering
	 * @param ug
	 * @param cluster
	 * @return min conductance
	 */
	
	public static double getMinConductanceWeightedTrove(WeightedUndirectedGraph ug, List <TIntList> clusters, double sampleRate){
		TDoubleList tdlist = Conductance.getSampleConductanceListWeightedTrove(ug, clusters, sampleRate);
		TDoubleIterator ti = tdlist.iterator();
		double minConductance = Double.MAX_VALUE;
		while(ti.hasNext()){
			double currentConductance = ti.next();
			minConductance = currentConductance < minConductance ? currentConductance : minConductance;
		}
		return minConductance;		
	}
	
	/*
	 * New Version:
	 * change1: totalWeight is calculated only once now
	 * change2: iterate through id-weight compound list only once now
	 * change3(thinking...): change clusters to TIntHashset, however this shouldn't change time complexity a lot because the clusters themselves are actually not big (<1000). 
	 */
	public static double getConductanceWeightedTroveNEW(WeightedUndirectedGraph ug, TIntList cluster, double totalWeight){
		
		int amplifier = ug.amplifier;
		// total weights is already given
		
		TIntObjectMap <TIntList> map = ug.map;

		double aSweight = 0.0;
		TIntIterator tii = cluster.iterator();
		while(tii.hasNext()){
			int id = tii.next();
			TIntList neighboridweights = map.get(id);
			TIntIterator niwi = neighboridweights.iterator();
			while(niwi.hasNext()){
				niwi.next(); // get rid of id
				aSweight += niwi.next();
			}
		}
		aSweight = 1.0 * aSweight / amplifier;
		double aNotSweight = 2 * totalWeight - aSweight;
		
		double weightCuts = 0;
		tii = cluster.iterator();
		//TIntList neighborList = new TIntArrayList();
		//TIntList weightList = new TIntArrayList();
		while(tii.hasNext()){
			int id = tii.next();
			TIntList neighboridweights = map.get(id);
			TIntIterator niwi = neighboridweights.iterator();
			// separate id and weight
			// Calculate them in a different way
			
			while(niwi.hasNext()){
				int nid = niwi.next();
				int nweight = niwi.next();
				if(!cluster.contains(nid)){ // If this is indeed an edge cut, add the weight
					weightCuts += nweight;
				}				
			}
		}
		weightCuts = 1.0 * weightCuts / amplifier;
		
		//System.out.println("aSweight:" + (aSweight));
		//System.out.println("aNotSweight:" + (aNotSweight));
		
		return 1.0*weightCuts/ Math.min(aSweight, aNotSweight);

	}
	public static double getTotalWeightedTrove(WeightedUndirectedGraph ug){
		
		int amplifier = ug.amplifier;
		// begin calculating total weights
		double totalWeight = 0.0;
		TIntObjectMap <TIntList> map = ug.map;
		TIntObjectIterator<TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			TIntList tl= mapIterator.value();
			TIntIterator tli = tl.iterator();
			while(tli.hasNext()){
				tli.next(); // get rid of id
				totalWeight += tli.next();
			}
		}
		totalWeight /= amplifier;
		totalWeight /= 2;
		return totalWeight;
		
	}
	
	/**
	 * Instead of calculating AVG and MIN separately we return a list
	 * There are duplicated computations in Conductance.getConductanceWeightedTrove so we use a new version
	 * Conductance.getConductanceWeightedTroveNew, which gets rid of duplicated total weight calculation
	 * @param ug
	 * @param clusters
	 * @return a list of conductances;
	 */
	public static TDoubleList getConductanceListWeightedTrove(WeightedUndirectedGraph ug, List <TIntList> clusters){
		TDoubleList tdl = new TDoubleArrayList();
		double totalWeight = Conductance.getTotalWeightedTrove(ug);
		for(TIntList til: clusters){
			tdl.add(Conductance.getConductanceWeightedTroveNEW(ug, til, totalWeight));
		}
		return tdl;
	}
	
	/**
	 * sample computes some conductances given a clustering method in the graph
	 * @param ug
	 * @param clusters
	 * @param sample rate, between 0 - 1.
	 * @return a list of conductances, the size is smaller than clusters.size();
	 */
	public static TDoubleList getSampleConductanceListWeightedTrove(WeightedUndirectedGraph ug, List <TIntList> clusters, double sampleRate){
		TDoubleList tdl = new TDoubleArrayList();
		Random r = new Random();
		double totalWeight = Conductance.getTotalWeightedTrove(ug);
		for(TIntList til: clusters){
			// if(r.nextDouble() >= sampleRate) continue; // don't compute 
			tdl.add(Conductance.getConductanceWeightedTroveNEW(ug, til, totalWeight));
		}
		return tdl;
	}

	/**
	 * Get average conductance in the whole graph w.r.t a clustering
	 * @param ug
	 * @param cluster
	 * @return average conductance
	 */
	public static double getAVGConductanceWeightedTrove(WeightedUndirectedGraph ug, List <TIntList> clusters, double SampleRate){
		TDoubleList tdlist = Conductance.getSampleConductanceListWeightedTrove(ug, clusters, SampleRate);
		double ConductanceSum = 0;
		TDoubleIterator ti = tdlist.iterator();
		while(ti.hasNext()){
			ConductanceSum += ti.next();
		}
		return 1.0 * ConductanceSum / tdlist.size();
	}
	
	public static void main(String [] args) throws IOException{
		//System.out.println("Begin conductance testing...");
		//UnweightedGraphTrove uwgt = new UnweightedGraphTrove("src/data/ConductanceSample","src/data/ConductanceSample_Progress");
		WeightedUndirectedGraph g = new WeightedUndirectedGraph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		//TIntList til = new TIntArrayList();
		//til.add(1); til.add(2);
		//System.out.println(Conductance.getConductanceUnweightedTrove(uwgt, til));
		//System.out.println(Conductance.getConductanceWeightedTrove(wgtz, til));
		System.out.println("Beginning to test getConductanceListWeightedTrove on the whole graph");
		TIntList tl = new TIntArrayList ();
		for(int i = 1; i< 100; i++){
			tl.add(i);
		}
		TIntList tl2 = new TIntArrayList ();
		for(int i = 101; i< 200; i++){
			tl2.add(i);
		}
		List <TIntList> clusters = new ArrayList <>();
		clusters.add(tl); clusters.add(tl2);
		System.out.println(Conductance.getSampleConductanceListWeightedTrove(g, clusters, 1));
	}

}
