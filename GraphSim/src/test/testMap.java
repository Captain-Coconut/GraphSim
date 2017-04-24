package test;

import java.io.IOException;
import java.util.*;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import publisher.Graph;
import publisher.GraphNoRemap;
import utils.MapHelper;

public class testMap {
	// This proves that Java won't allow you to put more entries while you are iterating through the map.
	public static void testMapIteratorModifyReference(){
		Map <Integer, Integer> map = new HashMap <> ();
		map.put(1, 11);
		map.put(2, 22);
		map.put(3, 33);
		
		for(Map.Entry<Integer, Integer> entry : map.entrySet()){
			System.out.println(entry.getKey() + "," + entry.getValue());

			map.put(entry.getKey(), entry.getValue()*100);
		}
		
		System.out.println(map.get(1));
	}
	
	public static void testCreateAndfilterGraph(double threshold) throws IOException{
		
		GraphNoRemap g = new GraphNoRemap("src/data/simple3", 1, ",", Integer.MIN_VALUE, 100, 0, 1, 2);
		System.out.println(g.getNumOfNodes());
		System.out.println(g.getNumOfEdges());
		
		g.filterGraph(threshold);
		System.out.println("After filtering");
		System.out.println(g.getNumOfNodes());
		System.out.println(g.getNumOfEdges());

		
	}
	
	public static void testRemap() throws IOException{
		Graph g = new Graph("src/data/simple3", 1, ",", Integer.MIN_VALUE, 100, 0, 1, 2);
		
		TIntObjectIterator <TIntList> mapIterator = g.map.iterator();
		System.out.println("original graph");
		while(mapIterator.hasNext()){
			mapIterator.advance();
			System.out.println(mapIterator.key() + ":" + mapIterator.value());
		}
		
		Map <Integer, Integer> mapRelationship = g.remap();
		MapHelper mh = new MapHelper();
		System.out.println("remapping relationship:");
		mh.printMap(mapRelationship);
		
		mapIterator = g.map.iterator();
		System.out.println("remapped graph");
		while(mapIterator.hasNext()){
			mapIterator.advance();
			System.out.println(mapIterator.key() + ":" + mapIterator.value());
		}
		
		
		System.out.println("mapped back graph");
		g.mapBack(mapRelationship);
		mapIterator = g.map.iterator();
		System.out.println("remapped graph");
		while(mapIterator.hasNext()){
			mapIterator.advance();
			System.out.println(mapIterator.key() + ":" + mapIterator.value());
		}
		System.out.println(g.map.size());
		
		System.out.println("test subgraph");
		HashSet <Integer> s = new HashSet<>();
		s.add(100);
		s.add(5);
		s.add(102);
		Graph newG = g.getSubGraph(g, s);
		mapIterator = newG.map.iterator();
		System.out.println("remapped graph");
		while(mapIterator.hasNext()){
			mapIterator.advance();
			System.out.println(mapIterator.key() + ":" + mapIterator.value());
		}
		System.out.println(newG.map.size());
		
	}
	
	
	public static void main(String [] args) throws IOException{
		testMapIteratorModifyReference();
		
	}

}
