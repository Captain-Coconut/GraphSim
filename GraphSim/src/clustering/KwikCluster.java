package clustering;

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

public class KwikCluster {
	public List <TIntList> res = new ArrayList <>();
	public Writer w;
	public Random r;
	
	public KwikCluster() throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		this.w = gfo.getWriter("KwikClusterMapSizeTracker");
		this.r = new Random();
	}
	
	// THIS IS NOT USABLE because of the stack might go too deep.
	public void getKWIKCLUSTEROVERFLOW(CorrelationGraph cg) throws IOException{
		TIntObjectMap<TIntList> map = cg.map;
		int pflag = cg.pflag;
		//int nflag = cg.nflag;

		if(map.size() <= 0) return;
		this.w.write(map.size()+"\n");
		//this.w.flush();
		TIntList currentCluster = new TIntArrayList();
		
		// pick a random pivot i and add it to current cluster
		int i = this.getRandomID(map);
		currentCluster.add(i);
		
		// Get the neighboring+relation list. 
		TIntList neighborRelation = map.get(i);
		// This is not possible because we are dealing with the fully connected component
		// if(neighborRelation == null || neighborRelation.size() == 0) 
		for(int k = 1; k < neighborRelation.size(); k += 2){
			if(neighborRelation.get(k) == pflag) {
				// If the relation is positive, add the previous element(node id) to current cluster
				int nodeID = neighborRelation.get(k);
				currentCluster.add(nodeID);
			}
		}
		
		this.res.add(currentCluster);
		
		//peeling stage
		TIntIterator tii = currentCluster.iterator();
		while(tii.hasNext()){
			map.remove(tii.next());
		}
		
		this.getKWIKCLUSTEROVERFLOW(cg);
		this.w.flush();
		this.w.close();
	}
	
	public List<TIntList> getKWIKCLUSTER(CorrelationGraph cg) throws IOException {
		TIntObjectMap<TIntList> map = cg.map;
		int pflag = cg.pflag;
		// int nflag = cg.nflag;
		List<TIntList> res = new ArrayList<>();
		int nodeID = 0;
		int flag = 0;

		while (map.size() != 0) {
			// this.w.flush();
			this.w.write(map.size()+"\n");

			TIntList currentCluster = new TIntArrayList();

			// pick a random pivot i and add it to current cluster
			int i = this.getRandomID(map);
			currentCluster.add(i);

			// Get the neighboring+relation list.
			TIntList neighborRelation = map.get(i);
			// This following is not possible because we are dealing with the fully connected component
			// if(neighborRelation == null || neighborRelation.size() == 0) continue;
			
			TIntIterator tii = neighborRelation.iterator();
			while(tii.hasNext()){
				nodeID = tii.next();
				flag = tii.next();
				if(flag == pflag){
					currentCluster.add(nodeID);
				}
			}

			res.add(currentCluster);

			// peeling stage
			TIntIterator tii2 = currentCluster.iterator();
			while (tii2.hasNext()) {
				map.remove(tii2.next());
			}
		}

		this.w.flush();
		this.w.close();
		return res;
	}
	
	public int getRandomID(TIntObjectMap<TIntList> map){
		int [] IDs = map.keys();
		return IDs[r.nextInt(IDs.length)];
	}
	
	
	public static void main(String [] args) throws IOException{
		CorrelationGraph cg = new CorrelationGraph(args[0], 4.5382307086428355 , -1, -2);
		KwikCluster kc = new KwikCluster();
		List<TIntList> res = kc.getKWIKCLUSTER(cg);
		
		GetFileOperator gfo = new GetFileOperator();
		Writer w = gfo.getWriter(args[1]);
		
		for(TIntList til : res){
			w.write(til.toString()+"\n");
		}
		
		w.flush();
		w.close();
	}

}
