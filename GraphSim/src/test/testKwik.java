package test;

import java.io.IOException;

import clustering.KwikClusterOutDated;
import gnu.trove.list.TIntList;
import graph.CorrelationGraph;

public class testKwik {
	public static void main(String [] args) throws IOException{
		KwikClusterOutDated kc = new KwikClusterOutDated();
		CorrelationGraph cg = new CorrelationGraph("src/data/CorrelationGraph", 4 , -1, -2);
//		
//		TIntObjectMap<TIntList> map = cg.map;
//		TIntObjectIterator<TIntList> toi = map.iterator();
//		while(toi.hasNext()){
//			toi.advance();
//			System.out.println("---");
//			System.out.println(toi.key());
//			System.out.println(toi.value().toString());
//			
//		}
//				
		kc.getKWIKCLUSTER(cg);
		
		for(TIntList til : kc.res){
			System.out.println(til.toString());
		}
		
	}
}
