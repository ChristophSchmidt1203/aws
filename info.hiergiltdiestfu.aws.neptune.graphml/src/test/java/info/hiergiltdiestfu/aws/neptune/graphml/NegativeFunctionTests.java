package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.NodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;

/**
 * Class to show that negative Parameters will not work I use Mockito to see if
 * functions are called correctly, regardless of the parameter passed. In
 * addition, Mockito helps classes to capsules and check them independently.
 * 
 * @author LUNOACK
 *
 */
public class NegativeFunctionTests {

	/**
	 * Database which tests our functions
	 */
	private static GraphTraversalSource g;
	private static Graph graph;

	/**
	 * The class which builts the new Database.
	 */
	private static RefactorGraph built;

	/**
	 * Creates a Test-Database.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	@BeforeAll
	static void setup() throws IOException, JAXBException {

		graph = Mockito.mock(Graph.class);
		built = new RefactorGraph("8182", "localhost");
		graph = TinkerGraph.open();
		g = graph.traversal();
	}

	/**
	 * Multiple ID´s will give Exceptions
	 */
	@Test
	void testVertexId() {
		NodeType node1 = new NodeType();
		node1.setId("1");
		built.extractNode(node1, g);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			NodeType node2 = new NodeType();
			node2.setId("1");
			built.extractNode(node2, g);
		});
	}

	/**
	 * Edge Sources of Verticies which are not existent will give Exceptions
	 */
	@Test
	void testEdgeTargetSource() {
		EdgeType edge = new EdgeType();
		Assertions.assertThrows(NullPointerException.class, () -> {
			built.extractEdge(edge, g);
		});

		edge.setSource("3");
		edge.setTarget("4");

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			built.extractEdge(edge, g);
		});

		edge.setSource("1");
		edge.setTarget("");

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			built.extractEdge(edge, g);
		});
	}
}
