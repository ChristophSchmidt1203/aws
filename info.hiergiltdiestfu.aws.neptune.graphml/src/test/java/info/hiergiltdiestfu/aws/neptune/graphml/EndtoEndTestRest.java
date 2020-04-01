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
import org.springframework.mock.web.MockHttpServletResponse;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.NeptuneAdapter;

/**
 * End-End-Test
 * Look if the starting Database is equal to the new created
 * @author LUNOACK
 *
 */
public class EndtoEndTestRest {
	
	/**
	 * Is the Database from which the Backup-File is created 
	 */
	private static GraphTraversalSource graphsource;
	
	/**
	 * Is the Database which is created from the Backup-File
	 */
	private static GraphTraversalSource graphtarget;
	
	@BeforeAll
	public static void setup() throws Exception {
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		final var write = httpServletResponse.getWriter();
		NeptuneAdapter inend = new NeptuneAdapter();
		inend.serialize(write);
		graphsource = inend.getG(); 
		
		BuiltGraph built; 
	
		/**
		 * Get the Properties from the Rest.
		 * And store them into a Local Database.
		 * In BuiltGraph the LocalDatabase has to be open.
		 */
		ImportedGraph imp = new ImportedGraph();
		imp.restToString();
		built = new BuiltGraph(imp.getType());
		graphtarget = built.getGraph();

	}
	
	/**
	 * Test if all Verticies are the same, ammount, properties
	 */
	@Test
	public void testVertex() {
		long numbervertexsource = IteratorUtils.count(graphsource.V());
		assertEquals(numbervertexsource,IteratorUtils.count(graphtarget.V())); //notempty
		assertTrue(numbervertexsource > 0);
		
		List<Object> stvert = graphsource.V().properties().with(WithOptions.tokens).unfold().toList();
		List<Object> endvert = graphtarget.V().properties().with(WithOptions.tokens).unfold().toList();
		
		stvert.removeAll(endvert);		
		assertEquals(true,stvert.isEmpty());
		
		List<String> stvertlabel = graphsource.V().label().toList();
		List<String> endvertlabel = graphtarget.V().label().toList();
		
		stvertlabel.removeAll(endvertlabel);
		assertTrue(stvertlabel.isEmpty());
	}	
		
	/**
	 * Test if all Edges are the same, ammount ,properties and connections
	 */
	@Test
	public void testEdge() {
		long numberedgesource = IteratorUtils.count(graphsource.E());
		assertEquals(numberedgesource,IteratorUtils.count(graphtarget.E()));
		assertTrue(numberedgesource > 0);
		
		List<?> ids = graphtarget.V().id().toList();
		for(Object i: ids) {
			assertEquals(graphsource.V(i.toString()).outE(),graphtarget.V(i.toString()).outE());
		}
		
		List<?> edgeids = graphtarget.E().id().toList();
		for(Object i : edgeids) {
			assertEquals(graphsource.E(i.toString()).valueMap().next().toString(),graphtarget.E(i.toString()).valueMap().next().toString());
		}
	}	
	
	/**
	 * A Example Test to show that multiple Properties can be compared
	 * @throws MalformedURLException
	 */
	@Test
	public void testMultipleProperties() throws MalformedURLException {
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
	