package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import file.GetFileOperator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class UnweightedGraphTrove {

	public TIntObjectMap<TIntList> map = new TIntObjectHashMap<>();
	public int MAX_ID = Integer.MIN_VALUE;
	public int MIN_ID = Integer.MAX_VALUE;
	public int numsofnodes = 0;
	public int numsofedges = 0;

	public UnweightedGraphTrove(String filepath, String Tracker) throws IOException {

		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(filepath);
		String line;
		// Get rid of header
		br.readLine();
		int countLines = 0;
		Writer w = gfo.getWriter(Tracker);
		String[] lines;
		int id1, id2;
		while ((line = br.readLine()) != null) {

			countLines++;
			lines = line.split(",");
			id1 = Integer.parseInt(lines[0]);
			id2 = Integer.parseInt(lines[1]);

			MAX_ID = MAX_ID > Math.max(id1, id2) ? MAX_ID : Math.max(id1, id2);
			MIN_ID = MIN_ID < Math.min(id1, id2) ? MIN_ID : Math.min(id1, id2);

			TIntList th1 = map.containsKey(id1) ? map.get(id1) : new TIntArrayList();
			th1.add(id2); // this is neighbor
			map.put(id1, th1);

			TIntList th2 = map.containsKey(id2) ? map.get(id2) : new TIntArrayList();
			th2.add(id1); // this is neighbor
			map.put(id2, th2);

			w.write(countLines + "\n");

		}

		this.numsofnodes = map.size();
		this.numsofedges = countLines;
		br.close();
		w.flush();
		w.close();
	}

}
