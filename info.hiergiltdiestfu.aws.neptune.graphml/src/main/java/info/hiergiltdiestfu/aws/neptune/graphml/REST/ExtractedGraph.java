package info.hiergiltdiestfu.aws.neptune.graphml.REST;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExtractedGraph {

	Map<String,Object> vertexProperties;
	Map<String,Object> edgeProperties;
	
	final List<Map<Object, Object>> nodes;
	final List<Map<Object, Object>> edges;
	public ExtractedGraph(List<Map<Object, Object>> nodes, List<Map<Object, Object>> edges) {
		super();
		this.nodes = nodes==null?Collections.emptyList():nodes;
		this.edges = edges==null?Collections.emptyList():edges;
	}
	public Map<String, Object> getNodeProperties() {
		return vertexProperties;
	}
	public void setVertexProperties(Map<String, Object> vertexProperties) {
		this.vertexProperties = vertexProperties;
	}
	public Map<String, Object> getEdgeProperties() {
		return edgeProperties;
	}
	public void setEdgeProperties(Map<String, Object> edgeProperties) {
		this.edgeProperties = edgeProperties;
	}
	public List<Map<Object, Object>> getNodes() {
		return nodes;
	}
	public List<Map<Object, Object>> getEdges() {
		return edges;
	}
	@Override
	public String toString() {
		final int maxLen = 4;
		StringBuilder builder = new StringBuilder();
		builder.append("ExtractedGraph [vertexProperties=");
		builder.append(vertexProperties != null ? toString(vertexProperties.entrySet(), maxLen) : null);
		builder.append(", edgeProperties=");
		builder.append(edgeProperties != null ? toString(edgeProperties.entrySet(), maxLen) : null);
		builder.append(", nodes=");
		builder.append(nodes != null ? toString(nodes, maxLen) : null);
		builder.append(", edges=");
		builder.append(edges != null ? toString(edges, maxLen) : null);
		builder.append("]");
		return builder.toString();
	}
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}


	
}
