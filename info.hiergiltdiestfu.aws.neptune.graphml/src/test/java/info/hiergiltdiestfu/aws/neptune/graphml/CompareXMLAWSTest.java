package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.BeforeMethod;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import info.hiergiltdiestfu.aws.neptune.graphml.AWS.AWSImporter;
import info.hiergiltdiestfu.aws.neptune.graphml.REST.NeptuneAdapter;

/**
 * Test if the AWS-S3-XML is equals to the XML- which is correct to the Neptune-File
 * @author LUNOACK
 *
 */
public class CompareXMLAWSTest extends XMLTestCase{
	
	private static final String PATH =  "XMLInput//testimporter.xml";
	private static Resource resource = new ClassPathResource(PATH);
	private static String s;
	@BeforeAll
	static void setup() throws Exception  {
		/**
		 * Get the XML-String from File
		 */
		new NeptuneAdapter().serialize(new FileWriter(resource.getFile()));
		byte[] encoded = Files.readAllBytes(Paths.get((resource.getFile().getAbsolutePath())));
		filexml = new String(encoded,StandardCharsets.US_ASCII);
		
		/**
		 * Get the XML-String from AWS-S3
		 */
		AWSImporter awsimp = new AWSImporter();
		s3xml = awsimp.getData();
	}

	@BeforeMethod
	void setSettings() {
		XMLUnit.setIgnoreAttributeOrder(true);
	    XMLUnit.setIgnoreComments(true);
	    XMLUnit.setIgnoreWhitespace(true);
	}
	
	private static String filexml;
	private static String s3xml;
	
	/**
	 * Check if Strings are equal
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	void testXMLS3toFile() throws SAXException, IOException {
		Diff diffxml =DiffBuilder.compare(filexml).withTest(s3xml)
								.normalizeWhitespace().checkForSimilar()
								.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
								.build();
		System.out.println(diffxml);
		assertFalse(diffxml.toString(), diffxml.hasDifferences());
	}

}
