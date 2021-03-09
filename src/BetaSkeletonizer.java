import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.io.ExportException;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;


//a circle-based brute force beta skeletonizer
public class BetaSkeletonizer {

	public Set<Vector2D> V;
	public double beta;
	public Set<Edge2D> E;
	
	public static void main(String[] args)
	{
		double[] betas = {0.95, 1.0};//{0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9};
		
		try
		{
			skeletonizeInstance("IpeData\\manhattan-subway+hotels+clinics", betas);
			skeletonizeInstance("SubInstancesPOIS\\Field3count50", betas);
			skeletonizeInstance("IpeData\\seattle-restaurants", betas);
			skeletonizeInstance("GISData\\standorte_uni_bonn_utm", betas);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void skeletonizeInstance(String location, double[] betas) throws IOException, ExportException
	{
		GraphMLIO.GraphWrapper gw = GraphMLIO.readGraph(location + ".graphml");
		
		for(double beta : betas)
		{
			BetaSkeletonizer bs = new BetaSkeletonizer(gw.V, beta);
			bs.skeletonize();
			Set<Edge2D> toRemove = new HashSet<>();
			for(Edge2D e : bs.E)
			{
				if(!gw.colorMap.get(e.a).equals(gw.colorMap.get(e.b)))
					toRemove.add(e);
			}
			bs.E.removeAll(toRemove);
			GraphMLIO.writeGraphToGraphML(new GraphMLIO.GraphWrapper(bs.V, bs.E, gw.colorMap), location + beta + ".graphml");
		}
	}
	
	public BetaSkeletonizer(Set<Vector2D> V, double beta)
	{
		this.V = V;
		this.beta = beta;
	}
	
	public void skeletonize()
	{
		E = new HashSet<Edge2D>();
		double theta = calculateTheta();
		for(Vector2D p : V)
		{
			for(Vector2D q : V)
			{
				if(p.equals(q))
					continue;
				boolean include = true;
				for(Vector2D r : V)
				{
					if(Vector2D.calculateAnglePRQ(p, r, q)>theta)
					{
						include = false;
						break;
					}
				}
				if(include)
					E.add(new Edge2D(p,q));
			}
		}
	}
	
	public double calculateTheta()
	{
		if(beta <= 1)
			return Math.PI - Math.asin(beta);
		else
			return Math.asin(1/beta);
	}
	
	
}
