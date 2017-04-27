package machineLearning;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.*;
import net.sf.javaml.tools.data.FileHandler;

public class test {
	public static void main(String[] args) throws IOException{
		String line = "[17638915, 17638914, 17638916]";
		line = line.substring(1, line.length()-1);
		String [] lines = line.split(",");
		HashSet <Integer> component = new HashSet <>();
		for(String l : lines){
			component.add(Integer.parseInt(l.trim()));
		}
	}
}
