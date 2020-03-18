package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AWSExporter {
	
	private AmazonS3 s3conn;
	private static final String FILENAME = "graph_backup.xml";
	private static final String BUCKETNAME = ""; //Name??	//Bucket da ??
	
	public AWSExporter() {
		var file = createTempFile();
		file = writeFile(file);
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
			FileWriter temp = new FileWriter(tempfile);
			new NeptuneAdapter().serialize(temp);
		}catch(Exception e) {
			System.err.print(e);
		}
		return tempfile;
	}
	
	public void createConnection() {
		s3conn = AmazonS3ClientBuilder.defaultClient();
	}
	
	/**
	 * delete existing file before uploading the new one
	 * 
	 */
	//Löschen lassen(BackUPS aufheben nach Meilensteinen aufheben)
	public void deleteAWSFile() {//Historie File = name Zeitstempel System.currenttimesmillis
		try {
			s3conn.deleteObject(BUCKETNAME,FILENAME);
		}catch(AmazonServiceException e) {
			System.err.println(e);
		}
	}
	
	/**
	 * Upload File 
	 * @param tempfile = File which gets uploaded to s3
	 */
	public void uploadFile(File tempfile) {
		try {
			//s3conn.createBucket(BUCKETNAME); //??
			PutObjectRequest request = new PutObjectRequest(BUCKETNAME,FILENAME,tempfile);
			ObjectMetadata metadata = new ObjectMetadata();
			//metadata.setContentEncoding("backup/xml");
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
