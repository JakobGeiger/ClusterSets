import java.io.FileOutputStream;
import java.io.IOException;
import delaunayTriangulation.*;
import GraphML.GraphMLIO;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class ILPFileCompleter {
	public static void main(String[] args)
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
		
		String errors = "";
		
		for(int i = 0; i < 9; i ++)
		{
        	//iterate over the sizes we want to achieve
			for(int count = 50; count <= 100; count = count + 50)
			{
				for(double beta : betas)
				{
					String curr = "SubInstancesPOIS\\Solutions\\Field" + i + "count" + count + "WithBeta" + beta + "ILP";
					try
					{
						completeILPFile(curr);
					}
					catch(Exception e)
					{
						errors = errors + curr + "\n";
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("Errors:\n" + errors);
	}
	
	public static void completeILPFile(String file) throws IOException
	{	
		List<Set<Vector2D>> clusters = GraphMLIO.getConnectedComponents(file + ".graphml");
		
		int cumul = 0;
		Set<Vector2D> V = new HashSet<>();
		
		for(Set<Vector2D> curr : clusters)
		{
			V.addAll(curr);
		}
		
		for(Set<Vector2D> curr : clusters)
		{
			Set<Vector2D> temp = new HashSet<>(V);
			temp.removeAll(curr);
			cumul += ConvexHullCalculator.howManyPoints(curr, temp);
		}
		
		double ppc = ((double) cumul)/clusters.size();
		
		FileOutputStream os = new FileOutputStream(file + ".txt", true);
		os.write((""+ppc).getBytes());
		os.close();
	}

}
