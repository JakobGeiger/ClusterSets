package delaunayTriangulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class FirstOrderDelaunayTriangulator {
	
	public DelaunayTriangulator dt;
	public Set<Vector2D> V;
	public Set<Edge2D> E;
	public Set<Edge2D> E_;
	public Map<Edge2D, Edge2D> crossings;
	
	public FirstOrderDelaunayTriangulator(DelaunayTriangulator dt)
	{
		this.dt = dt;
	}
	
	public void execute() throws NotEnoughPointsException
	{
		
			dt.triangulate();	//this could throw an exception
			V = new HashSet<Vector2D>(dt.getPointSet());
			E = new HashSet<Edge2D>();
			E_ = new HashSet<Edge2D>();
			crossings = new HashMap<Edge2D, Edge2D>();
			
			List<Triangle2D> triangles = dt.getTriangles();
			
			List<Edge2D> edges = new LinkedList<Edge2D>();
			
			for(Triangle2D t : triangles)
			{
				edges.clear();
				edges.add(new Edge2D(t.a, t.b));
				edges.add(new Edge2D(t.b, t.c));
				edges.add(new Edge2D(t.c, t.a));

				E.add(new Edge2D(t.a, t.b));
				E.add(new Edge2D(t.b, t.c));
				E.add(new Edge2D(t.c, t.a));
				
				for(Edge2D e : edges)
				{
					Triangle2D tri = dt.findNeighbour(t, e);
					if(tri != null)
					{
						Vector2D a = e.a;
						Vector2D b = e.b;
						Vector2D c = findThird(t,a,b);
						Vector2D d = findThird(tri,a,b);
						
						Triangle2D test1 = new Triangle2D(a,c,d);
						Triangle2D test2 = new Triangle2D(b,c,d);
						
						if(valid(test1, test2) && Edge2D.intersectLines(e, new Edge2D(c,d))!=null)
						{
							Edge2D temp = new Edge2D(c,d);
							E_.add(temp);
							crossings.put(temp, e);
							crossings.put(e,  temp);
						}
					}
				}
				
			}
	}
	
	public boolean valid(Triangle2D one, Triangle2D two)
	{
		int res1 = 0;
		int res2 = 0;
		double[] circle;
		Vector2D vec;
		
		for(Vector2D v : V)
		{
			circle = initThreePointCircle(one.a, one.b, one.c);
			vec = new Vector2D(circle[0],circle[1]);
			if(v.dist(vec)<circle[2])
			{
				res1++;
			}
			
			circle = initThreePointCircle(two.a, two.b, two.c);
			vec = new Vector2D(circle[0],circle[1]);
			if(v.dist(vec)<circle[2])
			{
				res2++;
			}
		}
		
		return (res1<=1)&&(res2<=1);
	}
	
	private double[] initThreePointCircle(Vector2D p1, Vector2D p2, Vector2D p3) {
		
		double[] res = new double[3];
        double a13, b13, c13;
        double a23, b23, c23;
        double x = 0., y = 0., rad = 0.;

        // begin pre-calculations for linear system reduction
        a13 = 2 * (p1.x - p3.x);
        b13 = 2 * (p1.y - p3.y);
        c13 = (p1.y * p1.y - p3.y * p3.y) + (p1.x * p1.x - p3.x * p3.x);
        a23 = 2 * (p2.x - p3.x);
        b23 = 2 * (p2.y - p3.y);
        c23 = (p2.y * p2.y - p3.y * p3.y) + (p2.x * p2.x - p3.x * p3.x);
        // testsuite-suite to be certain we have three distinct points passed
        double smallNumber = 0.01;
        if ((Math.abs(a13) < smallNumber && Math.abs(b13) < smallNumber)
                || (Math.abs(a23) < smallNumber && Math.abs(b23) < smallNumber)) {
            // // points too close so set to default circle
            x = 0;
            y = 0;
            rad = 0;
        } else {
            // everything is acceptable do the y calculation
            y = (a13 * c23 - a23 * c13) / (a13 * b23 - a23 * b13);
            // x calculation
            // choose best formula for calculation
            if (Math.abs(a13) > Math.abs(a23)) {
                x = (c13 - b13 * y) / a13;
            } else {
                x = (c23 - b23 * y) / a23;
            }
            // radius calculation
            rad = Math.sqrt((x - p1.x) * (x - p1.x) + (y - p1.y) * (y - p1.y));
        }
        res[0] = x;
        res[1] = y;
        res[2] = rad;
        
        return res;
}
	
	public Vector2D findThird(Triangle2D t, Vector2D a, Vector2D b)
	{
		if(t.a != a && t.a != b)
			return t.a;
		if(t.b != a && t.b != b)
			return t.b;
		if(t.c != a && t.c != b)
			return t.c;
		return null;
	}
	
}
