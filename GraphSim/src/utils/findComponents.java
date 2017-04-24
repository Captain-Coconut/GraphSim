package utils;

import graph.UnweightedGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import file.GetFileOperator;

public class findComponents{
	
	/** find connected components
	 * ignore isolated nodes, they don't appear in the map any way
	 * @param filepath
	 * @return a list of sets, each element in the list represents a connected components
	 * @throws IOException
	 */
	public static List<Set<Integer>> getConnectedComponents(String filepath) throws IOException{
		List<Set<Integer>> components = new ArrayList<>();
		UnweightedGraph g = new UnweightedGraph(filepath,0); 
		// We don't want to change the map so we create a new one
		Map <Integer, Set <Integer>> map = new HashMap<>(g.map);
		Set <Integer> visited = new HashSet <Integer> ();
		
		//Use a linked list to store the unvisited list so that we could take a random one as seed
		List <Integer> UnVisitedList = new ArrayList <Integer> (map.keySet());	
		while(map.size() != 0){
			// Just get a random start id as the initial seed
			List <Integer> seeds = new LinkedList <>();
			seeds.add(UnVisitedList.get(0));
			
			Set <Integer> component = new HashSet <Integer> ();
			
			while(seeds.size()!=0){
				int seed = seeds.get(0);
				component.add(seed);
				visited.add(seed);
				Set <Integer> neighbors = map.get(seed);
				//System.out.println(neighbors.size());
				for(Integer i : neighbors){
					if (!visited.contains(i) && !seeds.contains(i)) seeds.add(i);
					//component.add(i);
				}
				seeds.remove(0);
				map.remove(seed);
				UnVisitedList.remove(new Integer(seed));
				//UnVisitedList.remove(seed);
			}
			
			components.add(component);
		}
		
		return components;
	}
	
	/** find connected components put the nodes into the file immediately instead of calculating it first
	 * ignore isolated nodes, they don't appear in the map any way
	 * @param filepath
	 * @return a list of sets, each element in the list represents a connected components
	 * @throws IOException
	 */
	public static List<Set<Integer>> getConnectedComponentsRealTime(String filepath, String outputpath) throws IOException{
		List<Set<Integer>> components = new ArrayList<>();
		UnweightedGraph g = new UnweightedGraph(filepath,0); 
		// We don't want to change the map so we create a new one
		Map <Integer, Set <Integer>> map = new HashMap<>(g.map);
		Set <Integer> visited = new HashSet <Integer> ();
		
		//Use a list to store the unvisited list so that we could take a random one as seed
		Set <Integer> UnVisitedList = new HashSet <Integer> (map.keySet());	
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(outputpath);

		while(map.size() != 0){
			// Just get a random start id as the initial seed
			Set <Integer> seeds = new HashSet <>();
			Iterator iteratorUnvisitedList = UnVisitedList.iterator();
			seeds.add((Integer)iteratorUnvisitedList.next());
			
			Set <Integer> component = new HashSet <Integer> ();
			
			while(seeds.size()!=0){
				Iterator iteratorSeeds = seeds.iterator();
				int seed = (Integer)iteratorSeeds.next();
				component.add(seed);
				visited.add(seed);
				Set <Integer> neighbors = map.get(seed);
				
				// if we do have this node in the map
				if(neighbors != null){
					for(Integer i : neighbors){
						if (!visited.contains(i)) seeds.add(i);
						//component.add(i);
					}
				}
				seeds.remove(seed);
				map.remove(seed);
				UnVisitedList.remove(new Integer(seed));
				//UnVisitedList.remove(seed);
			}
			
			//components.add(component);
			w.write(component.toString() + "\n");
		}
		
		w.flush();
		w.close();
		return components;
	}
	
	public static void saveComponents(List<Set<Integer>> list, String path) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(path);
		for(Set <Integer> s: list){
			w.write(s.toString() + "\n");
		}
		w.flush();
		w.close();
	}
	
	public static void getLargestComponentFile(String allEdges, String componentsList, String largestGraph) throws IOException {
		// Construct the componentList first
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(componentsList);
		// It is at the second line
		String line = br.readLine();
		line = br.readLine();
		line = line.substring(1, line.length()-1);
		String [] lines = line.split(",");
		HashSet <Integer> component = new HashSet <>();
		for(String l : lines){
			component.add(Integer.parseInt(l.trim()));
		}
		br.close();
		
		BufferedReader br2 = gfo.getBR(allEdges);
		Writer w = gfo.getWriter(largestGraph);
		// write header
		w.write(br2.readLine()+"\n");
		String edgeLine = new String();
		while((edgeLine = br2.readLine()) != null){
			int id = Integer.parseInt(edgeLine.split(",")[1]);
			if(component.contains(id)){
				w.write(edgeLine + "\n");
			}
		}
		br2.close();
		w.flush();
		w.close();
	}
	

	
	
	public static void main(String [] args) throws IOException{
		List<Set<Integer>> components = findComponents.getConnectedComponentsRealTime("totalTrim_Full275_all_edges_R.csv", "componentList_NEW_hashset");
		//List<Set<Integer>> components = findComponents.getConnectedComponents("src/data/simple");
		//System.out.println(components.size());
		//for(Set<Integer> s: components){
		//	System.out.println(s.toString());
		//}
		//findComponents.saveComponents(components, "·componentsList");		
		//findComponents.getLargestComponentFile("totalTrim_Full275_all_edges_R.csv", "componentList_NEW_hashset", "LargestComponents.csv");
	}

}
