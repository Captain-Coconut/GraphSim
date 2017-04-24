package publisher;

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

public class Graph {
	
	public TIntObjectMap <TIntList> map = new TIntObjectHashMap<>();
	public int amplifier;

	/**
	 * Select specified columns in the csv file -- Merged into createGraph
	 * @param input_path: the original csv file
	 * @param output_path: the new trimmed csv file
	 * @param delimiter: usually comma
	 * @param header: 1 indicates we have a header in the csv, otherwise no
	 * @param keepColumns
	 * @throws IOException
	 */
	public void selectColumns(String input_path, String output_path, String delimiter, int header,
			List<Integer> keepColumns) throws IOException {
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input_path);
		Writer w = gfo.getWriter(output_path);

		if (header == 1) { // If we have header, writer the header
			w.write(br.readLine() + "\n");
		}

		String line;
		String[] lines;

		while ((line = br.readLine()) != null) {
			lines = line.split(delimiter);
			line = ""; // assemble the new line
			for (int i : keepColumns) {
				line += (lines[i] + delimiter);
			}
			w.write(line.substring(0, line.length() - 1) + "\n");
		}

		br.close();
		w.flush();
		w.close();
	}

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
	public Graph(String input_path, int header, String delimiter, double threshold, int amplifier, int id1_index, int id2_index, int weight_index) throws IOException {
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
	public Graph(TIntObjectMap <TIntList> map, int amplifier){
		this.amplifier = amplifier;
		this.map = map;
	}
	
	/**
	 * Constructor used when we only want to use the functions.
	 */
	public Graph(){
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
	
	public Set <Integer> getNodesSet(){
		Set <Integer> s = new HashSet <>();
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			s.add(mapIterator.key());
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			while(listIterator.hasNext()){
				s.add(listIterator.next());
				listIterator.next();
			}
		}
		return s;
	}
	
	/**
	 * filter the edges in the graph with low weights
	 * @param threshold
	 */
	public void filterGraph(double threshold){
		threshold *= this.amplifier; //remember to amplify the threshold
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
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
			map.put(key, newNeighborsAndWeight);
			
		}
		
	}
	
	/**
	 * re-map the graph to a representation with consecutive IDs
	 * It seems really expensive.... But it is necessary
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
	
	/**
	 * mapBack the new IDs to the original IDs.
	 * Before using this function, we need to make sure that we are having a re-mapped graph with consecutive IDs
	 * @param mapping: mapping relationship with OldID:NewID. So we need to reverse it first
	 */
	public Map <Integer, Integer> mapBack(Map<Integer, Integer> mapping){
		
		// Reverse the relationship
		Map <Integer, Integer> mapRelationship = new HashMap <Integer, Integer> ();
		for(Map.Entry<Integer, Integer> entry : mapping.entrySet()){
			mapRelationship.put(entry.getValue(), entry.getKey());
		}
		
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
	
	// We assume that the Node IDs are consecutive now starting from ********** 1 ************
	// uf.id[0] will always be 0, which we won't process
	public Map <Integer, Set <Integer>> getComponents(){
		int MAXID = Integer.parseInt(getMinAndMaxID().split(",")[1]);
		int N = MAXID + 1;
		UnionFind uf = new UnionFind(N);
		
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
						
			while(listIterator.hasNext()){
				uf.unite(key, listIterator.next()); // unite the IDs
				listIterator.next(); // skip the weight
			}
		}
		
		// Go through the array again to find root for every node.
		for(int i = 0; i < N; i++){
			uf.id[i] = uf.root(i);
		}
		
		// Put components in a HashMap
		Map <Integer, Set <Integer>> components = new HashMap <>();
		for(int i = 0; i < N; i++){
			Set <Integer> hs = components.containsKey(uf.id[i]) ? components.get(uf.id[i]) : new  HashSet <Integer> ();
			hs.add(i);				
			components.put(uf.id[i], hs);
		}

		return components;
	}
	
	public Graph getSubGraph(Graph g, Set <Integer> component) {
		
		TIntObjectMap <TIntList> newMap = new TIntObjectHashMap<>();
		
		TIntObjectIterator <TIntList> mapIterator = g.map.iterator();
		
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
		
		return new Graph(newMap, g.amplifier);
		
	}
	
	public List <Graph> getSubGraphs(Graph g, List <Set <Integer>> listOfComponents){
		List <Graph> res = new ArrayList <>();
		Graph gt = new Graph();
		for(Set <Integer> component : listOfComponents){
			res.add(gt.getSubGraph(g, component));
		}
		return res;
	}
	
	/**
	 * Get minID and maxID in the graph
	 * @param g
	 * @return "minID,maxID"
	 */
	public String getMinAndMaxID(){
		TIntObjectIterator <TIntList> mapIterator = map.iterator();
		int currentMAXID = Integer.MIN_VALUE;		
		int currentMINID = Integer.MAX_VALUE;

		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int id1 = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
						
			int id2;
			while(listIterator.hasNext()){
				id2 = listIterator.next();
				listIterator.next(); // get rid of weight
				currentMAXID = id1 > currentMAXID ? id1 : currentMAXID;
				currentMAXID = id2 > currentMAXID ? id2 : currentMAXID;
				currentMINID = id1 < currentMINID ? id1 : currentMINID;
				currentMINID = id2 < currentMINID ? id2 : currentMINID;
			}
		}
		return currentMINID + "," + currentMAXID;
	}
	
	/**
	 * Write three files: Metis graph file, Error Message, Mapping information(to map back to original IDs in the future)
	 * @param g
	 * @param output
	 * @param errMessage
	 * @throws IOException
	 */
	public void writeGraphMetisWeighted(Graph g, String output, String errMessage, String mapping) throws IOException{
		TIntObjectMap <TIntList> graph = g.map;
		// MAKE SURE THE IDS are CONSECUTIVE!!!
		Map <Integer, Integer> mappingInfo = g.remap();
		MapHelper mh = new MapHelper();
		mh.writeMap("oldID,newID", mappingInfo, mapping);
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		Writer errW = gfo.getWriter(errMessage);
		errW.write("Graph Property: minID, maxID, no.Nodes, no.Edges+\n");
		String [] minIDmaxID = g.getMinAndMaxID().split(",");
		int minID = Integer.parseInt(minIDmaxID[0]);
		int maxID = Integer.parseInt(minIDmaxID[1]);
		int edgeNum = g.getNumOfEdges();
		int nodeNum = g.getNumOfNodes();
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
			else // if we have this this id in the graph, write its neighbors
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
	}
	
	public void writeGraphEdgeList(Graph g, String header, String filepath, String delimiter) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(filepath);
		w.write(header + "\n");
		
		TIntObjectIterator <TIntList> mapIterator = g.map.iterator();
		
		while(mapIterator.hasNext()){
			mapIterator.advance();
			
			int key = mapIterator.key();
			TIntList neighborsAndWeight = mapIterator.value();
			TIntIterator listIterator = neighborsAndWeight.iterator();
			
			int id, amplifiedWeight;
			while(listIterator.hasNext()){
				id = listIterator.next();
				amplifiedWeight = listIterator.next();
				double originalWeight= 1.0 * amplifiedWeight/g.amplifier;
				w.write(key + delimiter + id + delimiter + originalWeight + "\n");
			}	
		}
		
		w.flush();
		w.close();
	}
	
	
}