package info.hiergiltdiestfu.aws.neptune.graphml.AWS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import info.hiergiltdiestfu.aws.neptune.graphml.REST.NeptuneAdapter;

public class AWSExporter {
	
	private AmazonS3 s3conn;
	private static final String BUCKET_NAME = "test-graph-backup";
	
	public AWSExporter() {
		var file = createTempFile();
		file = writeFile(file);
		createConnection();
		uploadFile(file);
	}
	
	/**
	 * Create temporary File
	 * @return tempfile = Temporary File 
	 */
	public File createTempFile() {
		File tempfile = null;
		final String uuid = UUID.randomUUID().toString();
		try {
			tempfile =  File.createTempFile(uuid, ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tempfile;
	}
	
	/**
	 * Write Graph Data on File
	 * @param tempfile = File where the Data gets written
	 * @return tempfile = File with Data
	 */
	public File writeFile(File tempfile) {
		try {
			new NeptuneAdapter().serialize(new FileWriter(tempfile));
		}catch(Exception e) {
			System.err.print(e);
		}
		return tempfile;
	}
	/**
	 * Create a Connection to AWS
	 */
	public void createConnection() {
		BasicAWSCredentials cred = new BasicAWSCredentials("","");
		s3conn = AmazonS3Client.builder()
				.withCredentials(new AWSStaticCredentialsProvider(cred))
				.withRegion("us-east-2")
				.build();
	}	
	
	/**
	 * Upload File 
	 * @param tempfile = File which gets uploaded to s3
	 */
	public void uploadFile(File tempfile) {
		try {
			String filename = String.valueOf(System.currentTimeMillis());
			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME,filename,tempfile);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.addUserMetadata("", "");
			request.setMetadata(metadata);
			s3conn.putObject(request);
		}catch(AmazonS3Exception e) {
			System.err.println(e);
		}
	}
		
	/**
	 * Delete Temporary File
	 * @param tempfile = Uploaded File which gets deleted
	 */
	
	public void deleteFile(File tempfile) {
		tempfile.deleteOnExit();
	}
}
