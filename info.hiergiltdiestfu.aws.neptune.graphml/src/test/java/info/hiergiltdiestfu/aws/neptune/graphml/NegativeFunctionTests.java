package info.hiergiltdiestfu.aws.neptune.graphml;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.graphdrawing.graphml.xmlns.EdgeType;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.NodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NegativeFunctionTests {
	
	@BeforeAll
	static void setup() {
		graph = Mockito.mock(Graph.class);
		GraphmlType mltype = Mockito.mock(GraphmlType.class);
		built = new BuiltGraph(mltype);
	}
	
	private static GraphTraversalSource g;
	private static BuiltGraph built;
	private static Graph graph;
	
	@Test
	void testVertexId() {
		g = built.createandconnectGraph(graph);
		NodeType node1 = new NodeType();	
		node1.setId("1");
		built.extractNode(node1, g);
		Assertions.assertThrows(IllegalArgumentException.class , () ->{
			NodeType node2 = new NodeType();
			node2.setId("1");
			built.extractNode(node2, g);
		});		
	}
	
	@Test
	void testEdgeTargetSource() {
		testVertexId();
		EdgeType edge = new EdgeType();
		Assertions.assertThrows(NullPointerException.class, () ->{
			built.extractEdge(edge, g);
		});	
		
		edge.setSource("3");
		edge.setTarget("4");
		
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			built.extractEdge(edge, g);
		});
		
		edge.setSource("1");
		edge.setTarget("");

		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			built.extractEdge(edge, g);
		});
	}
}
