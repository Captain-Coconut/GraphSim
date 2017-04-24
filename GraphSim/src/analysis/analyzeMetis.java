package analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import file.GetFileOperator;

public class analyzeMetis {
	public static void main(String [] args) throws IOException{
		for(String arg: args){
			System.out.println(arg);
		}
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader brMetis = gfo.getBR(args[0]);
		BufferedReader brOriginalGraph = gfo.getBR(args[1]);
		
		int sampleSize = Integer.parseInt(args[2]);
		int metisK = Integer.parseInt(args[3]);
		String mapOutput = args[4];
		String metisLine = new String();
		int clusterID = 0, nodeID = 0;
		
		// construct the clusterID --- > nodeIDs hashmap
		Map <Integer, List <Integer>> map = new HashMap <Integer, List <Integer>>();
		while((metisLine = brMetis.readLine()) != null){
			nodeID ++ ; 
			clusterID = Integer.parseInt(metisLine);
			List<Integer> current = map.containsKey(clusterID) ? map.get(clusterID) : new ArrayList <Integer>();
			current.add(nodeID);
			map.put(clusterID, current);
		}
		
		int averageSizeOfCluster = nodeID/metisK;
		Writer mapOutputW = gfo.getWriter(mapOutput);
		//write the map
		// Go though the map to see if the size is almost centered around averageSize
		for(Map.Entry<Integer, List <Integer> > entry : map.entrySet()){
			// nodeID:[1,2,3,4,5,6]
			mapOutputW.write(entry.getKey() + ":" + entry.getValue().toString());
			// If the size is not balanced, output the size
			if(Math.abs(entry.getValue().size() - averageSizeOfCluster) > 0.05 * averageSizeOfCluster){
				System.out.println("Imbalanced Size:" + entry.getValue().size());
			}
		}
		mapOutputW.flush();
		mapOutputW.close();
		
		// Start the sampling process
		Iterator <Integer> it = map.keySet().iterator();
		for(int i = 0; i < sampleSize; i++){
			int cID = it.next();
			
			Writer w = gfo.getWriter(args[1] + "_clusterID" + cID);
			List <Integer> nodeIDs = map.get(cID);
			brOriginalGraph = gfo.getBR(args[1]);
			// Write header
			w.write(brOriginalGraph.readLine() + "\n");
			String line;
			while((line = brOriginalGraph.readLine()) != null){
				String [] lines = line.split(",");
				//We will only write a line if both IDs are in this cluster
				int id1 = Integer.parseInt(lines[0]);
				int id2 = Integer.parseInt(lines[1]);
				if(nodeIDs.contains(id1) && nodeIDs.contains(id2)) w.write(line + "\n");
			}
			
			w.flush();
			w.close();
			
		}

	}
}
