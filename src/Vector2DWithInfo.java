import delaunayTriangulation.Vector2D;

public class Vector2DWithInfo<T> extends Vector2D {
	public T info;
	public Vector2DWithInfo(double x, double y, T t) {
		super(x, y);
		this.info = t;		
	}

}
