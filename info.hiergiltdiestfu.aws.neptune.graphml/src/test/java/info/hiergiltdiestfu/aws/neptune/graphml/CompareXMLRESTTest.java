package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.BeforeMethod;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import info.hiergiltdiestfu.aws.neptune.graphml.AWS.AWSImporter;
import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.REST.NeptuneAdapter;
/**
 * 
 * @author LUNOACK
 *
 *Start the Programm to make the REST available
 */
public class CompareXMLRESTTest extends XMLTestCase{

	private static final String PATH =  "XMLInput//testimporter.xml";
	private static Resource resource = new ClassPathResource(PATH);
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
			new NeptuneAdapter().serialize(new FileWriter(resource.getFile()));
			byte[] encoded = Files.readAllBytes(Paths.get((resource.getFile().getAbsolutePath())));
			filexml = new String(encoded,StandardCharsets.US_ASCII);
			
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
	
	private static String restxml;
	private static String filexml;
	
	
	@Test
	void testXMLResttoFile() throws SAXException, IOException {
		assertXMLEqual(restxml,filexml);
	}
}
