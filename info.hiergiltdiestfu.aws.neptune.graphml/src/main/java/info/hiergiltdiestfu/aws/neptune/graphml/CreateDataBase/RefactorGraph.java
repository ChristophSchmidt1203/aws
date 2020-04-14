package info.hiergiltdiestfu.aws.neptune.graphml.createdatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import org.graphdrawing.graphml.xmlns.DataType;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.GraphType;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.NodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class extracts the Data from the GraphmlType which stores the data of
 * the Database Creates a new Database with this Data.
 * 
 * @author LUNOACK
 *
 */

@Component
public class RefactorGraph {

	final Logger logger = LogManager.getLogger(RefactorGraph.class);

	/*
	 * The Local Database
	 */
	private Graph g;

	/**
	 * The Remote-Database which the Data is stored
	 */
	private GraphTraversalSource graph;

	/**
	 * String of the Property which is in the Graph-DB
	 */
	static final String LABEL = "label";

	/**
	 * This are all the Vertiecies which are imported into the Database.
	 */
	private Map<String, GraphTraversal<Vertex, Vertex>> nodeinstance = new HashMap<>();

	/**
	 * Creates a Connection to the Database.
	 * 
	 * @param port From application.properties
	 * @param host From the application.properties
	 */
	@Autowired
	public RefactorGraph(String port, String host) {
		Cluster.Builder builder = Cluster.build().addContactPoint(host).port(Integer.parseInt(port))
				// .enableSsl(true).keyCertChainFile("resources/aws/SFSRootCAG2.pem")
				.maxInProcessPerConnection(32).maxSimultaneousUsagePerConnection(32).maxContentLength(4 * 1024 * 1024)
				.serializer(Serializers.GRAPHBINARY_V1D0);

		final Cluster cluster = builder.create();

		/**
		 * Local Database for Test g = TinkerGraph.open(); setGraph( g.traversal());
		 */

		g = EmptyGraph.instance();
		graph = g.traversal().withRemote(DriverRemoteConnection.using(cluster));
	}

	/**
	 * Set database to empty state. Then import the data into the database.
	 */
	public void restoreDatabase(GraphmlType builtgraph) {
		this.graph = deleteDatafromDatabase(getGraph());
		logger.info("Write Data in Database..");
		Collection<?> tmp = builtgraph.getGraphOrData();
		for (Object o : tmp) {
			if (o instanceof GraphType) {
				for (Object p : ((GraphType) o).getDataOrNodeOrEdge()) {
					if (p instanceof NodeType) {
						extractNode((NodeType) p, getGraph());
					}
					if (p instanceof EdgeType) {
						extractEdge((EdgeType) p, getGraph());
					}
				}
			}
		}
	}

	/**
	 * Method to extract all Properties of an Vertex And Them Import the extracted
	 * Data into the Database
	 * 
	 * @param p Vertex Properties
	 * @param g Server Connection where to changes are integrated
	 * @return The created Vertex
	 */
	public Vertex extractNode(NodeType p, GraphTraversalSource g) {

		List<Object> nodetmp = p.getDataOrPort();
		String keyid = p.getId();
		HashMap<String, String> mapprop = new HashMap<>();
		for (Object r : nodetmp) {
			if (r instanceof DataType) {
				DataType dater = (DataType) r;
				mapprop.put(dater.getKey(), dater.getContent());
			}
		}
		GraphTraversal<Vertex, Vertex> verttmp = g.addV(mapprop.get(LABEL)).property(T.id, keyid);

		for (Entry<String, String> entry : mapprop.entrySet()) {
			if (!entry.getKey().equals(LABEL)) {
				verttmp.property(entry.getKey(), entry.getValue());
			}
		}

		nodeinstance.put(keyid, verttmp);

		return verttmp.next();
	}

	/**
	 * Method to extract all Properties of an Edge And Them Import the extracted
	 * Data into the Database
	 * 
	 * @param p Edge-Properties
	 * @param g Server Connection where to changes are integrated
	 * @return The created Edge
	 */
	public Edge extractEdge(EdgeType p, GraphTraversalSource g) {
		List<DataType> nodetmp = p.getData();

		HashMap<String, String> mapprop = new HashMap<>();
		for (DataType r : nodetmp) {
			mapprop.put(r.getKey(), r.getContent());
		}
		GraphTraversal<Edge, Edge> edge = g.addE(mapprop.get(LABEL)).from(g.V(p.getSource())).to(g.V(p.getTarget()))
				.property(T.id, p.getId());

		for (Entry<String, String> entry : mapprop.entrySet()) {
			if (!entry.getKey().equals(LABEL)) {
				edge.property(entry.getKey(), entry.getValue());
			}
		}

		return edge.next();
	}

	/**
	 * Delete the Data from the Database.
	 * 
	 * @param g
	 * @return
	 */
	public GraphTraversalSource deleteDatafromDatabase(GraphTraversalSource g) {
		logger.info("Delete data from Database..");
		graph.V().drop().iterate();

		return graph;
	}

	public GraphTraversalSource getGraph() {
		return graph;
	}

	public void setGraph(GraphTraversalSource graph) {
		this.graph = graph;
	}
}