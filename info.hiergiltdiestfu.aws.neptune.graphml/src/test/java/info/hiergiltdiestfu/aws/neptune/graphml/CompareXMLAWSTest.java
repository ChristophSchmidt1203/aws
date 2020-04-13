package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import info.hiergiltdiestfu.aws.neptune.graphml.aws.AWSExporter;
import info.hiergiltdiestfu.aws.neptune.graphml.aws.AWSImporter;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.NeptuneAdapter;

/**
 * Test if the AWS-S3-XML is equals to the XML- which is correct to the
 * Neptune-File
 * 
 * @author LUNOACK
 *
 */
@SpringBootTest
public class CompareXMLAWSTest extends XMLTestCase {

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
	 * The Information of the Database
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * This is the String of the XML-File, which is created from NeptuneAdapter
	 */
	private String filexml;
	/**
	 * This is the String of the XML-File, which is created from the AWS-S3
	 */
	private String s3xml;

	/**
	 * File which is downloaded to the AWS
	 */
	private File resource;

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
	 * Check if Strings are equal.
	 */
	@Test
	void testXMLS3toFile() {
		/**
		 * Load the Data of the Database into a File. And then put it into a String
		 */
		try {
			resource = File.createTempFile("test", ".xml");
			new NeptuneAdapter(config.getdbPort(), config.getdbHost()).serialize(new FileWriter(resource));
			byte[] encoded = Files.readAllBytes(Paths.get((resource.getAbsolutePath())));
			filexml = new String(encoded, StandardCharsets.US_ASCII);
		} catch (Exception e) {
			System.out.print(e);
		}

		/**
		 * Upload File to AWS S3
		 */
		PutObjectRequest req = awsexp.uploadFile(resource);

		/**
		 * Download File from AWS S3
		 */
		S3Object obj = awsimp.getBackupObject(req.getKey());
		s3xml = awsimp.readAWSObject(obj);

		/**
		 * Compare Initital File with Downloaded
		 */
		Diff diffxml = DiffBuilder.compare(filexml).withTest(s3xml).normalizeWhitespace().checkForSimilar()
				.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)).build();
		assertFalse(diffxml.toString(), diffxml.hasDifferences());
	}
}
