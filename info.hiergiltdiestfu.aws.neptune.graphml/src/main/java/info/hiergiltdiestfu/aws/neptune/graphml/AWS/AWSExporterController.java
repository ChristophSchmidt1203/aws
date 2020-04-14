package info.hiergiltdiestfu.aws.neptune.graphml.aws;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;

/**
 * This Class is the Controller for the Service, to Export the Information of
 * the Database into AWS.
 * 
 * @author LUNOACK
 *
 */
@Controller
public class AWSExporterController {

	final Logger logger = LogManager.getLogger(AWSExporterController.class);

	/**
	 * Class that upload the backup-file to AWS-S3.
	 */
	@Autowired
	private AWSExporter exporteraws;

	/**
	 * Class that checks if backup-files are obsolete.
	 */
	@Autowired
	private AWSBackupEditor editoraws;

	/**
	 * The Configuration of the Database-host and -port.
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * This Function calls the Method uploadFile() which is responsible for creating
	 * a Backup-File and Uploading it into AWS.
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/AWSExport", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			StopWatch watch = new StopWatch();
			editoraws.deleteBackup();
			watch.start();
			logger.info("Service to create a Backup File was called.");
			exporteraws.createFileforUpload(config.getdbHost(), config.getdbPort());
			watch.stop();
			logger.info("Service to create a Backup File finished.");
			logger.info("Service Duration in Seconds: {} ms", watch.getTime(TimeUnit.MILLISECONDS));

		} catch (Exception e) {
			logger.error("Service could not start Exception: {}", e);
		}
	}
}
