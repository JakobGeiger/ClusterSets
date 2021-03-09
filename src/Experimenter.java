
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.io.ExportException;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.MapFrame;

public class Experimenter {

	public static void main (String[] args)
	{
		/*
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
		
		String errors = "";
		
		for(int i = 0; i < 9; i ++)
		{
        	//iterate over the sizes we want to achieve
			for(int count = 50; count <= 250; count = count + 50)
			{
				for(double beta : betas)
				{
					String curr = "SubInstancesPOIS\\Field" + i + "count" + count + "WithBeta" + beta;
					try
					{
						doExperiments(curr);
					}
					catch(Exception e)
					{
						errors = errors + curr + "\n";
						e.printStackTrace();
					}
				}
			}
		}
		*/
		
		try
		{
			double[] betas = {0.95, 1.0};//{0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9};
			for(double beta : betas)
			{
				doExperiments("IpeData\\manhattan-subway+hotels+clinics" + beta);
				doExperiments("IpeData\\seattle-restaurants" + beta);
				doExperiments("GISData\\standorte_uni_bonn_utm" + beta);
				doExperiments("SubInstancesPOIS\\Field3count50WithBeta" + beta);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		try 
//		{
//			doExperiments("SubInstancesPOIS\\Field4count250WithBeta0.5");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExportException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
//		try
//		{
//			FileOutputStream os = new FileOutputStream("ErrorLog.txt");
//			os.write(errors.getBytes());
//			os.close();
//		}
//		catch(Exception e)
//		{
//			System.out.println("Error while generating error log (how ironic)");
//			System.out.println(errors);
//		}
		
	}
	
	public static void doExperiments(String filename) throws IOException, ExportException
	{
		GraphMLIO.GraphWrapper wrapper = GraphMLIO.readGraph(filename + ".graphml");
		
		
		
		
		int[] selection = {0,1,2};
		
		for(int c : selection)
		{
			HashSet<Vector2D> V = new HashSet<>(wrapper.V);
			HashSet<Edge2D> E = new HashSet<>(wrapper.E);
			GreedyClusterMin gcm = new GreedyClusterMin(V, E, wrapper.colorMap);
			
			String sel = "";
			double time = System.currentTimeMillis();
			switch(c)
			{
			case 0: 
				{
					gcm.runGreedy();
					sel = "Greedy";
					break;
				}
			case 1:
				{
					gcm.runReverseGreedy();
					sel = "ReverseGreedy";
					break;
				}
			case 2:
				{
					gcm.runReverse1PlaneGreedy();
					sel = "Reverse1PlaneGreedy";
					break;
				}
			}
			time = System.currentTimeMillis() - time;
			
			String res = "";
			
			double wrongPointsPerCluster = 0;
			
			Set<List<Vector2D>> clusters = gcm.getClusters();
			
			for(List<Vector2D> curr : clusters)
			{
				Set<Vector2D> temp = new HashSet<>(V);
				temp.removeAll(curr);
				wrongPointsPerCluster += (double) ConvexHullCalculator.howManyPoints(new HashSet<>(curr), temp);
			}
			
			wrongPointsPerCluster = wrongPointsPerCluster/clusters.size();
			
			res = res + "Algorithm used:\n" + sel + "\n";
			
			res = res + "Clusters:\n" + clusters.size() + "\n";
			
			res = res + "Time in ns:\n" + time + "\n";
			
			res += "Average number of \"wrong\" points in the convex hull of a cluster:\n" + wrongPointsPerCluster + "\n";
			
	
			
			
			String[] address = filename.split("\\\\");
			
			String outputLocation = "";
			
			for(int i = 0; i<address.length-1; i++)
			{
				outputLocation += address[i] + "\\";
			}
			
			outputLocation += "Solutions\\";
			outputLocation += address[address.length-1] + sel;
			outputLocation += ".txt";
			
			FileOutputStream os = new FileOutputStream(outputLocation);
			os.write(res.getBytes());
			os.close();
			
			String solutionLocation = "";
			
			for(int i = 0; i<address.length-1; i++)
			{
				solutionLocation += address[i] + "\\";
			}
			
			solutionLocation += "Solutions\\";
			solutionLocation += address[address.length-1] + sel;
			solutionLocation += ".graphml";
			
			
			try
			{
				GraphMLIO.writeGraphToGraphML(new GraphMLIO.GraphWrapper(gcm.V, gcm._E, gcm.vColors), solutionLocation);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			
		}
	}
}
