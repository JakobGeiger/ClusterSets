package delaunayTriangulation;

/**
 * 2D vector class implementation.
 * 
 * @author Johannes Diemke
 */
public class Vector2D {

    public double x;
    public double y;

    /**
     * Constructor of the 2D vector class used to create new vector instances.
     * 
     * @param x
     *            The x coordinate of the new vector
     * @param y
     *            The y coordinate of the new vector
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Subtracts the given vector from this.
     * 
     * @param vector
     *            The vector to be subtracted from this
     * @return A new instance holding the result of the vector subtraction
     */
    public Vector2D sub(Vector2D vector) {
        return new Vector2D(this.x - vector.x, this.y - vector.y);
    }

    /**
     * Adds the given vector to this.
     * 
     * @param vector
     *            The vector to be added to this
     * @return A new instance holding the result of the vector addition
     */
    public Vector2D add(Vector2D vector) {
        return new Vector2D(this.x + vector.x, this.y + vector.y);
    }

    /**
     * Multiplies this by the given scalar.
     * 
     * @param scalar
     *            The scalar to be multiplied by this
     * @return A new instance holding the result of the multiplication
     */
    public Vector2D mult(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    /**
     * Computes the magnitude or length of this.
     * 
     * @return The magnitude of this
     */
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Computes the dot product of this and the given vector.
     * 
     * @param vector
     *            The vector to be multiplied by this
     * @return A new instance holding the result of the multiplication
     */
    public double dot(Vector2D vector) {
        return this.x * vector.x + this.y * vector.y;
    }

    /**
     * Computes the 2D pseudo cross product Dot(Perp(this), vector) of this and
     * the given vector.
     * 
     * @param vector
     *            The vector to be multiplied to the perpendicular vector of
     *            this
     * @return A new instance holding the result of the pseudo cross product
     */
    public double cross(Vector2D vector) {
        return this.y * vector.x - this.x * vector.y;
    }
    
    public double dist(Vector2D vect)
    {
    	double dx = x-vect.x;
    	double dy = y-vect.y;
    	
    	dx = Math.pow(dx, 2);
    	dy = Math.pow(dy,  2);
    	
    	return Math.sqrt(dx+dy);
    }

    @Override
    public String toString() {
        return "Vector2D[" + x + ", " + y + "]";
    }
    
    @Override
    public boolean equals(Object o)
    {
    	if(!(o instanceof Vector2D))
    		return false;
    	Vector2D temp = (Vector2D) o;
    	return (this.x==temp.x&&this.y==temp.y);
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
    
    
    //a method to calculate the angle between three points.
    public static double calculateAnglePRQ(Vector2D p, Vector2D r, Vector2D q)
	{
		double lengthrpsquared = Math.pow(r.x-p.x,2) + Math.pow(r.y-p.y, 2);
		double lengthrqsquared = Math.pow(r.x-q.x,2) + Math.pow(r.y-q.y, 2);
		double lengthpqsquared = Math.pow(p.x-q.x,2) + Math.pow(p.y-q.y, 2);

		double temp = (lengthrpsquared+lengthrqsquared-lengthpqsquared)/(2*Math.sqrt(lengthrpsquared)*Math.sqrt(lengthrqsquared));
		
		return Math.acos(temp);
		
	}

}