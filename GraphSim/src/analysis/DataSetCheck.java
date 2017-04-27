package analysis;

import java.io.IOException;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import graph.WeightedUndirectedGraph;

// Validate DataSet largestComponent_ConsecutiveID
public class DataSetCheck {	
	
	public static void main(String [] args) throws IOException{
		WeightedUndirectedGraph wg = new WeightedUndirectedGraph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		System.out.println("node size:" + wg.getNumOfNodes());
		System.out.println("edge size:" + wg.getNumOfEdges());
		System.out.println("minID:" + wg.getMinID());
		System.out.println("maxID:" + wg.getMaxID());
		
		int i = 1;
		
		TIntObjectMap <TIntList> map = wg.map;

		while(i <= wg.getMaxID()){
			if(!map.containsKey(i)) {
				System.out.println("ID not Consecutive at:" + i);
			} else{
				TIntList tl = map.get(i);
				if(tl.size() % 2 != 0) System.out.println("Zorder Not right at:" + i + " with size:" + tl.size());
				if(tl.size() <= 1) System.out.println("neighbors IDs not right at:" + i + " with size:" + tl.size());
				TIntIterator tli = tl.iterator();
				while(tli.hasNext()){
					int id = tli.next();
					int weight = tli.next();
					if(weight < 0) System.out.println("Weight negative at index:" + i + ", weight: " + weight);
				}
			}
			i++;
		}
		
	}


}
