package info.hiergiltdiestfu.aws.neptune.graphml.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * This Class is the Configuration of the AWS-Connection. It manages the
 * connection options to AWS S3.
 * 
 * @author LUNOACK
 */
@Configuration
public class AmazonS3Configuration {

	@Value("${aws.access.key.id}")
	private String awskeyid;

	@Value("${aws.access.key.secret}")
	private String awssecretkey;

	@Value("${aws.region}")
	private String awsregion;

	@Value("${aws.s3.bucket}")
	private String bucketname;

	/**
	 * Gives the property for the AWSRegion for AWS.
	 * 
	 * @return
	 */
	@Bean(name = "awsregion")
	public String getAwsRegion() {
		return awsregion;
	}

	/**
	 * Gives the property for the AWSCredentials for AWS.
	 * 
	 * @return
	 */
	@Bean(name = "awscredentials")
	public AWSStaticCredentialsProvider getawsCredentials() {
		BasicAWSCredentials creds = new BasicAWSCredentials(this.awskeyid, this.awssecretkey);
		return new AWSStaticCredentialsProvider(creds);
	}

	/**
	 * Gives the property for the Bucket for AWS.
	 * 
	 * @return
	 */
	@Bean(name = "awsbucket")
	public String getBucketName() {
		return bucketname;
	}
}
