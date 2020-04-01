package info.hiergiltdiestfu.aws.neptune.graphml.Aws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.NeptuneAdapter;
/**
 * 
 * This class uploads an AWS backup file to AWS S3.
 * @author LUNOACK
 *
 */

@Component
public class AWSExporter {
	
	final Logger logger = LogManager.getLogger(AWSExporter.class);
	
	/**
	 * AWS S3 Connection
	 */
	private AmazonS3 amazons3;
	
	/**
	 * Amazon S3 BucketName
	 */
	private String awss3bucket;
	
    /**
     * Create Connection to AWS-S3, Configuration is in the properties File.
     * @param awsregion
     * @param awscredentials
     * @param awsbucket
     */
	@Autowired
	public AWSExporter(String awsregion, AWSCredentialsProvider awscredentials, String awsbucket) {
		this.amazons3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awscredentials)
                .withRegion(awsregion).build();
        this.awss3bucket = awsbucket;
	}
	
	/**
	 * Create temporary File
	 * @return tempfile = Temporary File 
	 */
	public File createTempFile() {
		logger.info("Creating File...");
		File tempfile = null;
		final String uuid = UUID.randomUUID().toString();
		
		try {
			tempfile =  File.createTempFile(uuid, ".xml");
		} catch (IOException e) {
			logger.error("Could not create File with Exception:\n {}",e);
		}
		
		writeFile(tempfile);
		
		return tempfile;
	}
	
	/**
	 * Write Graph Data on File
	 * @param tempfile = File where the Data gets written
	 * @return tempfile = File with Data
	 */
	public File writeFile(File tempfile) {
		logger.info("Write Database-Data on File...");
		
		try {
			new NeptuneAdapter().serialize(new FileWriter(tempfile));
		}catch(Exception e) {
			logger.error("Could not create Class NeptuneAdapter with Exception:\n {}",e);
		}
		
		uploadFile(tempfile);
		
		return tempfile;
	}
		
	/**
	 * Upload File 
	 * @param tempfile = File which gets uploaded to s3
	 */
	public PutObjectRequest uploadFile(File file) {		
		PutObjectRequest request = null;
		
		try {
			String filename = String.valueOf(System.currentTimeMillis());
			request = new PutObjectRequest(awss3bucket,filename,file);
			logger.info("Upload File with name {} to AWS...",filename);
			amazons3.putObject(request);
		}catch(AmazonS3Exception e) {
			logger.error("Could not Upload File to AWS with Exception:\n {}",e);
		} finally {
			
		}
		
		logger.info("Upload to AWS completed.");
		
		return request;
	}
		
	/**
	 * Delete Temporary File
	 * @param tempfile = Uploaded File which gets deleted
	 */
	
	public void deleteFile(File tempfile) {
		tempfile.deleteOnExit();
	}
}
