package info.hiergiltdiestfu.aws.neptune.graphml.Aws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * This class is there to see which backups must be kept and which can be deleted. 
 * Backups are managed according to the time of their creation, keeping the backups of the last 7 days and the Sunday backups of the last 6 weeks.
 * @author LUNOACK
 *
 */
@Component
public class AWSBackupEditor {
	
	final Logger logger = LogManager.getLogger(AWSBackupEditor.class);
	
	/**
	 * Bucket Name for AWS S3
	 */
	private String awss3bucket;
	
	/**
	 * AWS S3 Conection
	 */
    private AmazonS3 amazons3;
	
    /**
     * Create Connection to AWS-S3, Configuration is in the properties File.
     * @param awsregion
     * @param awscredentials
     * @param awsbucket
     */
    @Autowired
	public AWSBackupEditor(String awsregion, AWSCredentialsProvider awscredentials, String awsbucket) {
		this.amazons3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awscredentials)
                .withRegion(awsregion).build();
        this.awss3bucket = awsbucket;
	}
	
	/**
	 * A function that looks which backups can be deleted.
	 */
	public void deleteBackup() {
		logger.info("Start Backup-Editor");
		
		Calendar cal = Calendar.getInstance();
		
		Calendar dayslast7 = getLast7Days(cal);
		Calendar weekslast6 = getLast6Weeks(cal);
		
		logger.info("Get Backups from AWS...");
		ListObjectsV2Result result = amazons3.listObjectsV2(awss3bucket);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		
		ArrayList<KeyVersion> keys = new ArrayList<>();
		logger.info("These backups are deleted: ");
		for(S3ObjectSummary obj : objects) {
			if(shoulddeleteBackup(obj,dayslast7,weekslast6)) {
				logger.info("Backup: {} Date: {}",obj.getKey(),obj.getLastModified());
				keys.add(new KeyVersion(obj.getKey()));
			}
		}

		
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(awss3bucket)
                .withKeys(keys)
                .withQuiet(false);
		logger.info("Delete...");
		amazons3.deleteObjects(deleteObjectsRequest);
		logger.info("Backups have been deleted.");
	}
	
	/**
	 * Checks when the backups were created  (Keep BackUps of the last 7 Days and keep each Sunday BackUp of the last 6 weeks)
	 * @param obj is The S3-Object 
	 * @return true = keep the BackUp, false = delete the BackUp 
	 */
	public boolean shoulddeleteBackup(S3ObjectSummary obj, Calendar dayslast7, Calendar weekslast6) {	
		Date objdate = obj.getLastModified();
		Calendar objcal = Calendar.getInstance();
		objcal.setTime(objdate);
		
		if(objdate.after(dayslast7.getTime())) {
			return false;
		}
		else { 
			if(objcal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				return true;
			}
			else {
				 return objdate.before(weekslast6.getTime());
			}
		}			
	}
	
	/**
	 * get the date 7 days before today
	 * @return 
	 */
	public Calendar getLast7Days(Calendar cal) {
		Calendar calinstance = Calendar.getInstance();
		calinstance.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0);
		calinstance.add(Calendar.DATE, - 7);

		return calinstance;
	}
	
	/**
	 * get the date 6 weeks before today
	 * @return
	 */
	public Calendar getLast6Weeks(Calendar cal) {
		Calendar calinstance = Calendar.getInstance();
		calinstance.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0);
		calinstance.add(Calendar.DATE, - 42);
	
		return calinstance;
	}
}
