package clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import graph.UnweightedGraph;
import graph.UnweightedGraphTrove;
import graph.WeightedGraphTroveZorder;

public class RandomWalk {
	List<Integer> walk = new ArrayList<>();
	List<Double> prob = new ArrayList<>();

	/** Outdated because of memory problem
	 * Get a series of walks starting from node start in graph g, limited to
	 * stepsMax steps
	 * 
	 * @param g:
	 *            an un-weighted graph
	 * @param stepsMax:
	 *            number of steps
	 * @return list of node IDs along a walk
	 */
	public void getRandomWalk(UnweightedGraph g, int start, int stepsMax) {
		Map<Integer, Set<Integer>> map = g.map;

		List<Integer> walk = new ArrayList<>();
		List<Double> prob = new ArrayList<>();
		int step = 0;
		int cur = start;
		double p = 1.0;
		walk.add(cur);
		prob.add(p);

		while (step++ < stepsMax) {
			ArrayList<Integer> neighbors = new ArrayList<Integer>(map.get(cur));
			// TODO
			// What if it is empty, do we teleport back to the start? Yes
			// If then, how do we calculate the probability. Keep the prob
			// unchanged.
			if (neighbors == null || neighbors.size() == 0) {
				cur = start;
				walk.add(cur);
				prob.add(p);
			} else {
				// jump to a random node
				cur = this.getRandom(neighbors);
				walk.add(cur);
				// update probability
				p *= 1.0 / neighbors.size();
				prob.add(p);
			}
		}
	}

	/**
	 * Get a random element from a list
	 * 
	 * @param l
	 *            : a list
	 * @return a random element
	 */
	public int getRandom(List<Integer> l) {
		Random rand = new Random();
		int index = rand.nextInt(l.size());
		return l.get(index);
	}

	/**
	 * 
	 * @param rw1
	 * @param rw2
	 * @return Two elements: the first one is number of steps the second one is
	 *         prob of the first walker, the third one is prob of the second
	 *         walker.
	 */
	public List<Double> getMeetingDistance(RandomWalk rw1, RandomWalk rw2) {
		Iterator<Integer> w1 = rw1.walk.iterator();
		Iterator<Double> p1 = rw1.prob.iterator();
		Iterator<Integer> w2 = rw1.walk.iterator();
		Iterator<Double> p2 = rw1.prob.iterator();
		// TODO
		// Haven't considered the exception that they can't meet
		double prob1 = 1.0;
		double prob2 = 1.0;
		int cur1;
		int cur2;
		int steps = 0;
		while (w1.hasNext() && w2.hasNext()) {
			cur1 = w1.next();
			cur2 = w2.next();
			prob1 *= p1.next();
			prob2 *= p2.next();
			if (cur1 == cur2) { // we meet here
				break;
			}
			steps++;
		}
		List<Double> res = new ArrayList<>();
		res.add(steps * 1.0);
		res.add(prob1);
		res.add(prob2);
		return res;
	}

	public int getRandom(TIntList l) {
		Random rand = new Random();
		int index = rand.nextInt(l.size());
		return l.get(index);
	}

	/**
	 * Give a UnweightedGraphTrove graph, a starting node, return the RandomWalk
	 * track in limited steps. Please note the list we return might not have MAX nodes because the random walk might
	 * walk back.
	 * 
	 * @param UnweightedGraphTrove
	 *            us: the graph
	 * @param start:
	 *            the starting node
	 * @param MAX:
	 *            the maximum size of the cluster
	 * @return List of the walk trajectory
	 */
	public TIntList getRandomWalkCluster(UnweightedGraphTrove ug, int start, int MAX) {
		// Map<Integer, Set<Integer>> map = g.map;
		TIntObjectMap<TIntList> map = ug.map;

		TIntList walk = new TIntArrayList();
		int step = 0;
		int cur = start;
		walk.add(cur);

		while (step++ < MAX) {
			TIntList neighbors = map.get(cur);
			// We know neigbors wont be null because this is a connected graph.
			// But we include this anyway. Solution: teleport back to the start.
			if (neighbors == null || neighbors.size() == 0) {
				cur = start;
				walk.add(cur);
			} else {
				// jump to a random neighboring node
				cur = this.getRandom(neighbors);
				walk.add(cur);
			}
		}

		return walk;
	}

	/**
	 * Give a WeightedGraphTroveZorder graph, a starting node, return the
	 * RandomWalk track in limited steps. We assume the size of the cluster will
	 * be reasonable so there is no need to use Trove to store the list Please
	 * node the list we return might not have MAX nodes because the random walk
	 * might walk back.
	 * 
	 * @param WeightedGraphTroveZorder
	 *            us: the graph
	 * @param start:
	 *            the starting node
	 * @param MAX:
	 *            the maximum size of the cluster
	 * @return List of the walk trajectory
	 */
	public static TIntList RandomWalkCluster(WeightedGraphTroveZorder g, int start, int MAX) {
		// Map<Integer, Set<Integer>> map = g.map;
		TIntObjectMap<TIntList> map = g.map;
		// int amplifier = g.amplifier;

		TIntList walk = new TIntArrayList();
		int step = 0;
		int cur = start;
		walk.add(cur);

		while (step++ < MAX) {
			// neighborsComposite contains n1,w1,n2,w2,n3,w3
			TIntList neighborsComposite = map.get(cur);
			// We know neigbors wont be null because this is a connected graph.
			// But we include this anyway. Solution: teleport back to the start.
			if (neighborsComposite == null || neighborsComposite.size() == 0) {
				cur = start;
				walk.add(cur);
			} else {
				// jump to a random neighboring node
				cur = RandomWalk.getRandomTrove(neighborsComposite, g.amplifier);
				walk.add(cur);
			}
		}

		return walk;
	}

	/**
	 * Get a random node id from a composite list proportional to its weight The
	 * list contains n1,w1,n2,w2....
	 * @param tl: The neighboring nodes
	 * @param amplifier: the parameter used in Z order
	 * @return
	 */
	public static int getRandomTrove(TIntList tl, int amplifier) {
		TIntList IDs = new TIntArrayList();
		List <Integer> Weights = new ArrayList <Integer> ();
		TIntIterator ti = tl.iterator();
		
		while(ti.hasNext()){
			IDs.add(ti.next());
			Weights.add(ti.next());
		}
		
		return IDs.get(RandomWalk.getRandomTroveHelper(Weights, amplifier));
		
	}

	// Reservoir Sampling with Weights
	// https://github.com/graphaware/neo4j-algorithms/blob/master/src/main/java/com/graphaware/module/algo/generator/utils/WeightedReservoirSampler.java
	public static int getRandomTroveHelper(List<Integer> weights, int amplifier) {
		int result = 0, index;
		double maxKey = 0.0, u, key, weight;
		Random random = new Random();

		for (ListIterator<Integer> it = weights.listIterator(); it.hasNext();) {
			index = it.nextIndex();
			weight = 1.0 * it.next() / amplifier;
			u = random.nextDouble();
			key = Math.pow(u, (1.0 / weight)); 

			if (key > maxKey) {
				maxKey = key;
				result = index;
			}
		}

		return result;
	}
	
	public static void main (String [] args) throws IOException {
		System.out.println("Begin testing Random Walk");
		WeightedGraphTroveZorder wgtz = new WeightedGraphTroveZorder("largestComponent_ConsecutiveID", "TestRW_Track", 1000);
		int i = 0;
		while(i++ < 10){
			System.out.println("Test Index:" + i);
			RandomWalk.RandomWalkCluster(wgtz, i, 500);
		}
	}

}
