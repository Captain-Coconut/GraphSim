package publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestUFOneStep {
	
	public static void main(String [] args) throws IOException{
		Graph g = new Graph(args[0], 1,  ",",  4.58, 1000, 1, 2, 6);
		Map <Integer, Set <Integer>> components = g.getComponents();
		List <Set <Integer>> bigComponents = new ArrayList <> ();
		for(Map.Entry<Integer, Set <Integer>> entry: components.entrySet()){
			if(entry.getValue().size() > 500) {
				System.out.println("Find big component of size " + entry.getValue().size());
				bigComponents.add(entry.getValue());
			}
		}
		List <Graph> listGraphs = g.getSubGraphs(g, bigComponents);
		int i = 0;
		for(Graph subg : listGraphs){
			g.writeGraphEdgeList(subg, "id1,id2,weight", "UFBigComponents" + (i++), ",");
		}
		
	}

}
