package mapViewer;

import java.awt.geom.Point2D;

import com.vividsolutions.jts.awt.PointTransformation;
import com.vividsolutions.jts.geom.Coordinate;

public class JTSPointTransformation implements PointTransformation {
	
	private Transformation myTransformation;

	public JTSPointTransformation(Transformation t) {
		myTransformation = t;
	}
	
	@Override
	public void transform(Coordinate c, Point2D p) {
		p.setLocation(myTransformation.getColumn(c.x), myTransformation.getRow(c.y));

	}

}
