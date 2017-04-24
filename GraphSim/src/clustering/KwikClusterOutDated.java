package clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import file.GetFileOperator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import graph.CorrelationGraph;

public class KwikClusterOutDated {
	public List <TIntList> res = new ArrayList <>();
	public Writer w;
	
	public KwikClusterOutDated() throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		this.w = gfo.getWriter("KwikClusterMapSizeTracker");
	}
	
	public void getKWIKCLUSTER(CorrelationGraph cg) throws IOException{
		TIntObjectMap<TIntList> map = cg.map;
		int pflag = cg.pflag;
		int nflag = cg.nflag;

		if(map.size() <= 0) return;
		this.w.write(map.size()+"\n");
		this.w.flush();
		TIntList currentCluster = new TIntArrayList();
		
		// pick a random pivot i and add it to current cluster
		int i = this.getRandomID(map);
		currentCluster.add(i);
		//System.out.println("Random Pivot ID:" + i);
		
		for(int j : map.keys()){
			//System.out.println("-------Getting the flag of edge " + i + " to " + j);
			if(this.getLabel(map, i, j, pflag, nflag) == true) {
				// Add j to current cluster and remove it from original graph
				//System.out.println("True");
				currentCluster.add(j);
			}
		}
		this.res.add(currentCluster);
		
		//peeling stage
		TIntIterator tii = currentCluster.iterator();
		while(tii.hasNext()){
			map.remove(tii.next());
		}
		
		this.getKWIKCLUSTER(cg);
	}
	
	public int getRandomID(TIntObjectMap<TIntList> map){
		Random r = new Random();
		int [] IDs = map.keys();
		return IDs[r.nextInt(IDs.length)];
	}
	
	public boolean getLabel(TIntObjectMap<TIntList> map, int i, int j, int pflag, int nflag){
		// Please note after the removal of pivot points, the graph's map might not contain both i and j anymore
		// i.e. the graph might not indicate bi-direction any more.
		// So as long as one list has the information, it is enough
		TIntList listI = map.containsKey(i) ? map.get(i) : new TIntArrayList();
		TIntList listJ = map.containsKey(j) ? map.get(j) : new TIntArrayList();
		//System.out.println("neighboring list of " + i + ":" + listI);
		//System.out.println("neighboring list of " + j + ":" + listJ);

		int flag;
		
		boolean f1;
		int index1 = listI.indexOf(j);
		//System.out.println(index1);
		if(index1 == -1) {// if it is -1, then j is NOT in i's neighboring list
			f1 = false;
		} else{
			flag = listI.get(index1 + 1);
			f1 = (flag == pflag);
		}
		
		boolean f2;
		int index2 =listJ.indexOf(i);
		//System.out.println(index2);
		if(index2 == -1) {// if it is -1, then j is NOT in i's neighboring list
			f2 = false;
		} else{
			flag = listJ.get(index2 + 1);
			f2 = (flag == pflag);
		}
		
		return f1 || f2;
	}
	
	public static void main(String [] args) throws IOException{
		CorrelationGraph cg = new CorrelationGraph(args[0], 4.5382307086428355 , -1, -2);
		KwikClusterOutDated kc = new KwikClusterOutDated();
		kc.getKWIKCLUSTER(cg);
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(args[1]);
		
		for(TIntList til : kc.res){
			w.write(til.toString()+"\n");
		}
		
		w.flush();
		w.close();
	}

}
