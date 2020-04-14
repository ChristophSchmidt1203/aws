package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.NodeType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;;

/**
 * Function Test to show that the graphml/CreateDatabase Folder is capeable of
 * creating persistent Databases. The Data which will be restored to the
 * Database is mocked. I use Mockito to see if functions are called correctly,
 * regardless of the parameter passed. In addition, Mockito helps classes to
 * capsules and check them independently.
 * 
 * @author LUNOACK
 *
 */
class FunctionTests {

	/**
	 * The class which builts the new Database.
	 */
	private static RefactorGraph built;

	/**
	 * Database which tests our functions
	 */
	private static GraphTraversalSource g;
	private static Graph graph;

	/**
	 * For the Mock-Tets
	 */
	private RefactorGraph mockbuilt;

	/**
	 * Class which gets the Backup File and creates its Objects
	 */
	private GraphmlType graphml;

	/**
	 * Create a Database Connection
	 * 
	 * @throws Exception
	 */
	@BeforeAll
	public static void setup() throws Exception {
		built = new RefactorGraph("8182", "localhost");
		graph = TinkerGraph.open();
	}

	/**
	 * Test if all functions are called correct and in the right way
	 */
	@Test
	void callFunctions() {
		mockbuilt = Mockito.mock(RefactorGraph.class);

		mockbuilt.restoreDatabase(graphml);
		Mockito.verify(mockbuilt, Mockito.times(1)).restoreDatabase(graphml);
		Mockito.verify(mockbuilt, Mockito.times(0)).extractEdge(Mockito.any(), Mockito.any());
		Mockito.verify(mockbuilt, Mockito.times(0)).extractNode(Mockito.any(), Mockito.any());

		NodeType mocknode = Mockito.mock(NodeType.class);
		GraphTraversalSource gmock = Mockito.mock(GraphTraversalSource.class);
		Mockito.when(mockbuilt.extractNode(Mockito.any(), Mockito.any())).thenReturn(null);
		assertNull(mockbuilt.extractNode(mocknode, gmock));
	}

	/**
	 * Test if the created Database is workable
	 * 
	 * @throws Exception
	 */
	@Test
	void contextLoads() throws Exception {
		GraphTraversalSource g = graph.traversal();

		assertEquals(0, IteratorUtils.count(g.V()));
		assertEquals(0, IteratorUtils.count(g.E()));
		Vertex add = g.addV("Label").property(T.id, "1").property("name", "josh").next();
		Vertex add1 = g.addV("Label").property(T.id, "2").property("name", "mike").next();
		assertEquals(2, IteratorUtils.count(g.V()));
		Edge edge = g.addE("Test").from(add).to(add1).property(T.id, "1").next();
		assertEquals(1, IteratorUtils.count(g.E()));
		g.E(edge).drop().iterate();
		assertEquals(0, IteratorUtils.count(g.E()));
		g.V().drop().iterate();
		assertEquals(0, IteratorUtils.count(g.V()));
	}

	Vertex v1 = null;
	Vertex v2 = null;

	/**
	 * Function to show that the BuiltGraph Class is capeable of creating Verticies
	 */
	@Test
	void testVertexandEdge() {
		g = graph.traversal();
		setVertex();
		setEdge();
		assertNotNull(v1);
		assertNotNull(v2);
		assertEquals(2, IteratorUtils.count(g.V()));
		assertEquals("1", v1.id());
		assertEquals("2", v2.id());
		assertEquals("vertex", v1.label());
		assertEquals("vertex", v2.label());

		assertNotNull(e);
		assertEquals(1, IteratorUtils.count(g.E()));
		assertEquals("edge", e.label());
		assertEquals("10", e.id());
		assertEquals("1", e.outVertex().id());
		assertEquals("2", e.inVertex().id());
	}

	private Edge e;

	/**
	 * Function to show that the BuiltGraph Class is capeable of creating Edges
	 */
	void setEdge() {
		EdgeType edge1 = new EdgeType();

		edge1.setId("10");
		edge1.setSource("1");
		edge1.setTarget("2");

		e = built.extractEdge(edge1, g);
	}

	void setVertex() {
		NodeType node1 = new NodeType();
		NodeType node2 = new NodeType();

		node1.setId("1");
		node2.setId("2");

		v1 = built.extractNode(node1, g);
		v2 = built.extractNode(node2, g);
	}

}
