import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.io.ExportException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.PolygonMapObject;

public class GraphFiller {

	public static void main(String[] args)
	{
		try
		{
			double[] betas = {0.0};
			for(double beta : betas)
			{
				fillGraph("SubInstancesPOIS\\Field3count50WithBeta" + beta, "SubInstancesPOIS\\Solutions\\Field3count50WithBeta" + beta + "Greedy");
				/*
				fillGraph("GISData\\standorte_uni_bonn_utm" + beta, "GISData\\Solutions\\standorte_uni_bonn_utm" + beta + "Greedy");
				fillGraph("IpeData\\manhattan-subway+hotels+clinics" + beta, "IpeData\\Solutions\\manhattan-subway+hotels+clinics" + beta + "Greedy");
				fillGraph("IpeData\\seattle-restaurants" + beta, "IpeData\\Solutions\\seattle-restaurants" + beta + "Greedy");
				*/
			}
			/*
			double[] betas = {0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9};
			String[] algos = {"Greedy", "ReverseGreedy"};
			for(double beta : betas)
			{
				for(String algo : algos)
				{
					fillGraph("GISData\\standorte_uni_bonn_utm" + beta, "GISData\\Solutions\\standorte_uni_bonn_utm" + beta + algo);
				}
			}
			*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void fillGraph(String location1, String location2) throws IOException, ExportException
	{
		System.out.println("filling graph...");
		int minEnclose = 2;
		
		GraphMLIO.GraphWrapper gw = GraphMLIO.readGraph(location1 + ".graphml");
		GraphMLIO.ComponentsWrapper G = GraphMLIO.readConnectedComponents(location2 + ".graphml");
		
		Set<Edge2D> toAdd = new HashSet<>();
		
		Map<Edge2D, Integer> eMap = new HashMap<>();
		Map<Vector2D, Integer> vMap = new HashMap<>();
		Map<Integer, GraphMLIO.ClusterContainer> cMap = new HashMap<>();
		
		int index = 0;
		
		for(GraphMLIO.ClusterContainer cluster : G.clusters)
		{
			cMap.put(index, cluster);
			for(Vector2D v : cluster.V)
				vMap.put(v, index);
			for(Edge2D e : cluster.E)
				eMap.put(e, index);
			index++;
		}
		
		Set<Edge2D> toRemove = new HashSet<>();
		
		for(Edge2D e : gw.E)
		{
			if(!gw.colorMap.get(e.a).equals(gw.colorMap.get(e.b)))
				toRemove.add(e);
		}
		
		gw.E.removeAll(toRemove);
		
		for(Edge2D e : gw.E)
		{
			if(!(vMap.get(e.a)==vMap.get(e.b)))
				continue;
			
			boolean add = true;
			
			GraphMLIO.ClusterContainer cluster = cMap.get(vMap.get(e.a));
			Set<Edge2D> E = new HashSet<>(cluster.E);
			E.add(e);
			Set<Vector2D> V = new HashSet<>(cluster.V);
			
			if(E.size() > 0 && V.size() >= 4)
			{
				GeometryFactory gf = new GeometryFactory();
				GraphKelper.PolygonContainer polygon = GraphKelper.coveringPolygon(new GraphMLIO.ClusterContainer(V, E), gf);
				Coordinate[] coords = new Coordinate[polygon.V.size()];
				for(int j = 0; j < polygon.V.size(); j++)
				{
					coords[j] = new Coordinate(polygon.V.get(j).x, polygon.V.get(j).y);
				}
				
				Polygon p = gf.createPolygon(coords);
				
				for(GraphMLIO.ClusterContainer c : G.clusters)
				{
					if(c.V.size() >= minEnclose)
						continue;
					
					Geometry g = null;
					Set<Edge2D> _E = new HashSet<>(c.E);
					Set<Vector2D> _V = new HashSet<>(c.V);
					if(_E.size() > 0)
					{
						GraphKelper.PolygonContainer _polygon = GraphKelper.coveringPolygon(new GraphMLIO.ClusterContainer(_V, _E), gf);
						Coordinate[] _coords = new Coordinate[_polygon.V.size()];
						for(int j = 0; j < _polygon.V.size(); j++)
						{
							_coords[j] = new Coordinate(_polygon.V.get(j).x, _polygon.V.get(j).y);
						}
						
						g = gf.createPolygon(coords);
					}
					else
					{
						Vector2D v = c.V.iterator().next();
						g = gf.createPoint(new Coordinate(v.x,v.y));
					}
					if(p.contains(g))
					{
						add = false;
						break;
					}
				}
			}
			if(!add)
				continue;
			
			for(Edge2D edge : eMap.keySet())
			{
				if(Edge2D.crosses(e, edge) && !(eMap.get(edge)==vMap.get(e.a)))
				{
					add = false;
					break;
				}
			}
			
			if(add)
			{
				toAdd.add(e);
				eMap.put(e, vMap.get(e.a));
			}
		}
		
		Set<Edge2D> E = new HashSet<>();
		
		E.addAll(eMap.keySet());
		E.addAll(toAdd);
		
		GraphMLIO.GraphWrapper ret = new GraphMLIO.GraphWrapper(gw.V, E, gw.colorMap);
		
		GraphMLIO.writeGraphToGraphML(ret, location2 + "filledNew.graphml");
	}
}
