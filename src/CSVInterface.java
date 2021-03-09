

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.io.ExportException;

import GraphML.GraphMLIO;
import delaunayTriangulation.Vector2D;
import delaunayTriangulation.Edge2D;

import java.awt.Color;

public class CSVInterface {
	
	public static void main(String[] args) 
	{
		try {
			generateStatistics("SubInstancesPOIS","Field3count50withBeta");
			generateStatistics("IpeData","seattle-restaurants");
			generateStatistics("IpeData","manhattan-subway+hotels+clinics");
			generateStatistics("GISData","standorte_uni_bonn_utm");
			//convertCSVtoGML("GISData\\standorte_uni_bonn_utm");
			//generateCSVWithILP();
			//findBestAlgorithm();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateStatistics(String location, String filename) throws IOException, ExportException
	{
		String output = "";
		
		double[] betas = {0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};
		String[] algos = {"Greedy", "ReverseGreedy"};
		
		output += "Beta,Algorithm,Clusters\n";
		
		for(double beta : betas)
		{
			for(String algo : algos)
			{
				GraphMLIO.ComponentsWrapper cw = GraphMLIO.readConnectedComponents(location + "\\Solutions\\" + filename + beta + algo + ".graphml");
				
				
				
				output += beta + "," + algo + "," + cw.clusters.size() + "\n";
			}
		}
		FileOutputStream os = new FileOutputStream(location + "\\" + filename + ".csv");
		os.write(output.getBytes());
		os.close();
		
	}
	
	public static void convertCSVtoGML(String location) throws IOException, ExportException
	{
		Scanner sc = new Scanner(new FileInputStream(location + ".csv"));
		
		Color[] colors = {
				new Color(0xFFB300), // Vivid Yellow
                new Color(0x803E75), // Strong Purple
                new Color(0xFF6800), // Vivid Orange
                new Color(0xA6BDD7), // Very Light Blue
                new Color(0xC10020), // Vivid Red
                new Color(0x00538A), // Grayish Yellow
                new Color(0x817066), // Medium Gray
                new Color(0x007D34) // Vivid Green
		};
		Map<Vector2D, Color> colorMap = new HashMap<>();
		Set<Vector2D> V = new HashSet<>();
		//skip first line
		sc.nextLine();
		
		while(sc.hasNext())
		{
			String line = sc.nextLine();
			String[] components = line.split(",");
			//System.out.println(line);
			
			if(components.length<4)
				continue;
			/*
			for(String s : components)
				System.out.print(s + " ");
			System.out.println();
			*/
			
			double x = Double.parseDouble(components[0]);
			double y = Double.parseDouble(components[1]);
			
			int faculty = Integer.parseInt(components[4]);
			
			if(faculty == 0)
				continue;
			
			Color c = colors[faculty];
			
			Vector2D v = new Vector2D(x,y);
			
			V.add(v);
			colorMap.put(v, c);
		}
		sc.close();
		GraphMLIO.writeGraphToGraphML(new GraphMLIO.GraphWrapper(V, new HashSet<Edge2D>(), colorMap), location + ".graphml");
	}
	
	public static void generateCSVWithILP() throws IOException
	{
		double[] betas = new double[9];
		
		betas[0] = 0.5;
		betas[1] = 0.55;
		betas[2] = 0.6;
		betas[3] = 0.65;
		betas[4] = 0.7;
		betas[5] = 0.75;
		betas[6] = 0.8;
		betas[7] = 0.85;
		betas[8] = 0.9;
		
		String[] algos = {/*"Greedy", "ReverseGreedy", "Reverse1PlaneGreedy",*/ "ILP2"};
		
		FileOutputStream os = new FileOutputStream("ILP2Results.csv");
		
		os.write("Algorithm,Clusters,Time,Wrong Points per Cluster,Points,Beta,Field\n".getBytes());
		
		for(int i = 0; i < 9; i ++)
		{
        	//iterate over the sizes we want to achieve
			for(int count = 50; count <= 250; count = count + 50)
			{
				for(double beta : betas)
				{
					for(String algo : algos)
					{
						//String curr = "SubInstancesPOIS\\Solutions\\Field" + i + "count" + count + "WithBeta" + beta + algo + ".txt";	
						String curr = "..\\..\\Zwischenspeicher\\Field" + i + "count" + count + "WithBeta" + beta + algo + ".txt";
						File f = new File(curr);
						if(f.exists())
						{
							resultWrapper result = readResultFile(curr);
							os.write((result.toString() + "," + count + "," + beta + "," + i +"\n").getBytes());
						}
					}
				}
			}
		}
		os.close();
	}
	
	public static void generateCSV() throws IOException
	{
		double[] betas = new double[9];
		
		betas[0] = 0.5;
		betas[1] = 0.55;
		betas[2] = 0.6;
		betas[3] = 0.65;
		betas[4] = 0.7;
		betas[5] = 0.75;
		betas[6] = 0.8;
		betas[7] = 0.85;
		betas[8] = 0.9;
		
		String[] algos = {"Greedy", "ReverseGreedy", "Reverse1PlaneGreedy"};
		
		FileOutputStream os = new FileOutputStream("ExperimentResults.csv");
		
		os.write("Algorithm,Clusters,Time,Wrong Points per Cluster,Points,Beta,Field\n".getBytes());
		
		for(int i = 0; i < 9; i ++)
		{
        	//iterate over the sizes we want to achieve
			for(int count = 50; count <= 250; count = count + 50)
			{
				for(double beta : betas)
				{
					for(String algo : algos)
					{
						String curr = "SubInstancesPOIS\\Solutions\\Field" + i + "count" + count + "WithBeta" + beta + algo + ".txt";						
						resultWrapper result = readResultFile(curr);
						os.write((result.toString() + "," + count + "," + beta + "," + i +"\n").getBytes());
					}
				}
			}
		}
		os.close();
	}
	
	public static void subCSVs() throws IOException
	{
		double[] betas = new double[9];
		
		betas[0] = 0.5;
		betas[1] = 0.55;
		betas[2] = 0.6;
		betas[3] = 0.65;
		betas[4] = 0.7;
		betas[5] = 0.75;
		betas[6] = 0.8;
		betas[7] = 0.85;
		betas[8] = 0.9;
		
		String[] algos = {"Greedy", "ReverseGreedy", "Reverse1PlaneGreedy"};
		
		FileOutputStream os = new FileOutputStream("AlgorithmComparison.csv");
		os.write("Field,Points,Beta,Best Algorithm,Best Solution\n".getBytes());
		
		for(double beta : betas)
		{
			
			for(int i = 0; i < 9; i ++)
			{
	        	//iterate over the sizes we want to achieve
				for(int count = 50; count <= 250; count = count + 50)
				{

					resultWrapper[] res = new resultWrapper[3];
					int index = 0;
					for(String algo : algos)
					{

						String curr = "SubInstancesPOIS\\Solutions\\Field" + i + "count" + count + "WithBeta" + beta + algo + ".txt";						
						resultWrapper result = readResultFile(curr);
						res[index] = result;
						index++;
						
					}
					
					int best;
					if(res[0].clusters<=res[1].clusters)
					{
						if(res[0].clusters<=res[2].clusters)
							best = 0;
						else
							best = 2;
					}
					else
					{
						if(res[1].clusters<=res[2].clusters)
							best = 1;
						else
							best = 2;
					}
					os.write((i+","+count+","+beta+","+res[best].algo+","+res[best].clusters+"\n").getBytes());
					
				}
			}
		}
		
		os.close();
	}
	
	public static void findBestAlgorithm() throws IOException
	{
		double[] betas = new double[9];
		
		betas[0] = 0.5;
		betas[1] = 0.55;
		betas[2] = 0.6;
		betas[3] = 0.65;
		betas[4] = 0.7;
		betas[5] = 0.75;
		betas[6] = 0.8;
		betas[7] = 0.85;
		betas[8] = 0.9;
		
		String[] algos = {"Greedy", "ReverseGreedy", "Reverse1PlaneGreedy"};
		
		FileOutputStream os = new FileOutputStream("BestAlgorithmCounting.csv");
		os.write("Points,Greedy,ReverseGreedy,Reverse1PlaneGreedy\n".getBytes());
		
		for(int count = 50; count <= 250; count = count + 50)
		{
			int greedyCount = 0;
			int reverseGreedyCount = 0;
			int reverse1PlaneGreedyCount = 0;
			
			for(double beta : betas)
			{
				
				for(int i = 0; i < 9; i ++)
				{
	        	//iterate over the sizes we want to achieve

					resultWrapper[] res = new resultWrapper[3];
					for(int index = 0; index < 3; index++)
					{
						String algo = algos[index];
						String curr = "SubInstancesPOIS\\Solutions\\Field" + i + "count" + count + "WithBeta" + beta + algo + ".txt";						
						resultWrapper result = readResultFile(curr);
						res[index] = result;
						
					}
					
					if(res[0].clusters<=res[1].clusters && res[0].clusters<=res[2].clusters)
						greedyCount++;
					if(res[1].clusters<=res[2].clusters && res[1].clusters<=res[0].clusters)
						reverseGreedyCount++;
					if(res[2].clusters<=res[0].clusters && res[2].clusters<=res[1].clusters)
						reverse1PlaneGreedyCount++;

				}
			}
			os.write((count+","+greedyCount+","+reverseGreedyCount+","+reverse1PlaneGreedyCount+"\n").getBytes());
		}
		
		os.close();
	}
	
	public static resultWrapper readResultFile(String location) throws IOException
	{
		Scanner sc = new Scanner(new FileInputStream(location));
		
		sc.nextLine();
		String algo = sc.nextLine();
		
		sc.nextLine();
		int clusters = sc.nextInt();
		
		sc.nextLine();
		sc.nextLine();
		//I'm using parseDouble here, because for some reason nextDouble doesn't work
		double time = Double.parseDouble(sc.nextLine());
		
		double wrongPoints;
		
		sc.nextLine();
		if(sc.hasNextLine())
		{
			wrongPoints = Double.parseDouble(sc.nextLine());
		}
		else
		{
			wrongPoints = Double.MAX_VALUE;
		}
		
		
		resultWrapper result = new resultWrapper(algo, time, clusters, wrongPoints);
		
		sc.close();
		return result;
	}
	
	static class resultWrapper
	{
		public String algo;
		public double time;
		public int clusters;
		public double wrongPoints;
		
		public resultWrapper(String algo, double time, int clusters, double wrongPoints) 
		{
			this.algo = algo;
			this.time = time;
			this.clusters = clusters;
			this.wrongPoints = wrongPoints;
		}
		
		public String toString()
		{
			return algo + "," + clusters + "," + time + "," + wrongPoints;
		}
		
		
	}
	


}
