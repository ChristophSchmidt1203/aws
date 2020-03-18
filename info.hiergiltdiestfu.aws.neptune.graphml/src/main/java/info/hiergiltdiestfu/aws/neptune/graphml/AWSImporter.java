package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
/**
 * 
 * @author LUNOACK
 *
 */
public class AWSImporter {
	
	public AWSImporter() throws IOException, JAXBException {
		S3Object object = createConnection();
		String data = readAWSObject(object);
		createDatabase(data);
	}
	
	/**
	 * Create AmazonS3-Connection
	 */
	public S3Object createConnection() {
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

		return s3.getObject("bucket","graph_backup.xml");
	}
	
	/**
	 * Read the File into a String
	 * 
	 */
	public String readAWSObject(S3Object object) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
		
		return reader.lines().collect(Collectors.joining());
	}
	/**
	 * 
	 * Create DataBase
	 * @throws JAXBException 
	 * @throws IOException 
	 * 
	 */
	public BuiltGraph createDatabase(String data) throws IOException, JAXBException {
		ImportedGraph importer = new ImportedGraph();
		
		return new BuiltGraph(importer.type);
	}
}
