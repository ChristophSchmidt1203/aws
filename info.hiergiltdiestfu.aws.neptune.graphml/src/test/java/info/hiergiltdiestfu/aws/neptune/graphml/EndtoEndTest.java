package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import info.hiergiltdiestfu.aws.neptune.graphml.AWS.AWSImporter;
import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.REST.NeptuneAdapter;
/**
 * End-End-Test
 * Look if the starting Database is equal to the new created
 * @author LUNOACK
 *
 */
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
		gs = inend.getG(); 
		
		BuiltGraph built; 
	
		//Rest Test
		///*
		ImportedGraph imp = new ImportedGraph();
		imp.restToString();
		built = new BuiltGraph(imp.getType());
		ge = built.getGraph();
		//*/
		
		//AWS Test
		/*
		AWSImporter importer = new AWSImporter();
		built = importer.getGraph();
		ge = built.getGraph();
		*/
	}
	
	/**
	 * Test if all Verticies are the same, ammount, properties
	 */
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
		
	/**
	 * Test if all Edges are the same, ammount ,properties and connections
	 */
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
			assertEquals(gs.E(i.toString()).valueMap().next().toString(),ge.E(i.toString()).valueMap().next().toString());
		}
	}	
	
	
	@Test
	public void test() throws MalformedURLException {
		Graph g = TinkerGraph.open();
		ImportedGraph imp = new ImportedGraph();
		imp.restToString();
		BuiltGraph built = new BuiltGraph(imp.getType());
		GraphTraversalSource test = built.createandconnectGraph(g);
		
		test.addV("label").property(T.id,"1").next();
		test.addV("label").property(T.id,"2").next();
		
		GraphTraversal<Edge, Edge> edge = test.addE("HUE")
				.from(test.V("1"))
				.to(test.V("2"))
				.property(T.id,"0");
		
		edge.property("wheight", "2");
		edge.property("hello", "2");
		edge.next();
		
		GraphTraversalSource test1 = built.createandconnectGraph(g);
		
		test1.addV("label").property(T.id,"1").next();
		test1.addV("label").property(T.id,"2").next();
		
		GraphTraversal<Edge, Edge> edge1 = test1.addE("HUE")
				.from(test1.V("1"))
				.to(test1.V("2"))
				.property(T.id,"0");
		
		edge1.property("wheight", "2");
		edge1.property("hello", "2");
		edge1.next();
		
		List<?> edgeids = test.E().id().toList();
		for(Object i : edgeids) {
			assertEquals(test.E(i.toString()).valueMap().next(),test1.E(i.toString()).valueMap().next());
		}
	}
}	
	