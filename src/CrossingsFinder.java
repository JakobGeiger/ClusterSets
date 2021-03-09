import delaunayTriangulation.Edge2D;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CrossingsFinder {

    public static Map<Edge2D, List<Edge2D>> findCrossings(Set<Edge2D> set)
    {
    	Map<Edge2D, List<Edge2D>> ret = new HashMap<Edge2D, List<Edge2D>>();
    	
    	for(Edge2D e : set)
    	{
    		List<Edge2D> curr = new LinkedList<Edge2D>();
    		for(Edge2D _e : set) 
    		{
    			if(!(e.equals(_e)))
    			{
    				if(Edge2D.crosses(e, _e))
    					curr.add(_e);
    			}
    		}
    		ret.put(e, curr);
    	}
    	
    	return ret;
    }
	
}
