package info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.graphdrawing.graphml.xmlns.DataType;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.GraphType;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.NodeType;
/**
 * 
 * @author LUNOACK
 *
 */
public class BuiltGraph {
	
	private GraphmlType builtgraph;
	private Graph g;
	private GraphTraversalSource graph;
	private Map<String,GraphTraversal<Vertex,Vertex>> nodeinstance = new HashMap<>();
	
	/**
	 * 
	 * @param builtgraph is the GraphmlType with all properties of the Graph
	 */
	public BuiltGraph(GraphmlType builtgraph) {
		this.builtgraph= builtgraph;
		createandconnectGraph(g);
		extractProperties();
		try {
			getGraph().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Properties which are extracted from the GraphmlType
	 */
	public void extractProperties() {
		Collection<?> tmp = builtgraph.getGraphOrData();
		for(Object o : tmp) {
			if(o instanceof GraphType) {
				for(Object p :((GraphType) o).getDataOrNodeOrEdge()) {
					if(p instanceof NodeType) {
						extractNode((NodeType)p, getGraph());
					}
					if(p instanceof EdgeType) {
						extractEdge((EdgeType)p, getGraph());
					}
				}
			}	
		}
	}
	
	static final String LABEL = "label";
	/**
	 * Function to create a Vertex of the extraxted NodeType
	 * @param p Vertex Properties
	 * @param g Server Connection where to changes are integrated
	 * @return The created Vertex
	 */
	public Vertex extractNode(NodeType p , GraphTraversalSource g) { 
		
		List<Object> nodetmp = p.getDataOrPort(); //port schauen
		String keyid = p.getId();
		HashMap<String,String> mapprop = new HashMap<>();
		for(Object r : nodetmp) {
			if(r instanceof DataType) {
				DataType dater = (DataType) r;
				mapprop.put(dater.getKey(),dater.getContent());
			}
		}
		GraphTraversal<Vertex, Vertex> verttmp = g.addV(mapprop.get(LABEL))
														.property(T.id, keyid); 
		
		for(Entry<String,String> entry: mapprop.entrySet()) {			
			if(! entry.getKey().equals(LABEL)) {
				verttmp.property(entry.getKey(),entry.getValue());	
			}
		}
		
		nodeinstance.put(keyid, verttmp);
		
		return  verttmp.next();
	}
	/**
	 * Function to create a Edge of the extracted EdgeType
	 * @param p Edge-Properties
	 * @param g Server Connection where to changes are integrated
	 * @return The created Edge 
	 */
	public Edge extractEdge(EdgeType p, GraphTraversalSource g) {
		List<DataType> nodetmp = p.getData();
		
		HashMap<String,String> mapprop = new HashMap<>();
		for(DataType r : nodetmp) { 
			mapprop.put(r.getKey(), r.getContent());
		}
		GraphTraversal<Edge, Edge> edge = g.addE(mapprop.get(LABEL))
												.from(g.V(p.getSource()))
												.to(g.V(p.getTarget()))
												.property(T.id,p.getId());
		
		for(Entry<String,String> entry: mapprop.entrySet()) {
			if(!entry.getKey().equals(LABEL)){
				edge = edge.property(entry.getKey(), entry.getValue());
			}
		}
		
		return edge.next();
	}
	/**
	 * This creates a connection to a DB/server with the specific properties.
	 * @param g Graph-Type where the connection is built
	 * @return operational server connection 
	 */
	public GraphTraversalSource createandconnectGraph(Graph g) {
		g = TinkerGraph.open();    
		
		//Local Database for Test
		setGraph( g.traversal()); 
		
		//Database with Connection to Server
		//setGraph(g.traversal().withRemote(DriverRemoteConnection.using("localhost", 8182)));  
		
		return getGraph();
	}

	public GraphTraversalSource getGraph() {
		return graph;
	}

	public void setGraph(GraphTraversalSource graph) {
		this.graph = graph;
	}
}