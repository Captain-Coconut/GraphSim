package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import analysis.ClusterAnalysis;
import file.GetFileOperator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class testWeight {
	public static void main(String [] args) throws IOException{
//		Map <Integer, Integer> map = new HashMap <>();
//		map.put(1,1);
//		map.put(2, 2);
//		map.put(3, 3);
//		
//		// test removing a non-existing element
//		map.remove(4);
//		System.out.println(map.toString());
//		
//		// test retrieving non-existing element
//		Integer SS = map.get(4);
//		System.out.println(SS);
//		
//		//test if iterator always give the same order
//		Set <Integer> s = new HashSet<>();
//		s.add(1);
//		s.add(2);
//		s.add(3);
//		s.add(4);
//		s.add(2);
//		Iterator it = s.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//		}
//		String a = "abt" + "\t";
//		String sss = a + "c";
//		System.out.println(sss);
//		System.out.println(a.substring(0, a.length()-1) + "c");
//		
//		System.out.println("------");
//		Map <Integer, List<Integer>> map2 = new HashMap<>();
//		List <Integer> l = new LinkedList <> ();
//		l.add(2);
//		l.add(3);
//		map2.put(1, l);
//		System.out.println(map2);
//		List <Integer> l2 = map2.get(1);
//		l2.add(2222);
//		System.out.println(map2);
//		
//		TIntList list = new TIntArrayList ();
//		list.add(1);
//		list.add(2);
//		TIntIterator itt = list.iterator();
//		while(itt.hasNext()){
//			int id = (int) itt.next();
//			System.out.println(id);
//		}
//		
//		System.out.println("Test TIntObjectMap");
//		TIntObjectMap <TIntList> tmap = new TIntObjectHashMap<>();
//		TIntList th1 = new TIntArrayList();
//		th1.add(1); th1.add(10); th1.add(2); th1.add(20);
//		tmap.put(1, th1);
//		
//		TIntList th2 = new TIntArrayList();
//		th2.add(3); th2.add(30); th2.add(4); th2.add(40);th2.add(50);
//		tmap.put(2, th2);
//		
//		int [] keys = tmap.keys();
//		
//		for(int key : keys){
//			System.out.println(tmap.get(key).size());
//		}
//		
//		// Check HashSet
//		// So it is based on values.... not reference.
//		Set <List <Integer>> setoflist = new HashSet <>();
//		
//		List <Integer> ll = new ArrayList <>();
//		ll.add(1); ll.add(2);
//		setoflist.add(ll);
//		setoflist.add(ll);
//		System.out.println(setoflist);
//		
//		//List <Integer> ll2 = new ArrayList <>();
//		//ll2.add(1); ll2.add(2);//ll2.add(3);
//		ll.add(3);
//		ll.add(4);
//		setoflist.add(ll);
//		System.out.println(setoflist);
//		
//		System.out.println("2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17".split("\\s").length);
//		
//		List <Integer> lll = Arrays.asList(1,2,3,4,5);
//		TIntList tl = new TIntArrayList ();
//		tl.addAll(lll);
//		TIntIterator it1 = tl.iterator();
//		while(it1.hasNext()){
//			System.out.println(it1.next());
//		}
//		it1 = tl.iterator();
//		while(it1.hasNext()){
//			System.out.println(it1.next());
//		}
		
//		TIntObjectMap <TIntList> map = new TIntObjectHashMap<>();
//		TIntList l1 = new TIntArrayList ();
//		l1.add(11); l1.add(12);
//		map.put(1, l1);
//		TIntList l2 = new TIntArrayList ();
//		l2.add(21); l2.add(22);
//		map.put(2, l2);
//		TIntList l3 = new TIntArrayList ();
//		l3.add(31); l3.add(32);
//		map.put(3, l3);
//		
//		TIntObjectIterator<TIntList> mapIterator = map.iterator();
//
//		while(mapIterator.hasNext()){
//			mapIterator.advance();
//
//			System.out.println(mapIterator.key());
//
//		}
		
		//test get Cluster
		//ClusterAnalysis.getClusters("IntSpacemetisLargestComponentConsec.graph.part.100000");
//		double a = 0;
//		a = 1/2;
//		System.out.println(a);
		GetFileOperator gfo = new GetFileOperator();
		System.out.println("checking... negative weights");
		BufferedReader br = gfo.getBR("largestComponent_ConsecutiveID");
		Writer w = gfo.getWriter("checkWeights");
		String line = br.readLine();
		while((line = br.readLine()) != null){
			double weight = Double.parseDouble(line.split(",")[2]);
			if(weight <= 0 )  w.write(weight + "\n");
		}
		w.flush();
		w.close();
		br.close();
	}
}
