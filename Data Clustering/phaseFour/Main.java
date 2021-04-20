package phaseFour;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
	//Programming practices gotten from https://www.codejava.net/coding/10-java-core-best-practices-every-java-programmer-should-know
	  public static void main(String[] args) throws IOException 
	  { 
		//Declarations of variables run variable
		int runs = Integer.parseInt(args[3]);
		//ArrayList<Double> sseList = new ArrayList<Double>();

		int pointNumber = 0;
		int kMax = 0;
		
		String[] firstLine;
		
		File file = new File(args[0]); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		firstLine = br.readLine().split("\\s");
		pointNumber = Integer.parseInt(firstLine[0]);

		br.close();
		
		kMax = (int) Math.round(Math.sqrt(pointNumber/2));
		
		//Run the K-Means Clustering code n times (n = runs)
		for(int i = 2; i <= kMax; i++) {
			KMeans bestRun = new KMeans();
			ArrayList<KMeans> allRuns = new ArrayList<KMeans>();
			KMeans temp = new KMeans();
			System.out.println("Number of Clusters: " + i + "\n");
			for(int j = 0; j < runs; j++) {
				KMeans km = new KMeans();  
				km.setFileName(args[0]);
				km.initialRead();
				km.setClusterNumber(i);
				km.randomizeNumbers();
				km.readPoints();
				km.normalizeMinMax();
				km.randomPartitionInitialization();
				km.kMeansRun(Integer.parseInt(args[1]), Double.parseDouble(args[2]));
				allRuns.add(km);
			}
			//System.out.println("SIZE OF ALL RUNS: " + allRuns.size());
			temp = allRuns.get(0);
			for(KMeans k : allRuns) {
				if(temp.getFinalSSE() > k.getFinalSSE()) {
					bestRun = k;
				}
				else {
					bestRun = temp;
				}
			}
			//bestRun.printResult();
			//System.out.println("CH("+ i + "): " + bestRun.calculateCH(bestRun.getFinalSSE()) + "\n");
			//System.out.println("SW("+ i + "): " + bestRun.calculateSC() + "\n");
		}
	  }
}
