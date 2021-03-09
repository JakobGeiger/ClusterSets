import java.util.List;
import java.util.Map;
import java.util.Set;

import delaunayTriangulation.Edge2D;


public interface EdgeSelector {
	public Set<Edge2D> selectUncrossedSet(Set<Edge2D> set, Map<Edge2D, List<Edge2D>> crossings);
}
