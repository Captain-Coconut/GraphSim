package publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import file.GetFileOperator;

public class WeightFilterFile {
	
	/**
	 * Select specified columns in the csv file
	 * @param input_path: the original csv file
	 * @param output_path: the new trimmed csv file
	 * @param delimiter: usually comma
	 * @param header: 1 indicates we have a header in the csv, otherwise no
	 * @param keepColumns
	 * @throws IOException
	 */
	public void selectColumns(String input_path, String output_path, String delimiter, int header, List <Integer> keepColumns) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input_path);
		Writer w = gfo.getWriter(output_path);
		
		if(header == 1){ // If we have header, writer the header
			w.write(br.readLine() + "\n");
		}
		
		String line;
		String [] lines;
		
		while((line = br.readLine()) != null){
			lines = line.split(delimiter);
			line = ""; // assemble the new line
			for(int i : keepColumns) {
				line += (lines[i] + delimiter);
			}
			w.write(line.substring(0, line.length() - 1)+ "\n"); // remember to get rid of the last delimiter
		}
		
		br.close();
		w.flush();
		w.close();
	}
	
	public int [] filterWeightsAndUF(String input_path, int header, double threshold) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		//TODO have to find a more elegant way of re-mapping and union find
		//Right now we need to read the file twice

		// Re-mapping the IDs to a consecutive list first. Otherwise UF will consume unnecessary memory
		// Re-mapping also does the trick of filtering weights so we can get smaller graph file to read again
		WeightFilterFile wf = new WeightFilterFile();
		Map <Integer, Integer> map = wf.remap(input_path, input_path + ".Remap", threshold);
		
		// Read the new file
		BufferedReader br = gfo.getBR(input_path + ".Remap");

		if(header == 1){ // If we have header, get rid the header
			br.readLine();
		}
		
		String line;
		String [] lines;
		
		//Note the IDs start from 1 in the graph with consecutive IDs
		//However in UnionFind the IDs start from 0. ID 0 will always only have a self loop that shouldn't be counted
		//Always don't handle uf.id[0] because 0 is not in the re-mapping HashMap
		UnionFind uf = new UnionFind(map.size() + 1);
		
		while((line = br.readLine()) != null){
			lines = line.split(",");
			Integer id1 = Integer.parseInt(lines[1]);
			Integer id2 = Integer.parseInt(lines[2]);
			uf.unite(id1, id2);
			
		
		
		}
		return null;

	}
	
	/**
	 * Re-mapping a graph file to a new graph file with consecutive IDs, delimiter is set to be ","
	 * Always do weight filtering here. If not needed, set threshold to Integer.MAX_VALUE
	 * @param input: path for the original file
	 * @param output: path for the new file
	 * @param threshold: weight trimming threshold
	 * @return the re-mapping relationship in a HashMap. Key: old ID, Value: new ID
	 * @throws IOException
	 */
	public Map<Integer, Integer> remap (String input, String output, double threshold) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input);
		Writer Output = gfo.getWriter(output);
		
		// Write header
		Output.write(br.readLine()+"\n");
		
		//Pay attention that IDs start with 1!
		Map <Integer, Integer> map = new HashMap <Integer, Integer> ();
		int currentID = 1;
		
		String line = new String();
		while((line = br.readLine()) != null){
			String [] lines = line.split(",");
			if(lines.length < 2) System.err.println("Wrong input format");
			
			// If the weight is too small, ignore this line and go to the next one
			if(Double.parseDouble(lines[2]) <= threshold) continue;
			
			int id1 = Integer.parseInt(lines[0]);
			int id2 = Integer.parseInt(lines[1]);
			
			int new_id1 = map.containsKey(id1) ? map.get(id1) : currentID ++;
			map.put(id1, new_id1);
			lines[0] = new_id1 + "";
			int new_id2 = map.containsKey(id2) ? map.get(id2) : currentID ++;
			map.put(id2, new_id2);
			lines[1] = new_id2 + "";
			
			String newLine = new String();
			// Reassemble the line
			for(String s: lines){
				newLine = newLine + s + ",";
			}
			
			newLine = newLine.substring(0,newLine.length() - 1);
			
			Output.write(newLine + "\n");
			
		}
		
	
		Output.flush(); 
		Output.close();
		return map;
	}
	
		
	// analyze results of union find
	public void analyzeUF(){
		
	}
	
	
}
