package GraphML;

import java.awt.Color;

import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.io.GraphMLExporter.*;
import org.jgrapht.util.*;

import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.io.*;
import java.util.*;

/*this class handles the im- and export of graphs via GraphML.
 * Code taken and adapted from https://github.com/jgrapht/jgrapht/blob/master/jgrapht-demo/src/main/java/org/jgrapht/demo/GraphMLDemo.java
 */
public class GraphMLIO 
{
	
	static class CustomVertex
	{
		public Color color;
		public double x;
		public double y;
		public String id;
		
		public CustomVertex(double x, double y, Color color, String id)
		{
			this.x = x;
			this.y = y;
			this.color = color;
			this.id = id;
		}
		
		@Override
	    public String toString() {
	        return "Vertex[" + x + ", " + y + ", " + color.toString() + "]";
	    }
	    
		@Override
		public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CustomVertex other = (CustomVertex) obj;
            if (id == null) {
                return other.id == null;
            } else {
                return id.equals(other.id);
            }
        }
	    
	    @Override
	    public int hashCode()
	    {
	    	/*
	    	 * we floor x,y and use a pairing function to minimize collisions among non-equal points.
	    	 * this way, only points that lie within the same 1x1 square are assigned the same hash value
	    	 */
	    	int xTmp = (int) Math.floor(x);
	    	int yTmp = (int) Math.floor(y);
	    	
	    	int tmp = (yTmp + ((xTmp+1)/2));
	    	return xTmp + (tmp*tmp);
	    }
	}
	
	private static GraphExporter<CustomVertex, DefaultEdge> createExporter()
    {
        /*
         * Create vertex id provider.
         *
         * The exporter needs to generate for each vertex a unique identifier.
         */
        ComponentNameProvider<CustomVertex> vertexIdProvider = v -> v.id;

        /*
         * Create vertex label provider.
         *
         * The exporter may need to generate for each vertex a (not necessarily unique) label. If
         * null the exporter does not output any labels.
         */
        ComponentNameProvider<CustomVertex> vertexLabelProvider = null;

        /*
         * The exporter may need to generate for each vertex a set of attributes. Attributes must
         * also be registered as shown later on.
         */
        ComponentAttributeProvider<CustomVertex> vertexAttributeProvider = v -> {
            Map<String, Attribute> m = new HashMap<>();
            if (v.color != null) {
                m.put("color", DefaultAttribute.createAttribute(v.color.getRGB()));
            }
            m.put("name", DefaultAttribute.createAttribute("node-" + v.id));
            m.put("x", DefaultAttribute.createAttribute(v.x));
            m.put("y", DefaultAttribute.createAttribute(v.y));
            
            return m;
        };

        /*
         * Create edge id provider.
         *
         * The exporter needs to generate for each edge a unique identifier.
         */
        ComponentNameProvider<DefaultEdge> edgeIdProvider =
            new IntegerComponentNameProvider<>();

        /*
         * Create edge label provider.
         *
         * The exporter may need to generate for each edge a (not necessarily unique) label. If null
         * the exporter does not output any labels.
         */
        ComponentNameProvider<DefaultEdge> edgeLabelProvider = null;

        /*
         * The exporter may need to generate for each edge a set of attributes. Attributes must also
         * be registered as shown later on.
         */
        ComponentAttributeProvider<DefaultEdge> edgeAttributeProvider = e -> {
            Map<String, Attribute> m = new HashMap<>();
            m.put("name", DefaultAttribute.createAttribute(e.toString()));
            return m;
        };

        /*
         * Create the exporter
         */
        GraphMLExporter<CustomVertex,
            DefaultEdge> exporter = new GraphMLExporter<>(
                vertexIdProvider, vertexLabelProvider, vertexAttributeProvider, edgeIdProvider,
                edgeLabelProvider, edgeAttributeProvider);

        /*
         * Set to *not* export the internal edge weights
         */
        exporter.setExportEdgeWeights(false);

        /*
         * Register additional color attribute for vertices
         */
        exporter.registerAttribute("color", AttributeCategory.NODE, AttributeType.INT);
        exporter.registerAttribute("x", AttributeCategory.NODE, AttributeType.DOUBLE);
        exporter.registerAttribute("y", AttributeCategory.NODE, AttributeType.DOUBLE);
        
        
        /*
         * Register additional name attribute for vertices and edges
         */
        exporter.registerAttribute("name", AttributeCategory.ALL, AttributeType.STRING);

        return exporter;
    }
	
	/**
     * Create the importer
     */
    private static GraphImporter<CustomVertex, DefaultEdge> createImporter()
    {
        /*
         * Create vertex provider.
         *
         * The importer reads vertices and calls a vertex provider to create them. The provider
         * receives as input the unique id of each vertex and any additional attributes from the
         * input stream.
         */
        VertexProvider<CustomVertex> vertexProvider = (id, attributes) -> {
            //CustomVertex cv = new CustomVertex(id);
        	Color c = null;
        	double x = 0.;
        	double y = 0.;

            // read color from attributes map
            if (attributes.containsKey("color")) {
                c = new Color(Integer.parseInt(attributes.get("color").getValue()));
            }
            
            if (attributes.containsKey("x")) {
            	x = Double.parseDouble(attributes.get("x").getValue());
            }
            
            if (attributes.containsKey("y")) {
            	y = Double.parseDouble(attributes.get("y").getValue());
            }
            
            CustomVertex cv = new CustomVertex(x,y,c,id);

            return cv;
        };

        /*
         * Create edge provider.
         *
         * The importer reads edges from the input stream and calls an edge provider to create them.
         * The provider receives as input the source and target vertex of the edge, an edge label
         * (which can be null) and a set of edge attributes all read from the input stream.
         */
        EdgeProvider<CustomVertex, DefaultEdge> edgeProvider =
            (from, to, label, attributes) -> new DefaultEdge();

        /*
         * Create the graph importer with a vertex and an edge provider.
         */
        GraphMLImporter<CustomVertex, DefaultEdge> importer =
            new GraphMLImporter<>(vertexProvider, edgeProvider);

        return importer;
    }
	
	public static void writeGraphToGraphML (Set<Vector2D> V, Set<Edge2D> E, Map<Vector2D, Color> cMap, FileOutputStream os) throws ExportException
	{
		
		Graph<CustomVertex, DefaultEdge> graph = new DefaultUndirectedGraph<CustomVertex,DefaultEdge>(null, SupplierUtil.createDefaultEdgeSupplier(), false);
		
		int id = 0;
		Map<Vector2D, CustomVertex> map = new HashMap<Vector2D, CustomVertex>();
		
		for(Vector2D cur : V)
		{
			CustomVertex ver = new CustomVertex(cur.x, cur.y, cMap.get(cur), String.valueOf(id++));
			map.put(cur, ver);
			graph.addVertex(ver);
		}
		
		for(Edge2D cur : E)
		{
			graph.addEdge(map.get(cur.a), map.get(cur.b));
		}
		
		
			
		GraphExporter<CustomVertex, DefaultEdge> exporter = createExporter();
        // export as string
        exporter.exportGraph(graph, os);
		
	
		
		
	}
	
	public static void writeGraphToGraphML(GraphWrapper gw, String location) throws ExportException, IOException
	{
		FileOutputStream os = new FileOutputStream(location);
		writeGraphToGraphML(gw.V, gw.E, gw.colorMap,os);
		os.close();
	}
	
	public static Graph<CustomVertex, DefaultEdge> readGraph(FileInputStream is)
	{
		Graph<CustomVertex, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
		
		try {
			GraphImporter<CustomVertex, DefaultEdge> importer = createImporter();
			
			
			
			importer.importGraph(graph, is);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return graph;
	}
	
	public static GraphWrapper readGraph(String location) throws IOException
	{
		FileInputStream is = new FileInputStream(location);
		Graph<CustomVertex, DefaultEdge> graph = readGraph(is);
		is.close();
		
		Set<Vector2D> V = new HashSet<Vector2D>();
		Set<Edge2D> E = new HashSet<Edge2D>();
		Map<Vector2D, Color> colorMap = new HashMap<Vector2D, Color>();
		
		Map<String, Vector2D> ids = new HashMap<String, Vector2D>();
			
		for(CustomVertex cv : graph.vertexSet())
		{
			Vector2D temp = new Vector2D(cv.x, cv.y);
			V.add(temp);
			colorMap.put(temp, cv.color);
			ids.put(cv.id, temp);
		}
		
		for(DefaultEdge de : graph.edgeSet())
		{
			CustomVertex source = graph.getEdgeSource(de);
			CustomVertex target = graph.getEdgeTarget(de);
			
			E.add(new Edge2D(ids.get(source.id), ids.get(target.id)));
		}
		
		return new GraphMLIO.GraphWrapper(V,E,colorMap);
	}
	
	public static ComponentsWrapper readConnectedComponents(String location) throws IOException
	{
		FileInputStream is = new FileInputStream(location);
		Graph<CustomVertex, DefaultEdge> graph = readGraph(is);
		is.close();
		
		List<ClusterContainer> list = new ArrayList<>();
		Map<Vector2D, Color> colorMap = new HashMap<Vector2D, Color>();
		
		ConnectivityInspector<CustomVertex, DefaultEdge> ci = new ConnectivityInspector<>(graph);
		
		List<Set<CustomVertex>> components = ci.connectedSets();
		
		for(Set<CustomVertex> curr : components)
		{
			Set<Vector2D> V = new HashSet<>();
			Set<Edge2D> E = new HashSet<>();
			Map<String, Vector2D> ids = new HashMap<String, Vector2D>();
			
			for(CustomVertex cv : curr)
			{
				Vector2D temp = new Vector2D(cv.x, cv.y);
				V.add(temp);
				colorMap.put(temp, cv.color);
				ids.put(cv.id, temp);
			}
			
			
			for(CustomVertex cv : curr)
			{
				for(DefaultEdge de : graph.outgoingEdgesOf(cv))
				{
					CustomVertex source = graph.getEdgeSource(de);
					CustomVertex target = graph.getEdgeTarget(de);
					
					E.add(new Edge2D(ids.get(source.id), ids.get(target.id)));
				}
			}
			
			list.add(new ClusterContainer(V, E));
		}
		
		Collections.sort(list, new Comparator<ClusterContainer>() {

			@Override
			public int compare(ClusterContainer arg0, ClusterContainer arg1) {
				// TODO Auto-generated method stub
				return Integer.compare(arg0.V.size(), arg1.V.size());
			}
			
		});
		
		Collections.reverse(list);
		
		return new ComponentsWrapper(list, colorMap);
		
	}
	
	public static class GraphWrapper
	{
		public Set<Vector2D> V;
		public Set<Edge2D> E;
		public Map<Vector2D, Color> colorMap;
		
		public GraphWrapper(Set<Vector2D> V, Set<Edge2D> E, Map<Vector2D, Color> colorMap)
		{
			this.V = V;
			this.E = E;
			this.colorMap = colorMap;
		}
	}
	
	public static class ComponentsWrapper
	{
		public List<ClusterContainer> clusters;
		public Map<Vector2D, Color> colorMap;
		
		public ComponentsWrapper(List<ClusterContainer> list, Map<Vector2D, Color> map)
		{
			clusters = list;
			colorMap = map;
		}
	}
	
	public static class ClusterContainer {
		public Set<Vector2D> V;
		public Set<Edge2D> E;
		
		public ClusterContainer(Set<Vector2D> V, Set<Edge2D> E)
		{
			this.V = V;
			this.E = E;
		}
	}
	
	public static class ContainerComparator implements Comparator<ClusterContainer>
	{

		@Override
		public int compare(ClusterContainer arg0, ClusterContainer arg1) {
			// TODO Auto-generated method stub
			return Integer.compare(arg0.V.size(), arg1.E.size());
		}
		
	}
}
