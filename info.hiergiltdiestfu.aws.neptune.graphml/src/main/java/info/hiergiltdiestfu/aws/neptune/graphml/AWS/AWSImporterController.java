package info.hiergiltdiestfu.aws.neptune.graphml.aws;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;

/**
 * This Class is the Controller for the Service, to Import the Data of AWS into
 * a Database.
 * 
 * @author LUNOACK
 *
 */
@Controller
public class AWSImporterController {

	final Logger logger = LogManager.getLogger(AWSImporterController.class);

	/**
	 * Class to Import the BackupFile from AWS-S3.
	 */
	@Autowired
	private AWSImporter importaws;

	/**
	 * Class to Export the BackupFile to AWS-S3.
	 */
	@Autowired
	private AWSExporter exportaws;

	/**
	 * Class to restore the Database.
	 */
	@Autowired
	private RefactorGraph refactor;

	/**
	 * The Configuration of the Database-host and -port.
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * This Method created the Class AWSImporter. And calls the function
	 * createDatabase(), which is responsible to Import the Data from AWS and
	 * putting it into a new Database.
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/AWSImport", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			logger.info("Service to restore a Database was called.");
			String data = importaws.createDatabase();
			ImportedGraph imp = new ImportedGraph();
			imp.stringtoXMLFile(data);
			createBackupfile();
			refactor.restoreDatabase(imp.getGraphmlType());
			watch.stop();
			logger.info("Service to restore a Database finished.");
			logger.info("Service Duration in Seconds: {} ms", watch.getTime(TimeUnit.MILLISECONDS));

		} catch (Exception e) {
			logger.error("Service could not start Exception: {}", e);
		}
	}

	/**
	 * This Method created the Class AWSImporter. And calls the function
	 * createDatabase(), which is responsible to Import the Data from AWS and
	 * putting it into a new Database. The @param id can be given, which searches
	 * for the same Filename in AWS and imports that File to the Database.
	 * 
	 * @param response
	 * @param id ID-of the imported Backup File
	 */
	@RequestMapping(value = "/AWSImport/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public void graphMLwithID(HttpServletResponse response, @PathVariable String id) {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			importaws.setBackupfile(id);
			logger.info("Service to restore a Database was called with Backup-ID: {}", id);
			String data = importaws.createDatabase();
			ImportedGraph imp = new ImportedGraph();
			imp.stringtoXMLFile(data);
			createBackupfile();
			refactor.restoreDatabase(imp.getGraphmlType());
			importaws.setBackupfile("");
			watch.stop();
			logger.info("Service to restore a Database finished.");
			logger.info("Service Duration in Seconds: {} ms", watch.getTime(TimeUnit.MILLISECONDS));

		} catch (Exception e) {
			logger.error("Service could not start Exception: {}", e);
		}
	}

	/**
	 * Function to create a Backup-File of the existing Database. It will be called
	 * before the new Data gets filled in the Database.
	 */
	public void createBackupfile() {
		logger.info("Create a Backup File.");
		exportaws.createFileforUpload(config.getdbHost(), config.getdbPort());
		logger.info("Backup File was created.");
	}
}
