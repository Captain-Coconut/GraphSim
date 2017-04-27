// ABANDONED because of different way of remapping now

//package file;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Writer;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import gnu.trove.iterator.TIntIterator;
//import gnu.trove.list.TIntList;
//import gnu.trove.map.TIntFloatMap;
//import gnu.trove.map.TIntObjectMap;
//import graph.UnweightedGraph;
//import graph.WeightedGraph;
//import graph.WeightedUndirectedGraph;
//
//public class Adapter {
//	/** Convert a unweighted graph file to Metis format
//	 * 
//	 * @param input
//	 * @param output
//	 * @throws IOException
//	 */
//	public static void convert2Metis(String input, String output) throws IOException{
//		UnweightedGraph ug = new UnweightedGraph(input,0);
//		int MAX_ID = ug.MAX_ID;
//		Map <Integer, Set <Integer>> graph = ug.map;
//		
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(output);
//		
//		int edgeNum = 0;
//		//count the number of edges in a different way
//		Iterator <Integer> it = graph.keySet().iterator();
//		while(it.hasNext()){
//			edgeNum += graph.get(it.next()).size();
//		}
//		
//		edgeNum = edgeNum/2;
//		
//		// write the header for metis
//		w.write(MAX_ID + "\t" + edgeNum + "\n");
//		
//		int index = 1;
//		while(index <=  MAX_ID){
//			// If we don't have this id (outliers), write a blank line
//			if(!graph.containsKey(index)) {
//				w.write("\n");
//			}
//			else // if we have this this id in the graph, write its neighbors
//			{ 
//				Set <Integer> neighbors = graph.get(index);
//				StringBuilder newline = new StringBuilder();
//				for(Integer i: neighbors){
//					newline.append(i+"\t");
//				}
//				// Get rid of the last table 
//				newline.substring(0, newline.length()-1);
//				w.write(newline.toString()+"\n");
//			}
//			index++;
//		}
//		w.flush();
//		w.close();
//	}
//	
//
//	/** Convert a weighted graph file to Metis format
//	 * @format description: 第一行表示顶点个数和边的条数，以及format格式为带权重图。第 i 行表示 i-1 节点连接的顶点编号，紧跟边的权重值。
//	 * @param input
//	 * @param output
//	 * @throws IOException
//	 */
//	public static void convert2MetisWeight(String input, String output) throws IOException{
//		WeightedGraph g = new WeightedGraph(input,0);
//		int MAX_ID = g.MAX_ID;
//		Map <Integer, HashMap <Integer, Double>> graph = g.map;
//		
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(output);
//		
//		int edgeNum = g.numsofedges;
//		
//		// write the header for metis, nodes number, edge number, and 001 to denote weighted graph
//		w.write(MAX_ID + "\t" + edgeNum + "\t" + "001" + "\n");
//		
//		int index = 1;
//		while(index <=  MAX_ID){
//			// If we don't have this id (outliers), write a blank line
//			if(!graph.containsKey(index)) {
//				w.write("\n");
//			}
//			else // if we have this this id in the graph, write its neighbors
//			{ 
//				HashMap <Integer, Double> neighbors = graph.get(index);
//				StringBuilder newline = new StringBuilder();
//				for(Map.Entry<Integer, Double> entry: neighbors.entrySet()){
//					// somehow metis won't process weights smaller than 1. Err message: weights must be positive
//					if(entry.getValue() > 1){ 
// 					newline.append(entry.getKey()+"\t" + entry.getValue()+"\t");
//					}
//					else newline.append(entry.getKey()+"\t" + "1" +"\t");
//				}
//				// Get rid of the last table 
//				newline.substring(0, newline.length()-1);
//				w.write(newline.toString()+"\n");
//			}
//			index++;
//		}
//		w.flush();
//		w.close();
//	}
//	
//	public static void convert2MetisWeightTroveZorder(String input, String output, String errMessage) throws IOException{
//		WeightedGraphTroveZorder g = new WeightedGraphTroveZorder(input, input+"_ReadProgress", 1000);
//		int MAX_ID = g.MAX_ID;
//		TIntObjectMap <TIntList> graph = g.map;
//		
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(output);
//		Writer errW = gfo.getWriter(errMessage);
//		errW.write("Graph Property: minID, maxID, no.Nodes, no.Edges+\n");
//		errW.write(g.MIN_ID  + ","+ g.MAX_ID +","+ g.numsofnodes +","+ g.numsofedges + "\n");
//		int edgeNum = g.numsofedges;
//		
//		// write the header for metis, nodes number, edge number, and 001 to denote weighted graph
//		w.write(MAX_ID + "\t" + edgeNum + "\t" + "001" + "\n");
//		
//		int index = 1;
//		while(index <=  MAX_ID){
//			// If we don't have this id (outliers), write a blank line
//			if(!graph.containsKey(index)) {
//				w.write("\n");
//				// For this experiment we should not have any inconsistent nodes!
//				errW.write("Inconsistent Nodes Error at:" + index);
//			}
//			else // if we have this this id in the graph, write its neighbors
//			{ 
//				TIntList neighborsWeight = graph.get(index);
//				StringBuilder newline = new StringBuilder();
//				TIntIterator it = neighborsWeight.iterator();
//				while(it.hasNext()){
//					int neighbor = (int)it.next();
//					float weight = ((float)it.next())/1000;
//					if(weight > 1) newline.append(neighbor+"\t" + weight+"\t");
//					else newline.append(neighbor+"\t" + "1" +"\t");	
//				}
//				// Get rid of the last table 
//				newline.substring(0, newline.length()-1);
//				w.write(newline.toString()+"\n");
//			}
//			index++;
//		}
//		w.flush();
//		w.close();
//		errW.flush();
//		errW.close();
//	}
//	
//	public static void convert2MetisWeightTroveZorderSpaceIntWeight(String input, String output, String errMessage) throws IOException{
//		WeightedGraphTroveZorder g = new WeightedGraphTroveZorder(input, input+"_ReadProgress", 1000);
//		int MAX_ID = g.MAX_ID;
//		TIntObjectMap <TIntList> graph = g.map;
//		
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(output);
//		Writer errW = gfo.getWriter(errMessage);
//		errW.write("Graph Property: minID, maxID, no.Nodes, no.Edges+\n");
//		errW.write(g.MIN_ID  + ","+ g.MAX_ID +","+ g.numsofnodes +","+ g.numsofedges + "\n");
//		int edgeNum = g.numsofedges;
//		
//		// write the header for metis, nodes number, edge number, and 001 to denote weighted graph
//		w.write(MAX_ID + " " + edgeNum + " " + "001" + "\n");
//		
//		int index = 1;
//		while(index <=  MAX_ID){
//			// If we don't have this id (outliers), write a blank line
//			if(!graph.containsKey(index)) {
//				w.write("\n");
//				// For this experiment we should not have any inconsistent nodes!
//				errW.write("Inconsistent Nodes Error at:" + index);
//			}
//			else // if we have this this id in the graph, write its neighbors
//			{ 
//				TIntList neighborsWeight = graph.get(index);
//				StringBuilder newline = new StringBuilder();
//				TIntIterator it = neighborsWeight.iterator();
//				while(it.hasNext()){
//					int neighbor = (int)it.next();
//					int weight = (int)it.next();
//					if(weight > 1) newline.append(neighbor+" " + weight+" ");
//					else newline.append(neighbor+" " + "1" +" ");	
//				}
//				// Get rid of the last space 
//				newline.substring(0, newline.length()-1);
//				w.write(newline.toString()+"\n");
//			}
//			index++;
//		}
//		w.flush();
//		w.close();
//		errW.flush();
//		errW.close();
//	}
//	
//	public static void convert2MetisWeightTroveZorderSpaceNoWeight(String input, String output, String errMessage) throws IOException{
//		WeightedGraphTroveZorder g = new WeightedGraphTroveZorder(input, input+"_ReadProgress", 1000);
//		int MAX_ID = g.MAX_ID;
//		TIntObjectMap <TIntList> graph = g.map;
//		
//		GetFileOperator gfo = new GetFileOperator();
//		Writer w = gfo.getWriter(output);
//		Writer errW = gfo.getWriter(errMessage);
//		errW.write("Graph Property: minID, maxID, no.Nodes, no.Edges+\n");
//		errW.write(g.MIN_ID  + ","+ g.MAX_ID +","+ g.numsofnodes +","+ g.numsofedges + "\n");
//		int edgeNum = g.numsofedges;
//		
//		// write the header for metis, nodes number, edge number, and 001 to denote weighted graph
//		w.write(MAX_ID + " " + edgeNum + "\n");
//		
//		int index = 1;
//		while(index <=  MAX_ID){
//			// If we don't have this id (outliers), write a blank line
//			if(!graph.containsKey(index)) {
//				w.write("\n");
//				// For this experiment we should not have any inconsistent nodes!
//				errW.write("Inconsistent Nodes Error at:" + index);
//			}
//			else // if we have this this id in the graph, write its neighbors
//			{ 
//				TIntList neighborsWeight = graph.get(index);
//				StringBuilder newline = new StringBuilder();
//				TIntIterator it = neighborsWeight.iterator();
//				while(it.hasNext()){
//					int neighbor = (int)it.next();
//					int weight = (int)it.next();
//					if(weight > 1) newline.append(neighbor+" ");
//					else newline.append(neighbor+" ");	
//				}
//				// Get rid of the last space 
//				newline.substring(0, newline.length()-1);
//				w.write(newline.toString()+"\n");
//			}
//			index++;
//		}
//		w.flush();
//		w.close();
//		errW.flush();
//		errW.close();
//	}
//	
//	public void testEdgesInMetisFile() throws IOException{
//		GetFileOperator gfo = new GetFileOperator();
//		BufferedReader br = gfo.getBR("metisLargestComponentConsec.graph");
//		br.readLine();
//		String line = new String();
//		int count = 0;
//		while((line = br.readLine())!=null){
//			String [] lines = line.split(" ");
//			count += lines.length/2;
//		}
//		System.out.println(count);
//		br.close();
//	}
//	
//	
//	public static void main (String [] args) throws IOException {
//		Adapter.convert2MetisWeightTroveZorderSpaceNoWeight("largestComponent_ConsecutiveID", "TroveNonWeightedLargestComponent.graph", "errorNEW");
//	}
//}
