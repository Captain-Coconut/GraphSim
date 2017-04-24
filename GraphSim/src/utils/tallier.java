package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import file.GetFileOperator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import graph.UnweightedGraph;
import graph.WeightedGraphTroveZorder;

/**
 * Tally measurements in a graph
 * @author baichuanzhou
 *
 */
public class tallier {
	
	/**
	 * Tally degree info into a file
	 * @param path
	 * @param output
	 * @throws IOException
	 */
	public static void tallyDegrees(String path, String output) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		UnweightedGraph g = new UnweightedGraph(path,0); 
		Map <Integer, Set <Integer>> map = g.map;
		w.write("degree Info\n");
		for(Map.Entry<Integer, Set <Integer>> entry : map.entrySet()){
			w.write(entry.getValue().size()+"\n");
		}
		w.flush();
		w.close();
	}
	
	/**
	 * file format: every line represent degree for a node 
	 * @param path
	 * @throws IOException
	 */
	public static void printEdgeDegree(String path, String output) throws IOException {
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(path);
		//Writer w = gfo.getWriter(output);
		Map <Integer, Integer> map = new HashMap <>();
		br.readLine();
		String line;
		int numofnodes=0;
		int degreeTotal = 0;
		while((line = br.readLine()) != null){
			int degree = Integer.parseInt(line);
			numofnodes++;
			degreeTotal+=degree;
			if (! map.containsKey(degree)) {
				map.put(degree, 1);
			} else {
				int value = map.get(degree);
				map.put(degree, ++value);
			}
		}
		System.out.println("number of nodes: " + numofnodes);
		System.out.println("average degree: " + degreeTotal/numofnodes);
		int sum =0;
		System.out.println("degree" + "   count");
		for(Map.Entry<Integer, Integer> entry:map.entrySet()){
			System.out.println(entry.getKey() + "         " + entry.getValue());
			sum += entry.getValue();
		}
		//System.out.println("number of nodes with at least one edge" + sum);	
	}
	
	public static void printComponentsSize(String input, String output) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input);
		Writer w = gfo.getWriter(output);
		String line = new String();
		int count = 0;
		w.write("Index, Size"+"\n");
		while((line = br.readLine()) != null){
			// get rid of the '[' and ']'
			line = line.substring(1, line.length()-1);
			int component_size = line.split(",").length;
			w.write(count + "," + component_size + "\n");
			count++;
		}
		br.close();
		w.flush();
		w.close();
	}
	
	public static int [] getLargeComponents(String input) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input);
		String line = new String();
		br.readLine();
		// <10, 10-30, 30-50, 50-100, >100
		int [] sizes = new int [5];
		int min = 1000000;
		int max = -1;
		while((line = br.readLine()) != null){
			int size = Integer.parseInt(line.split(",")[1]);
			if(size < min) min = size;
			if(size > max) max = size;
			if(size < 10 ) sizes[0]++;
			if(size > 10 && size < 30) sizes[1]++;
			if(size > 30 && size < 50) sizes[2]++;
			if(size > 50 && size < 100) sizes[3]++;
			if(size > 100) {
				System.out.println(size);
				sizes[4]++;
			}
			
		}
		br.close();
		//System.out.println(min);
		//System.out.println(max);
		return sizes;
	}
	
	/**
	 * find the length of the longest path using BFS
	 * @param graph
	 * @return length
	 */
	public static int findDeepestBFS(Map <Integer, Set <Integer>> graph, int start){
		int count = 0;
		Set <Integer> visited = new HashSet <>();
		Set <Integer> queue = new HashSet <>();
		queue.add(start);
		
		while(visited.size() != graph.size()){
			// Get the current 
			Set <Integer> queue_copy = new HashSet<Integer> (queue);
			Iterator <Integer> queue_it = queue_copy.iterator();
			while(queue_it.hasNext()){
				int cur = queue_it.next();
				visited.add(cur);
				Set <Integer> neighbors = graph.get(cur);
				for(Integer i : neighbors){
					if(!visited.contains(i)) queue.add(i);
				}
			}
			count++;
		}
		return count;
	}
	
	/**
	 * find approximate diameter
	 * @param input
	 * @param iteMax
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static double findApproxDiameter(String input, int iteMax, String output) throws IOException{
		UnweightedGraph g = new UnweightedGraph(input,0);
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		
		Iterator <Integer> it= g.map.keySet().iterator();
		String out = new String ("");
		int count = 0;
		
		// Starting with a random node and do a BFS
		for(int i = 0; i < iteMax; i++){
			int start = it.next();
			int len = tallier.findDeepestBFS(g.map, start);
			count += len;
			out = out + "," + len;
		}
		
		w.write(out+"\n");
		w.flush();
		w.close();
		
		// return the average
		return 1.0*count/iteMax; 
	}
	
	/**
	 * take some random sample test if:
	 * if we have edge (a,b), we must have edge (b,a)
	 * @param path
	 * @param output
	 * @throws IOException
	 */
	public static boolean testDirection(String path, String output, int iterations) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		UnweightedGraph g = new UnweightedGraph(path,0);
		Map <Integer, Set <Integer>> map = g.map;
		Iterator <Integer> it = map.keySet().iterator();
		int count = 0;
		boolean flag = true;
		while(it.hasNext()){
			//w.write("\n" + "iterations:" + count + "\n");
			int seed = it.next();
			//w.write("node id:" + seed + "\n");
			Set <Integer> neighbors = map.get(seed);
			//w.write("with neighbors" + neighbors + "\n" + "---------------" + "\n");
			for(Integer n : neighbors){
				if(!map.get(n).contains(seed)) {
					flag = false;
					w.write("errrrr!" + "\n");
					//w.write("errrrrrr!"+"\n");
				}
				//w.write("node id:" + n + " neighbors:" + map.get(n) + "\n");
			}
		}
		w.write(flag + "\n");

		w.flush();
		w.close();
		return flag;
	}
		
	public static void tallyDegree(String input, String output) throws IOException{
		WeightedGraphTroveZorder g = new WeightedGraphTroveZorder(input, input+"_ReadProgress", 1000);
		TIntObjectMap <TIntList> graph = g.map;
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(output);
		int MAX_ID = g.MAX_ID;
		int i = 1;
		int degree;
		while(i <= MAX_ID){
			degree = graph.get(i).size();
			w.write(degree+"\n");
		}
		w.flush();
		w.close();
	}
	
	public static void tallyDegreeTrove() throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR("TroveNonWeightedLargestComponent.graph");
		Writer w1 = gfo.getWriter("DegreeTroveNonWeightedLargestComponent");
		Writer w2 = gfo.getWriter("CompactDegreeTroveNonWeightedLargestComponent");
		String line = new String();
		int degree = 0;
		int minDegree = Integer.MAX_VALUE;
		int maxDegree = Integer.MIN_VALUE;
		int single = 0;
		int two2ten = 0;
		int ten225 = 0;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;		
		int count4 = 0;		
		int count5 = 0;		
		int superbig = 0;
		br.readLine();
		int index = 0;
		while((line = br.readLine())!=null){
			index++;
			degree = line.split("\\s").length;
			w1.write(degree + "\n");
			minDegree = minDegree > degree ? degree : minDegree;
			maxDegree = maxDegree < degree ? degree : maxDegree;
			if(degree < 2) single++;
			else if (degree >= 2 && degree <= 10) two2ten++;
			else if (degree > 10 && degree <= 25) ten225++;
			else if (degree > 25 && degree <= 50) count1++;
			else if (degree > 50 && degree <= 100) count2++;
			else if (degree > 100 && degree <= 500) count3++;
			else if (degree > 500 && degree <= 1000) count4++;
			else if (degree > 1000 && degree <= 5000) {
				w2.write("Found node with large degree at line " + index + ". The degree is " + degree);
				count5++;
			}
			else {
				w2.write("Found node with large degree at line " + index + ". The degree is " + degree);
				superbig++;
			}
		}
		
		w2.write("minDegree:"+minDegree + "  maxDegree" + maxDegree +"\n");
		w2.write("single:"+  + single + "\n");
		w2.write("degree >= 2 && degree <= 10:" + two2ten + "\n");
		w2.write("degree > 10 && degree <= 25:"  + ten225 + "\n");
		w2.write("degree > 25 && degree <= 50:"  + count1 + "\n");
		w2.write("degree > 50 && degree <= 100:"  + count2 + "\n");
		w2.write("degree > 100 && degree <= 500:"  + count3+ "\n");
		w2.write("degree > 500 && degree <= 1000:"  + count4 + "\n");
		w2.write("degree > 1000 && degree <= 5000:"  + count5 + "\n");
		w2.write("superbig:" + superbig + "\n");
		
		w1.flush();
		w1.close();
		w2.flush();
		w2.close();
		
	}

	
	
	
	public static void main (String []args) throws IOException{
		//tallier.tallyDegrees("all_edges_deID_OnlyIDandTotal.csv", "tallyDegree");
		//tallier.testDirection("largestTrim.csv", "testDirectionRes", 100000);
		//System.out.println(tallier.findApproxDiameter("largestTrim.csv", 100, "diameter.output"));
		//tallier.tallyDegree("largestComponent_ConsecutiveID", "DegreeInfoLargestComponent");
		tallier.tallyDegreeTrove();
	}

}
