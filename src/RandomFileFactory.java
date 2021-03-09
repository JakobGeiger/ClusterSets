import java.util.Random;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.IllegalParametersException;
import com.vividsolutions.jump.io.ShapefileWriter;


public class RandomFileFactory {

	ShapefileWriter sfw;
	
	public RandomFileFactory()
	{
		sfw = new ShapefileWriter();
	}
	
	public void generateRandomFile(String destination, int size, int xDim, int yDim, int classes)
	{
		DriverProperties dp = new DriverProperties(destination);
		FeatureSchema fs = new FeatureSchema();
		fs.addAttribute("fclass", AttributeType.STRING);
		fs.addAttribute("geometry", AttributeType.GEOMETRY);
		FeatureCollection fc = new FeatureDataset(fs);
		GeometryFactory gf = new GeometryFactory();
		Random r = new Random();
		
		for(int i = 0; i< size; i++)
		{
			Feature f = new BasicFeature(fs);
			
			double randomX = 350000 + xDim*r.nextDouble();
    		double randomY = 5600000 + yDim*r.nextDouble();
    		int randomClass = (int) (classes*r.nextDouble());
    		String c;
    		switch(randomClass)
    		{
    		case 0: {c = "restaurant"; break;}
    		case 1: {c = "clothes"; break;}
    		case 2: {c = "fast_food"; break;}
    		case 3: {c = "shoe_shop"; break;}
    		case 4: {c = "jeweller"; break;}
    		case 5: {c = "hairdresser"; break;}
    		default: {c = "restaurant"; break;}
    		}
    		

			Geometry geom = gf.createPoint(new Coordinate(randomX,randomY));
			f.setGeometry(geom);
			f.setAttribute("fclass", c);
			
			fc.add(f);
		}
	
		
		
		
		try {
			sfw.write(fc, dp);
		}
		catch(IllegalParametersException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
}
