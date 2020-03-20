package info.hiergiltdiestfu.aws.neptune.graphml.AWS;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * 
 * @author LUNOACK
 *
 */
public class AWSBackupEditor {
	
	public AWSBackupEditor() {
		
	}

	
	private AmazonS3 s3;
	
	/**
	 * Create a Connection to AWS
	 * @return S3-Connection to AWS
	 */
	public AmazonS3 createConnection() {
		BasicAWSCredentials cred = new BasicAWSCredentials("","");
		s3 = AmazonS3Client.builder()
			.withCredentials(new AWSStaticCredentialsProvider(cred))
			.withRegion("us-east-2")
			.build();
		
		return s3;
	}
	
	private final String bucket_name ="";
	
	/**
	 * Function which Lists all BackUps in the AWS-Bucket and searches for old ones
	 */
	public void setBackup() {
		createConnection();
		ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		
		for(S3ObjectSummary obj : objects) {
			if(keepBackup(obj)) {
				s3.deleteObject(bucket_name,obj.getKey());
			}
		}
	}
	
	/**
	 * Looking for old BackUps which can be deleted (Keep BackUps of the last 7 Days and keep each Sunday BackUp of the last 6 weeks)
	 * @param obj is The S3-Object 
	 * @return false = not meant to be deleted, true = meant to be deleted
	 */
	public boolean keepBackup(S3ObjectSummary obj) {
		Calendar datelast7d = getLast7Days();
		Calendar datelast6w = getLast6Weeks();
		
		Date objdate = obj.getLastModified();
		Calendar objcal = Calendar.getInstance();
		objcal.setTime(objdate);
		
		if(objdate.after(datelast7d.getTime())) 
			return false;
		else 
			if(objcal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				return true;
			else
				if(objdate.before(datelast6w.getTime()))
					return true;
				else
					return false;
	}
	
	/**
	 * get the date 7 days before today
	 * @return 
	 */
	public Calendar getLast7Days() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, - 7);
		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0);

		return cal;
	}
	
	/**
	 * get the date 6 weeks before today
	 * @return
	 */
	public Calendar getLast6Weeks() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, - 42);
		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0);

		return cal;
	}
}
