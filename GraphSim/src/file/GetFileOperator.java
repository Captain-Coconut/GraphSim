package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class GetFileOperator {
	public BufferedReader getBR(String filename) throws FileNotFoundException{
		FileInputStream stream = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		return br;
	}
	
	public Writer getWriter(String filename) throws FileNotFoundException{
		File outfile=new File(filename);
        FileOutputStream outputStream = new FileOutputStream(outfile);
        OutputStreamWriter osw = new OutputStreamWriter(outputStream);    
        Writer outputwriter = new BufferedWriter(osw);
		return outputwriter;
	}
}
