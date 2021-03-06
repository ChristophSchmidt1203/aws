package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.NeptuneAdapter;

/**
 * 
 * @author LUNOACK
 *
 *         This class compares the XML-File from the Rest with the Database
 *         Backup File.
 */
@SpringBootTest
public class CompareXMLRESTTest extends XMLTestCase {

	/**
	 * This is the String of the XML-File, which is created from NeptuneAdapter
	 */
	private String filexml;
	/**
	 * This is the String of the XML-File, which is created from the REST-API
	 */
	private String restxml;

	/**
	 * The Information of the Database
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * Creating a meaningful test environment. To ignore Comments, Whitespaces and
	 * Attribute order of the XML-Files.
	 */
	@BeforeAll
	static void setSettings() {
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
	}

	/**
	 * Test which compares the String of the File from the Database with the String
	 * from Rest
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	void testXMLResttoFile() throws SAXException, IOException {
		try {
			/**
			 * Get the XML-String from REST
			 */
			ImportedGraph importer = new ImportedGraph();
			restxml = importer.restToString();

			/**
			 * Get the XML-String from File
			 */
			File resource = null;
			resource = File.createTempFile("test", ".xml");
			new NeptuneAdapter(config.getdbPort(), config.getdbHost()).serialize(new FileWriter(resource));
			byte[] encoded = Files.readAllBytes(Paths.get((resource.getAbsolutePath())));
			filexml = new String(encoded, StandardCharsets.US_ASCII);

			resource.deleteOnExit();

		} catch (Exception e) {
			System.err.print(e);
		}
		assertXMLEqual(restxml, filexml);
	}
}
