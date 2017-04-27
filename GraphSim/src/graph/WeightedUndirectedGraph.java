package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import analysis.MapHelper;
import file.GetFileOperator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import publisher.UnionFind;

// This is a revised version of class::GraphNoRemap.
// The major change is right now we don't set remap/mapback as seperate functions and create new graphs
// The structure of the functions are also changed
// getNodesSet is changed.
// Remapping is changed

public class WeightedUndirectedGraph {
	
	public TIntObjectMap <TIntList> map = new TIntObjectHashMap<>();
	public int amplifier;

	/**
	 * Constructor
	 * read the graph file and ONLY pick up edges with weight larger than 'threshold'
	 * If we don't need weight filter, just set threshold to Integer.MIN_VALUE
	 * @param input_path: path of the edge list file
	 * @param header: 1 indicates the edge list file has a header, otherwise no
	 * @param delimiter: usually comma
	 * @param threshold: threshold of edge weight (4.58 for now)
	 * @param amplifier: for flattening the hashmap
	 * @param id1_index: index for node 1 in the edge list file
	 * @param id2_index: index for node 2 in the edge list file
	 * @param weight_index: index for weight in the edge list file
	 * @throws IOException
	 */ 
	public WeightedUndirectedGraph(String input_path, int header, String delimiter, double threshold, int amplifier, int id1_index, int id2_index, int weight_index) throws IOException {
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input_path);
		this.amplifier = amplifier;
		
		// Get rid of header
		if(header == 1)	br.readLine();

		System.out.println("Constructing the graph with HashMap....");

		String line;
		String[] lines;
		int id1, id2;
		int weight;
		while ((line = br.readLine()) != null) {
			lines = line.split(delimiter);

			// We multiply it by an amplifier to make it an Integer so we can
			// save both neighbors and weights in a plain Trove list
			// This amplifier can help us specify the accuracy we want to preserve
			weight = Math.round(Float.parseFloat(lines[weight_index]) * amplifier);

			// If the weight is smaller than the threshold, then we discard this
			// line and go to the next line
			if (weight <= threshold * amplifier) continue;

			id1 = Integer.parseInt(lines[id1_index]);
			id2 = Integer.parseInt(lines[id2_index]);

			TIntList th1 = map.containsKey(id1) ? map.get(id1) : new TIntArrayList();
			th1.add(id2); // this is neighbor
			th1.add(weight); // this is weight*amplifier
			map.put(id1, th1);

			TIntList th2 = map.containsKey(id2) ? map.get(id2) : new TIntArrayList();
			th2.add(id1); // this is neighbor
			th2.add(weight); // this is weight*amplifier
			map.put(id2, th2);
		}
		
