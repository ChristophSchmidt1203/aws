package info.hiergiltdiestfu.aws.neptune.graphml.Aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.ImportedGraph;
/**
 * 
 * This class imports the backup data from AWS and creates instances of ImportedGraph, which ensures that the data is imported into the database.
 * @author LUNOACK
 *
 */

@Component
public class AWSImporter {
	
	/**
	 * The Backup File which will be imported.  
	 */
	@Value("${aws.backup-file}")
	String backupfile;
	
	final Logger logger = LogManager.getLogger(AWSImporter.class);
	
	/**
	 * Amazon S3 BucketName
	 */
	private String awss3bucket;
	
	/**
	 * Amazon S3 Connection
	 */
    private AmazonS3 amazons3;
    
	/**
	 * This is the Data which is extraxted from AWS.
	 */
	private String data;
	/**
	 * BuiltGraph creates the Database and writes the Data in.
	 */
	private BuiltGraph graph;
	
    /**
     * Create Connection to AWS-S3, Configuration is in the properties File.
     * @param awsregion
     * @param awscredentials
     * @param awsbucket
     */
	@Autowired
	public AWSImporter(String awsregion, AWSCredentialsProvider awscredentials, String awsbucket) {
		this.amazons3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awscredentials)
                .withRegion(awsregion).build();
        this.awss3bucket = awsbucket;
	}
	
	/**
	 * Gets the Backup File of the Parameter or 
	 * get latest Object (This Data will be filled in the DataBase)
	 * from AWS S3
	 *@param s3 AWS-S3 Connection
	 *@param backupfile String Name of the Backup File
	 *@return the newest backup file in the Bucket
	 */
	public S3Object getBackupObject(String backupfile) {
		if(backupfile.equals("")) {
			ListObjectsV2Result result = amazons3.listObjectsV2(awss3bucket);
			List<S3ObjectSummary> objects = result.getObjectSummaries();
			
			return amazons3.getObject(awss3bucket,Collections.max(objects, Comparator.comparing(c -> c.getLastModified())).getKey());
		}else {
			return amazons3.getObject(awss3bucket,backupfile);
		}
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
	 * Create a Connection to AWS and downloades a Backup-File (Default File is the Latest) 
	 * After that it reads the File into a String and extracts the XML into the classes
	 * In the End it Creates the Class BuiltGraph which creates the Database.
	 * @throws JAXBException 
	 * @throws IOException 
	 * @param Data which is filled in the DataBase
	 * @return The BuiltGraph which creates the DataBase
	 * 
	 */
	public BuiltGraph createDatabase() throws IOException {
		logger.info("Create AWS-Connection...");
		
		S3Object obj = getBackupObject(backupfile);
		
		logger.info("Back up File: {} will be imported",obj.getKey());
		logger.info("Read Backup File from AWS...");
		setData(readAWSObject(obj));
		
		logger.info("Create Database...");
		ImportedGraph importer = new ImportedGraph();
		
		importer.stringtoXMLFile(data);
		
		this.graph = new BuiltGraph(importer.getType());
		
		logger.info("Database created.");
		
		return graph;
	}

	/**
	 * Data on the Backup File.
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * Data on the Backup File.
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Class which creates the Database.
	 * @return
	 */
	public BuiltGraph getGraph() {
		return graph;
	}

	/**
	 * Class which creates the Database.
	 * @param graph
	 */
	public void setGraph(BuiltGraph graph) {
		this.graph = graph;
	}
}
