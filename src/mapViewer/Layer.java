package mapViewer;


import java.awt.Color;
import java.util.List;

//Download https://sourceforge.net/projects/jts-topo-suite/
import com.vividsolutions.jts.geom.Envelope;

public abstract class Layer {
	
    protected Envelope extent;
    protected Color myColor;
	   
    public Layer(Color c) {
    	myColor = c;
    }
    
    
    /**
     * Returns the extent of this layer as an envelope
     * @return
     */
    public Envelope getExtent() {
    	return extent;
    } 
        
    
    /**
     * Queries all Objects whose bounding boxes intersect the search envelope
     * @param searchEnv
     * @return
     */
	public abstract List<MapObject> query(Envelope searchEnv);
	
	public ListLayer toCachedLayer() {
		ListLayer myCachedLayer = new ListLayer(myColor);
		for (MapObject mo : this.query(extent)) {
			myCachedLayer.add(mo);
		}
		return myCachedLayer;
	}


	public Color getColor() {
		return myColor;
	}
	
	public void setColor(Color c) {
		myColor = c;
	}
}
