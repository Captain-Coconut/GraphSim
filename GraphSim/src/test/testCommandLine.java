package test;

import java.io.IOException;

public class testCommandLine {
	public static void testTerminal() throws IOException{
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("touch 128384.txt");
		System.out.println(pr.toString());
	}
	
	public static void main(String [] args) throws IOException{
		testTerminal();
	}
	
}
