package info.hiergiltdiestfu.aws.neptune.graphml.AWS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.ImportedGraph;
/**
 * 
 * @author LUNOACK
 *
 */
public class AWSImporter {
	
	private String data;
	private BuiltGraph graph;
	
	public AWSImporter() throws IOException, JAXBException {
		S3Object object = createConnection();
		setData(readAWSObject(object));
		setGraph(createDatabase(getData()));
	}
	
	/**
	 * Create AmazonS3-Connection
	 */
	public S3Object createConnection() {
		BasicAWSCredentials creds = new BasicAWSCredentials("", "");
		final AmazonS3 s3 = AmazonS3Client.builder()
			    .withRegion("us-east-2")
			    .withCredentials(new AWSStaticCredentialsProvider(creds))
			    .build();

		S3ObjectSummary obj = getLastObject(s3);
		System.out.println(obj.getKey());
		return s3.getObject("test-graph-backup",obj.getKey());
	}
	
	/**
	 * get latest Object (This Data will be filled in the DataBase)
	 *@param s3 AWS-S3 Connection
	 *@return the newest backup file in the Bucket
	 */
	public S3ObjectSummary getLastObject(AmazonS3 s3) {
		ListObjectsV2Result result = s3.listObjectsV2("test-graph-backup");
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		
		return Collections.max(objects, Comparator.comparing(c -> c.getLastModified()));
	}
	
	/**
	 * Read the File into a String
	 *@param object this data will be stored in the DataBase
	 *@return returns the String from the S3Object
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
	 * @param Data which is filled in the DataBase
	 * @return The BuiltGraph which creates the DataBase
	 * 
	 */
	public BuiltGraph createDatabase(String data) throws IOException, JAXBException {
		ImportedGraph importer = new ImportedGraph();
		importer.stringtoXMLFile(data);
		
		return new BuiltGraph(importer.getType());
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public BuiltGraph getGraph() {
		return graph;
	}

	public void setGraph(BuiltGraph graph) {
		this.graph = graph;
	}
}
