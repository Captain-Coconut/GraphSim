package analysis;

import java.io.IOException;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import graph.WeightedGraphTroveZorder;

// Validate DataSet largestComponent_ConsecutiveID
public class DataSetCheck {	
	
	public static void main(String [] args) throws IOException{
		WeightedGraphTroveZorder wg = new WeightedGraphTroveZorder("largestComponent_ConsecutiveID",  "ReadTrack", 1);
		System.out.println("node size:" + wg.numsofnodes);
		System.out.println("edge size:" + wg.numsofedges);
		System.out.println("minID:" + wg.MIN_ID);
		System.out.println("maxID:" + wg.MAX_ID);
		
		int i = 1;
		
		TIntObjectMap <TIntList> map = wg.map;

		while(i <= wg.MAX_ID){
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
