package delaunayTriangulation;

/**
 * 2D edge class implementation.
 * 
 * @author Johannes Diemke
 */
public class Edge2D{

    public Vector2D a;
    public Vector2D b;
    public double length;

    /**
     * Constructor of the 2D edge class used to create a new edge instance from
     * two 2D vectors describing the edge's vertices.
     * 
     * @param a
     *            The first vertex of the edge
     * @param b
     *            The second vertex of the edge
     */
    public Edge2D(Vector2D a, Vector2D b) 
    {
        this.a = a;
        this.b = b;
        length = a.dist(b);
    }
    
    //method to check if two edges cross
    public static boolean crosses(Edge2D l, Edge2D m) 
    {
    	if((l.a == m.a) || (l.a == m.b) || (l.b == m.a) || (l.b == m.b))
			return false;
    	return !(intersectLines(l,m)==null);
    }
    
    public boolean connects(Vector2D a, Vector2D b)
    {
    	return (this.a.equals(a)&&this.b.equals(b))||(this.a.equals(b)&&this.b.equals(a));
    }
    
    public static Vector2D intersectLines(Edge2D l, Edge2D m)
    {	
    	
    	/*
    	 * calculates the intersection point of two edges
    	 * code shamelessly taken from the internet, modeled after the wikipedia page on line intersection.	
    	 */
    	
                
	    double x1 = l.a.x;
	    double x2 = l.b.x;
	    double x3 = m.a.x;
	    double x4 = m.b.x;
	    double y1 = l.a.y;
	    double y2 = l.b.y;
	    double y3 = m.a.y;
	    double y4 = m.b.y;

	    
	    double zx = (x1 * y2 - y1 * x2)*(x3-x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
	    double zy = (x1 * y2 - y1 * x2)*(y3-y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
	      
	    
	    double n = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
	    
	   
	    double x = zx/n;
	    double y = zy/n;
    	
	    
	    if (Double.isNaN(x)& Double.isNaN(y))
	    {
	    	 return null;
	    }
	    
	    /*if ((x - x1) / (x2 - x1) > 1 || (x - x3) / (x4 - x3) > 1 || (y - y1) / (y2 - y1) > 1 || (y - y3) / (y4 - y3) > 1 )
	    {
	    	 return null;
	    }
	    */
	    
	    boolean outsidel = ((x<x1&&x<x2)||(x>x1&&x>x2)) || ((y<y1&&y<y2)||(y>y1&&y>y2));
	    boolean outsidem = ((x<x3&&x<x4)||(x>x3&&x>x4)) || ((y<y3&&y<y4)||(y>y3&&y>y4));
	    
	    if(outsidel||outsidem)
	    	return null;
	    
	    Vector2D ret = new Vector2D(x,y);
	    if(!ret.equals(l.a) && !ret.equals(l.b) && !ret.equals(m.a) && !ret.equals(m.b))
	    	return ret;
	    else
	    	return null;
	    
    }
    
    
    @Override
    public boolean equals(Object o)
    {
    	/*
    	 * since we don't work with multigraphs, "reversed" edges need to be equal
    	 */
    	if(!(o instanceof Edge2D))
    		return false;
    	Edge2D temp = (Edge2D) o;
    	return temp.connects(a,b);
    }
    
    @Override
    public int hashCode()
    {
    	/*
    	 * we simply add the hash values of the two end points, this way we ensure that even "reversed" edges have the same hash value
    	 */
    	return a.hashCode() + b.hashCode();
    }

    public String toString()
    {
    	return "Edge2D[("+a.x + "," + a.y + "),("+b.x +","+b.y+")]";
    }

}