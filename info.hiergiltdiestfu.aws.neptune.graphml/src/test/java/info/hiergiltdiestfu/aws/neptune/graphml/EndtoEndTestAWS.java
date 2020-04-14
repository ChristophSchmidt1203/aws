package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import info.hiergiltdiestfu.aws.neptune.graphml.aws.AWSExporter;
import info.hiergiltdiestfu.aws.neptune.graphml.aws.AWSImporter;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.NeptuneAdapter;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;

/**
 * Here we compare the Databases. Which is created from AWS-S3 Backup File with
 * the Database which the Backup-File is created from.
 * 
 * @author LUNOACK
 *
 */
@SpringBootTest
public class EndtoEndTestAWS {

	/**
	 * Is the Database from which the Backup-File is created
	 */
	private static GraphTraversalSource graphsource;

	/**
	 * Is the Database which is created from the Backup-File
	 */
	private static GraphTraversalSource graphtarget;

	/**
	 * The class which exports the DB-Data to AWS-S3
	 */
	@Autowired
	private AWSExporter awsexp;

	/**
	 * The class which imports the DB-Data from AWS-S3
	 */
	@Autowired
	private AWSImporter awsimp;

	/**
	 * The class which builts the new Database.
	 */
	@Autowired
	private RefactorGraph refactor;

	/**
	 * The Information of the Database
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * Create a File for the upload to AWS-S3.
	 */
	private static File resource;

	@BeforeAll
	static void setup() throws Exception {
		/**
		 * Create a File
		 */
		try {
			resource = File.createTempFile("test", ".xml");
		} catch (IOException e) {
			System.out.print(e);
		}
	}

	/**
	 * Creates a GraphTraversalSource from the Database-Source. And a target
	 * Database which is created from the Backup File.
	 * 
	 * @throws Exception
	 */
	@Test
	void testVertieciesandEdges() throws Exception {
		NeptuneAdapter nep = new NeptuneAdapter(config.getdbPort(), config.getdbHost());
		nep.serialize(new FileWriter(resource));

		graphsource = nep.getG();
		/**
		 * Upload File to AWS S3
		 */
		PutObjectRequest req = awsexp.uploadFile(resource);

		/**
		 * Download File from AWS S3
		 */
		S3Object obj = awsimp.getBackupObject(req.getKey());
		String s3xml = awsimp.readAWSObject(obj);

		ImportedGraph graph = new ImportedGraph();

		/**
		 * Create a Database based on the XML from AWS S3.
		 */
		graph.stringtoXMLFile(s3xml);

		refactor.restoreDatabase(graph.getGraphmlType());

		graphtarget = refactor.getGraph();

		testVertex();

		testEdge();
	}

	/**
	 * Test if all Verticies are the same, ammount, properties
	 */
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
