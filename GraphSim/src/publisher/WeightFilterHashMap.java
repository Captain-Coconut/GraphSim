package publisher;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import file.GetFileOperator;
import graph.WeightedUndirectedGraph;

public class WeightFilterHashMap {
	List <Set <Integer>> componentsList = new ArrayList<>();
	//int sizeLimit = -1;
	int recursionLimit = 10;
	double stepAmplifier = 1.2; // Needs to be larger than 1
	
	// See recursiveUnionfind for explanation
	double filterDropPercentageThreshold = 0.03;
	double filterDropSizeThreshold = 10;
	
	//TODO beforenodeset and afternodeset will always be the same according to how filter() was written
	//TODO filter() needs to be changed in a way such that if the neighboring list is empty, we remove it.
	// Size limit is NOT a hard limit.
	public void recursiveUnionfind(WeightedUndirectedGraph g, int depth, int sizeLimit, double weightThreshold) throws IOException{
		
		Set <Integer> beforeNodeSet = g.getNodesSetHashSet();
		
		System.out.println("depth" + depth);
		
		if(depth >= recursionLimit)  
		{ // If we have reached the limit for recursion, we report the cluster without partitioning
			componentsList.add(beforeNodeSet);
			return;		
		}
		
		// filter the graph with the new weight threshold to make it sparser
		g.filterGraph(weightThreshold);
		Set <Integer> AfterNodeSet = g.getNodesSetHashSet();
		System.out.println("before node set size" + beforeNodeSet.size());
		System.out.println("after node set size" + AfterNodeSet.size());
		System.out.println("Drop Ratio" + 1.0*AfterNodeSet.size() / beforeNodeSet.size());
		if (1.0*AfterNodeSet.size() / beforeNodeSet.size() < filterDropPercentageThreshold || AfterNodeSet.size() < filterDropSizeThreshold) { 
			// It means the weightThreshold is too high and we have eliminated most of the edges
			// essentially it means most of the nodes have similar relationship with each other
			// And we should just put them together
			// we simply add the original set to the result and don't do more partitioning
			// It applies as well when the filtered graph is too small
			componentsList.add(beforeNodeSet);
			return;
		}

		// Now the map of the graph actually didn't change.
		// Map <Integer, Integer> old2new = g.remap();
		// getComponents is written in a way such that the results are the original old IDs
		Map <Integer, Set <Integer>> components = g.getComponents();
		
		List <Set <Integer>> nextStep = new ArrayList <> ();
		
		// node 0 always ONLY have a self loop to 0 and is only used in UF for convenience;
		for(Map.Entry<Integer, Set <Integer>> entry : components.entrySet()){
			Set <Integer> cur = entry.getValue();
			if(cur.size() < sizeLimit) { // If the component size is small enough
				componentsList.add(cur); 
				// Add the component to the result
			} else{ 
				// add the component for the processing queue
				nextStep.add(cur);
			}
		}
				
		for(Set <Integer> cur : nextStep){
			WeightedUndirectedGraph subG = g.getSubGraph(cur);
			recursiveUnionfind(subG, ++depth, sizeLimit, weightThreshold*stepAmplifier);
		}
		
	}
		
	public void writeComponents(String filepath) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(filepath);
		for(Set<Integer> s : componentsList){
			w.write(s.toString() + "\n");
		}
		w.flush();
		w.close();
	}
	
	public static void main(String [] args) throws IOException{ 
//		WeightFilterHashMap wfhm = new WeightFilterHashMap();
//		Graph g = new Graph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
//		wfhm.recursiveUnionfind(g, 0, 500, 4.58);
//		wfhm.writeComponents(args[1]);
		
		// Change to another way to invoke recursive union find
		// ... To save memory
		// The major difference is that in the first recursion, there is NO remapping any more
		WeightFilterHashMap wfhm = new WeightFilterHashMap();
		WeightedUndirectedGraph g = new WeightedUndirectedGraph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		Map <Integer, Set <Integer>> components = g.getComponents();
		List <Set <Integer>> bigComponents = new ArrayList <> ();
		for(Map.Entry<Integer, Set <Integer>> entry: components.entrySet()){
			if(entry.getValue().size() > 1000) { 
				//if we find big components, save them for future use
				System.out.println("Find big component of size : " + entry.getValue().size());
				// System.out.println("Component Content:" + entry.getValue().toString());
				bigComponents.add(entry.getValue());
			} else{
				// put the smaller components directly to the results.
				wfhm.componentsList.add(entry.getValue());
			}
		}
		
		System.out.println("big components size" + bigComponents.size());
		
		for(Set <Integer> component: bigComponents){
			WeightedUndirectedGraph subG = g.getSubGraph(component);
			System.out.println("subG size:" + subG.getNumOfNodes());
			wfhm.recursiveUnionfind(subG, 0, 200, 4.58 * wfhm.stepAmplifier);
		}
		
		
		wfhm.writeComponents(args[1]);

	}
	
}
