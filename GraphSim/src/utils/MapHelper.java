package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import file.GetFileOperator;

public class MapHelper {
	
	public void printMap(Map <?, ?> map){
		Iterator iter =map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

	/*
	 * @ header: if we need to write header, this will be it. If we don't, this will be null
	 */
	public boolean writeMap(String header, Map<?,?> map,String filename) throws IOException {
		GetFileOperator g=new GetFileOperator();
		Writer w=g.getWriter(filename);
		Iterator it=map.entrySet().iterator();
		if(!header.equals("null")){
			w.write(header+"\n");
		}
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			w.write(entry.getKey().toString()+","+entry.getValue().toString()+"\n");
		}
		w.close();
		return true;
	}
}
