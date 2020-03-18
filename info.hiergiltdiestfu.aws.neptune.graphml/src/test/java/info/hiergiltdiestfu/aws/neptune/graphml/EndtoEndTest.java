package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

@SpringBootTest
public class EndtoEndTest {
	
	private static GraphTraversalSource gs;
	private static GraphTraversalSource ge;
	
	@BeforeAll
	public static void setup() throws Exception {
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		final var write = httpServletResponse.getWriter();
		NeptuneAdapter inend = new NeptuneAdapter();
		inend.serialize(write);
		gs = inend.g; 
		ImportedGraph imp = new ImportedGraph();
		BuiltGraph built = new BuiltGraph(imp.type);
		ge = built.graph;
	}
	
	@Test
	public void testVertex() {
		assertEquals(IteratorUtils.count(gs.V()),IteratorUtils.count(ge.V())); //notempty
		assertTrue(IteratorUtils.count(gs.V()) > 0);
		
		List<Object> stvert = gs.V().properties().with(WithOptions.tokens).unfold().toList();
		List<Object> endvert = ge.V().properties().with(WithOptions.tokens).unfold().toList();
		
		stvert.removeAll(endvert);		
		assertEquals(true,stvert.isEmpty());
		
		List<String> stvertlabel = gs.V().label().toList();
		List<String> endvertlabel = ge.V().label().toList();
		
		stvertlabel.removeAll(endvertlabel);
		assertEquals(true,stvertlabel.isEmpty());
	}	
		
	@Test
	public void testEdge() {
		assertEquals(IteratorUtils.count(gs.E()),IteratorUtils.count(ge.E()));
		assertTrue(IteratorUtils.count(gs.E()) > 0);
		
		List<?> ids = ge.V().id().toList();
		for(Object i: ids) {
			assertEquals(gs.V(i.toString()).outE(),ge.V(i.toString()).outE());
		}
		
		List<?> edgeids = ge.E().id().toList();
		for(Object i : edgeids) {
			assertEquals(gs.E(i.toString()).properties(),ge.E(i.toString()).properties());
		}
	}	
	
	@Test
	public void test() {
		BuiltGraph built = new BuiltGraph(Mockito.mock(GraphmlType.class));
		GraphTraversalSource test = built.createandconnectGraph(Mockito.mock(Graph.class));
		
		test.addV("label").property(T.id,"1").next();
		test.addV("label").property(T.id,"2").next();
		System.out.println(test.V().valueMap().with(WithOptions.tokens).toList());
		
		GraphTraversal<Edge, Edge> edge = test.addE("label")
				.from("1")
				.to("2")
				.property(T.id,"0");
		
		edge.property("wheiht", "1");
		edge.property("hello", "2");
		edge.iterate();
		
		GraphTraversalSource test1 = built.createandconnectGraph(Mockito.mock(Graph.class));
		
		test1.addV("label").property(T.id,"1").next();
		test1.addV("label").property(T.id,"2").next();
		
		test1.addE("powered").from("1").to("2").property("wheight", "2").property("hello", "yes").next();
		
		System.out.println(test.V().valueMap().with(WithOptions.tokens).toList());
		System.out.println(test1.V().valueMap().with(WithOptions.tokens).toList());
	}
	//doku!!!!
}	//für bugs testfälle schreiben(zum späteren erinnern), mehrere properties vergleichen
	//pull requests, review über code
	//end to end test
	