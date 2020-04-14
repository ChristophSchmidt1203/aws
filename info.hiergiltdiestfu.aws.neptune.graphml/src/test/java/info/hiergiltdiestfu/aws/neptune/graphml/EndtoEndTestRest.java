package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.NeptuneAdapter;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;

/**
 * End-End-Test Look if the starting Database is equal to the new created from
 * Rest.
 * 
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

	/**
	 * Creates a new Database from the Rest-Connection and a Source-Database
	 * 
	 * @throws Exception
	 */
	@BeforeAll
	public static void setup() throws Exception {
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		final var write = httpServletResponse.getWriter();
		NeptuneAdapter inend = new NeptuneAdapter("8182", "localhost");
		inend.serialize(write);
		graphsource = inend.getG();

		RefactorGraph built;

		/**
		 * Get the Properties from the Rest. And store them into a Local Database. In
		 * BuiltGraph the LocalDatabase has to be open.
		 */
		ImportedGraph imp = new ImportedGraph();
		imp.restToString();
		built = new RefactorGraph("8182", "localhost");
		graphtarget = built.getGraph();

	}

	/**
	 * Test if all Verticies are the same, ammount, properties
	 */
	@Test
	public void testVertex() {
		long numbervertexsource = IteratorUtils.count(graphsource.V());
		assertEquals(numbervertexsource, IteratorUtils.count(graphtarget.V())); // notempty
		assertTrue(numbervertexsource > 0);

		List<Object> stvert = graphsource.V().properties().with(WithOptions.tokens).unfold().toList();
		List<Object> endvert = graphtarget.V().properties().with(WithOptions.tokens).unfold().toList();

		stvert.removeAll(endvert);
		assertEquals(true, stvert.isEmpty());

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
		assertEquals(numberedgesource, IteratorUtils.count(graphtarget.E()));
		assertTrue(numberedgesource > 0);

		List<?> ids = graphtarget.V().id().toList();
		for (Object i : ids) {
			assertEquals(graphsource.V(i.toString()).outE(), graphtarget.V(i.toString()).outE());
		}

		List<?> edgeids = graphtarget.E().id().toList();
		for (Object i : edgeids) {
			assertEquals(graphsource.E(i.toString()).valueMap().next().toString(),
					graphtarget.E(i.toString()).valueMap().next().toString());
		}
	}
}
