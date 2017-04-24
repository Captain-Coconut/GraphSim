package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import file.GetFileOperator;

public class Remapper {
	
	/**
	 * When dealing with connected components, the node ID assignment might be messy and 
	 * We can't directly use it in either metis or matlab
	 * So we can remap the node id to consecutive form
	 * @param input: path for input file. The first two columns have to be id1 and id2
	 * @param output: path for re-mapped file. Default path : input + "_ConsecutiveID"
	 * @param mapRec: path for the file that records the mapping. Format : "originalID(EM_ID_plus),newID". Default path : input + "_Remapping"
	 */
	public static void remap (String input, String output, String mapRec) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(input);
		if(output.equals("")) output = input + "_ConsecutiveID";
		if(mapRec.equals("")) mapRec = input + "_Remapping";
		Writer wOutput = gfo.getWriter(output);
		Writer wMapping = gfo.getWriter(mapRec);
		
		// Write header
		wOutput.write(br.readLine()+"\n");
		wMapping.write("OriginalID,newID\n");
		
		Map <Integer, Integer> map = new HashMap <Integer, Integer> ();
		int currentID = 1;
		
		String line = new String();
		while((line = br.readLine()) != null){
			String [] lines = line.split(",");
			if(lines.length < 2) System.err.println("Wrong input format");
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
			
			wOutput.write(newLine + "\n");
			
		}
		
		//Writer the mapping file
		for(Map.Entry<Integer, Integer> entry : map.entrySet()){
			wMapping.write(entry.getKey()+ "," + entry.getValue()+"\n");
		}
		
		wOutput.flush(); wOutput.close();
		wMapping.flush(); wMapping.close();
	}
	
	public static void main (String [] args) throws IOException{
		Remapper.remap(args[0], "", "");
	}

}
