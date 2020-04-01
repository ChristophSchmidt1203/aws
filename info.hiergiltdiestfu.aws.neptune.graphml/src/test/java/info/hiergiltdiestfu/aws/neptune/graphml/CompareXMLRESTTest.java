package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeMethod;
import org.xml.sax.SAXException;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.NeptuneAdapter;
/**
 * 
 * @author LUNOACK
 *
 *Start the Programm to make the REST available
 */
public class CompareXMLRESTTest extends XMLTestCase{
	
	/**
	 * This is the String of the XML-File, which is created from NeptuneAdapter
	 */
	private static String filexml;
	/**
	 * This is the String of the XML-File, which is created from the REST-API
	 */
	private static String restxml;
	
	@BeforeAll
	static void setup() throws IOException, JAXBException  {
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
			resource =  File.createTempFile("test", ".xml");
			
			new NeptuneAdapter().serialize(new FileWriter(resource));
			byte[] encoded = Files.readAllBytes(Paths.get((resource.getAbsolutePath())));
			filexml = new String(encoded,StandardCharsets.US_ASCII);
			
			resource.deleteOnExit();
			
		}catch(Exception e) {
			System.err.print(e);
		}	
	}
	
	@BeforeMethod
	void setSettings() {
		XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
	}
	
	/**
	 * Test which compares the String of the File from the Database with the String from Rest
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	void testXMLResttoFile() throws SAXException, IOException {
		System.out.println(restxml);
		assertXMLEqual(restxml,filexml);
	}
}
