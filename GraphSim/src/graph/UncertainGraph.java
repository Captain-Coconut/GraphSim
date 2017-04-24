package graph;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import file.GetFileOperator;

public class UncertainGraph {
	
	private Map <Integer, Map <Integer, Double>> ajList = new HashMap <Integer, Map <Integer, Double>>();
	public int Max_ID = Integer.MIN_VALUE;
	
	//TODO This is wrong!!!
	/**
	 * Construct an uncertain graph from a local file
	 * file format: id1	id2	probability
	 * We don't consider weights now so it doesn't matter what comes in the fourth column
	 * @param filepath
	 * @throws IOException
	 */
	public UncertainGraph(String filepath) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		//Map <Integer, Map <Integer, Double>> hm = new HashMap<Integer, Map <Integer, Double>> ();
		while((line = br.readLine()) != null){
			String [] lines = line.split("\t",-1);
			Map <Integer, Double> m = new HashMap <Integer, Double>();
			m.put(Integer.parseInt(lines[1]), Double.parseDouble(lines[2]));
			ajList.put(Integer.parseInt(lines[0]), m);
		}
		br.close();
	}
	
	/**
	 * Get the ajacency matrix from an uncertain graph ug
	 * @param ug
	 * @return
	 */
	public double [][] getAdjMatrix(UncertainGraph ug){
		return null;
	}
	
	/**
	 * Get the transition matrix from an uncertain graph ug
	 * @param ug
	 * @return
	 */
	//TODO
	public double [][] getTransitionMatrix(UncertainGraph ug){
		return null;
	}
		
	/**
	 * Test
	 * @param args
	 * @throws IOException
	 */
	public static void main(String [] args) throws IOException{
		final long startTime = System.currentTimeMillis();
		UncertainGraph ug = new UncertainGraph("src/data/KroganMIPSprgraph.txt");
		final long endTime = System.currentTimeMillis();
		System.out.println((endTime- startTime));
		System.out.println(ug.ajList.size());
	}
	
	
}
