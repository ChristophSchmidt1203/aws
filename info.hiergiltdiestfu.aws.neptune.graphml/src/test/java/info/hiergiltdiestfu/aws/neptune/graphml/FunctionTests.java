package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.NodeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;;


@SpringBootTest
class FunctionTests {
	
	private static BuiltGraph built;
	
	@BeforeAll
	public static void setup() throws Exception {
		graph = Mockito.mock(Graph.class);
		GraphmlType graphml = Mockito.mock(GraphmlType.class);
		built = new BuiltGraph(graphml);
	}
	
	private static GraphTraversalSource g;
	private static BuiltGraph mockbuilt;
	private static Graph graph;
	
	@Test
	void callFunctions() {
		mockbuilt = Mockito.mock(BuiltGraph.class);
		
		mockbuilt.extractProperties();
		Mockito.verify(mockbuilt,Mockito.times(1)).extractProperties();
		Mockito.verify(mockbuilt,Mockito.times(0)).extractEdge(Mockito.any(), Mockito.any());
		Mockito.verify(mockbuilt,Mockito.times(0)).extractNode(Mockito.any(), Mockito.any());
		
		NodeType mocknode = Mockito.mock(NodeType.class);
		GraphTraversalSource gmock = Mockito.mock(GraphTraversalSource.class);
		Mockito.when(mockbuilt.extractNode(Mockito.any(), Mockito.any())).thenReturn(null);
		assertNull(mockbuilt.extractNode(mocknode, gmock));
		
		Mockito.verify(mockbuilt,Mockito.times(0)).createandconnectGraph(Mockito.any());
		mockbuilt.createandconnectGraph(graph);
		Mockito.verify(mockbuilt,Mockito.times(1)).createandconnectGraph(Mockito.any());
		Mockito.verify(mockbuilt, Mockito.never()).extractEdge(Mockito.any(), Mockito.any());
		
		Mockito.when(mockbuilt.createandconnectGraph(Mockito.isA(Graph.class))).thenReturn(null);
		assertNull(mockbuilt.createandconnectGraph(graph));	
	}
	
	@Test
	void contextLoads() throws Exception {	
		g = built.createandconnectGraph(graph);

		assertEquals(0,IteratorUtils.count(g.V()));
		assertEquals(0, IteratorUtils.count(g.E()));
		Vertex add = g.addV("Label").property(T.id,"1").property("name", "josh").next();
		Vertex add1 = g.addV("Label").property(T.id,"2").property("name", "mike").next();
		assertEquals(2,IteratorUtils.count(g.V()));
		Edge edge = g.addE("Test").from(add).to(add1).property(T.id,"1").next();
		assertEquals(1,IteratorUtils.count(g.E()));
		g.E(edge).drop().iterate();
		assertEquals(0,IteratorUtils.count(g.E()));
		g.V().drop().iterate();
		assertEquals(0,IteratorUtils.count(g.V()) );	
			
	}
	
	private Vertex v1;
	private Vertex v2;
	
	@Test
	void testVertex() {
		g = built.createandconnectGraph(graph);
		setVertex();
		
		assertNotNull(v1);
		assertNotNull(v2);
		assertEquals(2,IteratorUtils.count(g.V()));
		assertEquals("1",v1.id());
		assertEquals("2",v2.id());
		assertEquals("vertex",v1.label());
		assertEquals("vertex",v2.label());
	}
	
	private Edge e;
	
	@Test
	void testEdge() {
		testVertex();
		setEdge();	
		
		assertNotNull(e);
		assertEquals(1,IteratorUtils.count(g.E()));
		assertEquals("edge",e.label());
		assertEquals("10",e.id());
		assertEquals("1",e.outVertex().id());
		assertEquals("2",e.inVertex().id());
	}
	
	void setVertex() {
		NodeType node1 = new NodeType();
		NodeType node2 = new NodeType();
		
		node1.setId("1");
		node2.setId("2");
		
		v1 = built.extractNode(node1, g);
		v2 = built.extractNode(node2, g);
	}
	
	void setEdge() {
		EdgeType edge1= new EdgeType();
		
		edge1.setId("10");
		edge1.setSource("1");
		edge1.setTarget("2");
		
		e = built.extractEdge(edge1, g);
	}
	
	@AfterAll
	public static void  tearDown() {
		
	}
}
