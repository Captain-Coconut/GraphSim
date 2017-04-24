package utils;

import java.io.BufferedReader;
import java.io.IOException;

import file.GetFileOperator;

public class analyzeComponent {
	public static void main(String []args) throws IOException{
		GetFileOperator gfo = new GetFileOperator();
		BufferedReader br = gfo.getBR(args[0]);
		String line;
		int count1 = 0, count2 = 0, count3 = 0;
		int count4_10 = 0, count10_50 = 0, count50_100 = 0;
		int count100_500 = 0, count500_1000 = 0, countlarger1000 = 0;
		int max = Integer.MIN_VALUE;
		while((line = br.readLine()) != null){
			int size = line.split(",").length;
			max = size > max ? size : max;
			if(size == 1) count1++;
			else if(size == 2) count2++;
			else if(size == 3) count3++;
			else if(size >=4 && size < 10) count4_10++;
			else if(size >= 10 && size < 50) count10_50++;
			else if(size >= 50 && size < 100) count50_100++;
			else if(size >= 100 && size < 500) count100_500++;			
			else if(size >= 500 && size < 1000) count500_1000++;
			else if(size >= 1000) countlarger1000++;
		}
		System.out.println("count1:" + count1);
		System.out.println("count2:" + count2);
		System.out.println("count3:" + count3);
		System.out.println("count4_10:" + count4_10);
		System.out.println("count10_50:" + count10_50);
		System.out.println("count50_100:" + count50_100);
		System.out.println("count100_500:" + count100_500);
		System.out.println("count500_1000:" + count500_1000);
		System.out.println("countlarger1000:" + countlarger1000);
		System.out.println("max:" + max);
	}
}
