package publisher;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import file.GetFileOperator;

public class WeightFilterHashMap {
	
	List <Set <Integer>> componentsList = new ArrayList<>();
	//int sizeLimit = -1;
	int recursionLimit = 4;
	double stepAmplifier = 1.2; // Needs to be larger than 1
	
	// See recursiveUnionfind for explanation
	double filterDropPercentageThreshold = 0.03;
	double filterDropSizeThreshold = 10;
	
	// Size limit is NOT a hard limit.
	public void recursiveUnionfind(Graph g, int depth, int sizeLimit, double weightThreshold) throws IOException{
		
		Set <Integer> beforeNodeSet = g.getNodesSet();

		if(depth >= recursionLimit)  
		{ // If we have reached the limit for recursion, we report the cluster without partitioning
			componentsList.add(beforeNodeSet);
			return;		
		}
		
		// filter the graph with the new weight threshold to make it sparser
		g.filterGraph(weightThreshold);
		Set <Integer> AfterNodeSet = g.getNodesSet();
		if (1.0*AfterNodeSet.size() / beforeNodeSet.size() < filterDropPercentageThreshold || AfterNodeSet.size() < filterDropSizeThreshold) { 
			// It means the weightThreshold is too high and we have eliminated most of the edges
			// essentially it means most of the nodes have similar relationship with each other
			// And we should just put them together
			// we simply add the original set to the result and don't do more partitioning
			// It applies as well when the filtered graph is too small
			componentsList.add(beforeNodeSet);
			return;
		}

		Map <Integer, Integer> old2new = g.remap();
		Map <Integer, Set <Integer>> components = g.getComponents();
		components.remove(0);
		
		List <Set <Integer>> nextStep = new ArrayList <> ();
		
		// node 0 always ONLY have a self loop to 0 and is only used in UF for convenience;
		for(Map.Entry<Integer, Set <Integer>> entry : components.entrySet()){
			Set <Integer> cur = entry.getValue();
			if(cur.size() < sizeLimit) { // If the component size is small enough
				componentsList.add(mapBack(old2new, cur)); 
				// Map the IDs back first
				// Add the component to the result
			} else{ 
				// Map  the IDs back first
				// And then add the component for the processing queue
				nextStep.add(mapBack(old2new, cur));
			}
		}
		
		//g also needs to be mapped back
		g.mapBack(old2new);
		
		for(Set <Integer> cur : nextStep){
			Graph subG = g.getSubGraph(g, cur);
			recursiveUnionfind(subG, ++depth, sizeLimit, weightThreshold*stepAmplifier);
		}
		
	}
	
	public Map <Integer, Integer> reversePair(Map <Integer, Integer> old2new){
		// Reverse the relationship
		Map <Integer, Integer> new2old = new HashMap <Integer, Integer> ();
		for(Map.Entry<Integer, Integer> entry : old2new.entrySet()){
			new2old.put(entry.getValue(), entry.getKey());
		}
		return new2old;
	}
	
	public Set <Integer> mapBack (Map<Integer, Integer> old2new, Set <Integer> newSet){
		Map <Integer, Integer> new2old = reversePair(old2new);
		Set <Integer> oldSet = new HashSet <>();
		for(int newEntry: newSet){
			oldSet.add(new2old.get(newEntry));
		}
		return oldSet;
	}
	
	//	List <Set <Integer>> componentsList = new ArrayList<>();
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
		Graph g = new Graph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		Map <Integer, Set <Integer>> components = g.getComponents();
		List <Set <Integer>> bigComponents = new ArrayList <> ();
		for(Map.Entry<Integer, Set <Integer>> entry: components.entrySet()){
			if(entry.getValue().size() > 1000) { 
				//if we find big components, save them for future use
				System.out.println("Find big component of size " + entry.getValue().size());
				bigComponents.add(entry.getValue());
			} else{
				// put the smaller components directly to the resuls.
				wfhm.componentsList.add(entry.getValue());
			}
		}
		
		List <Graph> listGraphs = g.getSubGraphs(g, bigComponents);
		for(Graph subG : listGraphs){
			wfhm.recursiveUnionfind(subG, 0, 500, 4.58 * wfhm.stepAmplifier);
		}
		
		wfhm.writeComponents(args[1]);

	}
	
}