		System.out.println("Finished constructing the original graph/HashMap...");

	}
	
	/**
	 * Constructor
	 * construct a graph from a HashMap
	 * @param map: a HashMap with "flattened" format 
	 */
	public WeightedUndirectedGraph(TIntObjectMap <TIntList> map, int amplifier){
		this.amplifier = amplifier;
		this.map = map;
	}
	
	/**
	 * Constructor used when we simply want to use the functions.
	 */
	public WeightedUndirectedGraph(){
		this.amplifier = 1;
	}
	
	public int getNumOfNodes(){
		return this.map.size();
	}
	
	public int getNumOfEdges(){
		int numOfEdges = 0;
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			numOfEdges += mapIterator.value().size();
		}
		return numOfEdges/4;  // divided by 4 because weights are also in the list
	}
	
	public int getMaxID(){
		TIntSet tis = map.keySet();
		TIntIterator tii = tis.iterator();
		int max = Integer.MIN_VALUE;
		while(tii.hasNext()){
			max = Math.max(max,tii.next());
		}
		return max;
	}
	
	public int getMinID(){
		TIntSet tis = map.keySet();
		TIntIterator tii = tis.iterator();
		int min = Integer.MAX_VALUE;
		while(tii.hasNext()){
			min = Math.min(min,tii.next());
		}
		return min;
	}
	
	// Changed to a more efficient calculation
	public TIntSet getNodesSet(){
		return map.keySet();
	}
	
	public Set <Integer> getNodesSetHashSet(){
		TIntSet ts = map.keySet();
		TIntIterator ti = ts.iterator();
		Set <Integer> s = new HashSet <>();
		while(ti.hasNext()){
			s.add(ti.next());
		}
		return s;
	}
	
	/** FilterGraph() is also updated. The original one would keep outlier nodes, this eliminates them
	 * filter the edges in the graph with low weights
	 * @param threshold
	 */
	public void filterGraph(double threshold){
		threshold *= this.amplifier; //remember to amplify the threshold
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		List <Integer> outliers = new ArrayList <>();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntList newNeighborsAndWeight = new TIntArrayList();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			int id, amplifiedWeight;
			while(listIterator.hasNext()){
				id = listIterator.next();
				amplifiedWeight = listIterator.next();
				if(amplifiedWeight > threshold) { // if the weight is sufficiently large
					newNeighborsAndWeight.add(id);
					newNeighborsAndWeight.add(amplifiedWeight);
				}
			}
			//System.out.println("old:" + neighborsAndWeight);
			//System.out.println("new:" + newNeighborsAndWeight);
			
			// caution: map.put(key, newNeighborsAndWeight) might cause problem for the iterator
			// because of the concurrent modification exception
			// update the new id-weight list (the safe way)
			// Cannot use neighborsAndWeight = new ArrayList <> (newNeighborsAndWeight)
			// because it will change the reference!
			// neighborsAndWeight.clear();
			// listIterator = newNeighborsAndWeight.iterator();
			// while(listIterator.hasNext()) neighborsAndWeight.add(listIterator.next());
			if(newNeighborsAndWeight.size() == 0){
				// If we trimmed the graph and created some outliers, we don't put newNeighborsAndWeight in the map
				// And also, we need to remove the original one
				// map.remove(key); this will causes concurrent modification
				outliers.add(key);
			} else{
				// for the put() function: Associates the specified value with the specified key in this map. If the map previously contained a mapping for the key, the old value is replaced.
				// So "replacing the value" is fine. But removal or change the key is not allowed.
				map.put(key, newNeighborsAndWeight);
			}	
		}
		
		// Remove the outliers
		for(int outlier: outliers){
			map.remove(outlier);
		}
		
	}
	
	/** Re-mapping function is now changed so that it ONLY returns the mapping relationship
	 * without actually modifying the graph
	 * re-map the graph to a representation with consecutive IDs
	 * @return the re-mapping relationship. OldID : New ID
	 * @throws IOException
	 */
	public Map<Integer, Integer> remap () throws IOException{
		TIntSet IDs = this.map.keySet();
		Map <Integer, Integer> mapRelationship = new HashMap <Integer, Integer> ();

		// PAY ATTENTION: ID STARTS WITH 1
		int currentID = 1;
		
		// Get the mapping relationship
		TIntIterator it = IDs.iterator();
		while(it.hasNext()){
			int oldID = it.next();
			int newID = mapRelationship.containsKey(oldID) ? mapRelationship.get(oldID) : currentID ++;
			mapRelationship.put(oldID, newID);	
		}
		
		return mapRelationship;
	}
	
	// This will actually change everything and thus is very expensive
	public Map<Integer, Integer> deepRemap () throws IOException{
		TIntSet IDs = this.map.keySet();
		Map <Integer, Integer> mapRelationship = new HashMap <Integer, Integer> ();

		// PAY ATTENTION: ID STARTS WITH 1
		int currentID = 1;
		
		// Get the mapping relationship
		TIntIterator it = IDs.iterator();
		while(it.hasNext()){
			int oldID = it.next();
			int newID = mapRelationship.containsKey(oldID) ? mapRelationship.get(oldID) : currentID ++;
			mapRelationship.put(oldID, newID);	
		}
		
		//Start to re-map every ID in the graph
		//This will be VERY expensive... We HAVE to create a new HashMap
		TIntObjectMap <TIntList> newMap = new TIntObjectHashMap<>();
		
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			
			int newKey = mapRelationship.get(key);
			TIntList newNeighborsAndWeight = new TIntArrayList();
			
			int id, amplifiedWeight;
			while(listIterator.hasNext()){
				id = listIterator.next();
				amplifiedWeight = listIterator.next();
				newNeighborsAndWeight.add(mapRelationship.get(id));
				newNeighborsAndWeight.add(amplifiedWeight);
			}
			
			newMap.put(newKey, newNeighborsAndWeight);
		}
		
		// Update the graph
		this.map = newMap;
		
		return mapRelationship;
	}
	
	/** MapBack function is now deprecated
	 * We now only need a reverse mapping relation table
	 * Before using this function, we need to make sure that we are having a re-mapped graph with consecutive IDs
	 * @param mapping: mapping relationship with OldID:NewID. So we need to reverse it first
	 */
	public Map <Integer, Integer> reverseMapping(Map<Integer, Integer> mapping){
		
		// Reverse the relationship
		Map <Integer, Integer> mapRelationship = new HashMap <Integer, Integer> ();
		for(Map.Entry<Integer, Integer> entry : mapping.entrySet()){
			mapRelationship.put(entry.getValue(), entry.getKey());
		}
		
		return mapRelationship;
	}
	
	// New change: We will now handle id[0] here
	// just map.remove(0) 
	// ALSO, the REMAPPING of UNION FIND IS DONE here!
	// We assume that the Node IDs are consecutive now starting from ********** 1 ************
	// uf.id[0] will always be 0, which we won't process
	public Map <Integer, Set <Integer>> getComponents() throws IOException{
		
		// Get the re-mapping relationship;
		Map <Integer, Integer> Mapping = remap(); // old : new
		Map <Integer, Integer> reverseMapping = reverseMapping(Mapping); // new : old
		
		// We can't use this to get the size any more because now the graph is NOT remapped
		// int MAXID = Integer.parseInt(getMinAndMaxID().split(",")[1]);
		int MAXID = Mapping.size(); // This will be correct if remap() is correct
		int N = MAXID + 1;
		UnionFind uf = new UnionFind(N);
		
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			key = Mapping.get(key); // Convert it to the new corresponding ID
					
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
						
			while(listIterator.hasNext()){
				uf.unite(key, Mapping.get(listIterator.next())); // unite the IDs (convert to new ID first)
				listIterator.next(); // skip the weight
			}
		}
		
		// Go through the array again to find root for every node.
		for(int i = 0; i < N; i++){
			uf.id[i] = uf.root(i);
		}
		
		// Put components in a HashMap
		Map <Integer, Set <Integer>> components = new HashMap <>();
		for(int i = 1; i < N; i++){
			int oldCurNodeID = reverseMapping.get(i); // get the old ID for the current node 
			int oldRootID = reverseMapping.get(uf.id[i]); // get the old ID for the root node:ClusterID
			Set <Integer> hs = components.containsKey(oldRootID) ? components.get(oldRootID) : new  HashSet <Integer> ();
			hs.add(oldCurNodeID);				
			components.put(oldRootID, hs);
		}
		
		// We know for sure that key=0 exists and the value is always [0]
		//components.remove(0);

		return components;
	}
	
	//TODO
	public Map <Integer, Set <Integer>> getClustersMetis(String MetisGraph, String errMessage, String mappingPath, String MetisCommand, int k) throws IOException{
		
		// Step 1, Write Metis File
		// Step 2, Invoke Metis
		// Step 3, Read Metis Output
		Map <Integer, Set <Integer>> clusters = new HashMap <>();
		
		// NewID: OldID
		Map<Integer, Integer>  reverseMapping = writeGraphMetisWeighted(MetisGraph, errMessage, mappingPath);
		
		
		
		return null;
	}
	
	public WeightedUndirectedGraph getSubGraph(Set <Integer> component) {
		
		TIntObjectMap <TIntList> newMap = new TIntObjectHashMap<>();
		
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			if (!component.contains(key)) continue; // if id1 is not in component, skip this line
			
			TIntList newNeighborsAndWeight = new TIntArrayList();
			
			int id, amplifiedWeight;
			while(listIterator.hasNext()){
				id = listIterator.next();
				amplifiedWeight = listIterator.next();
				if(component.contains(id)){
					newNeighborsAndWeight.add(id);
					newNeighborsAndWeight.add(amplifiedWeight);
				}
			}
			
			newMap.put(key, newNeighborsAndWeight);
		}
		
		return new WeightedUndirectedGraph(newMap, amplifier);
		
	}
	
	public List <WeightedUndirectedGraph> getSubGraphs(List <Set <Integer>> listOfComponents){
		List <WeightedUndirectedGraph> res = new ArrayList <>();
		WeightedUndirectedGraph gt = new WeightedUndirectedGraph();
		for(Set <Integer> component : listOfComponents){
			res.add(gt.getSubGraph(component));
		}
		return res;
	}
	
	
	/** This is also changed to a more efficient version
	 * Get minID and maxID in the graph
	 * @param g
	 * @return "minID,maxID"
	 */
	public String getMinAndMaxID(){
		TIntSet nodeSet = getNodesSet();
		TIntIterator it = nodeSet.iterator();
		
		int currentMAXID = Integer.MIN_VALUE;		
		int currentMINID = Integer.MAX_VALUE;
		int cur = -1;

		while(it.hasNext()){
			cur = it.next();	
			currentMAXID = cur > currentMAXID ? cur : currentMAXID;
			currentMINID = cur < currentMINID ? cur : currentMINID;
		}
		
		return currentMINID + "," + currentMAXID;
	}
	
	/**
	 * Write three files: Metis graph file, Error Message, Mapping information(to map back to original IDs in the future)
	 * @param output
	 * @param errMessage
	 * @throws IOException
	 */
	public Map<Integer, Integer> writeGraphMetisWeighted(String output, String errMessage, String mappingPath) throws IOException{
		// This needs to be changed to shallow Remap()
		// TODO
		deepRemap();
		
		TIntObjectMap <TIntList> graph = map;

		// Remember the g.map is actually NOT changed now
		Map <Integer, Integer> mappingInfo = remap();
		Map <Integer, Integer> reverseMapping = reverseMapping(mappingInfo);
		MapHelper mh = new MapHelper();
		mh.writeMap("newID,oldID", reverseMapping, mappingPath);
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		Writer errW = gfo.getWriter(errMessage);
		errW.write("GraphNoRemap Property: minID, maxID, no.Nodes, no.Edges+\n");
		String [] minIDmaxID = getMinAndMaxID().split(",");
		int minID = Integer.parseInt(minIDmaxID[0]);
		int maxID = Integer.parseInt(minIDmaxID[1]);
		int edgeNum = getNumOfEdges();
		int nodeNum = getNumOfNodes();
		errW.write(minID + "," + maxID +","+ edgeNum +","+ nodeNum + "\n");
		
		// write the header for metis, nodes number, edge number, and 001 to denote weighted graph
		w.write(maxID + " " + edgeNum + " " + "001" + "\n");
		
		int index = 1;
		while(index <=  maxID){
			// If we don't have this id (outliers), write a blank line and report the warning in the errW file
			// This shouldn't occur because we remapped the graph first
			
			if(!graph.containsKey(index)) {
				w.write("\n");
				// For this experiment we should not have any inconsistent nodes!
				errW.write("Inconsistent Nodes Error at:" + index);
			}
			else // if we have this id in the graph, write its neighbors
			{ 
				TIntList neighborsWeight = graph.get(index);
				StringBuilder newline = new StringBuilder();
				TIntIterator it = neighborsWeight.iterator();
				while(it.hasNext()){
					int neighbor = (int)it.next();
					int weight = (int)it.next();
					if(weight > 1) newline.append(neighbor+" " + weight+" ");
					else newline.append(neighbor+" " + "1" +" ");	
				}
				// Get rid of the last space 
				newline.substring(0, newline.length()-1);
				w.write(newline.toString()+"\n");
			}
			index++;
		}
		w.flush();
		w.close();
		errW.flush();
		errW.close();
		return reverseMapping;
	}
	
	public void writeGraphEdgeList(String header, String filepath, String delimiter) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(filepath);
		w.write(header + "\n");
		
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			
			int id, amplifiedWeight;
			while(listIterator.hasNext()){
				id = listIterator.next();
				amplifiedWeight = listIterator.next();
				double originalWeight= 1.0 * amplifiedWeight/amplifier;
				w.write(key + delimiter + id + delimiter + originalWeight + "\n");
			}	
		}
		
		w.flush();
		w.close();
	}
	
	
}