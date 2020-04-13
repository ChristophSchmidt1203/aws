package info.hiergiltdiestfu.aws.neptune.graphml.rest;

import org.springframework.web.bind.annotation.RestController;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.ImportedGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.RefactorGraph;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This Class is the Controller for the Service, which gets Data from an
 * REST-API and exctracts it into an Database.
 * 
 * @author LUNOACK
 *
 */
@RestController
public class ImporterController {

	final Logger logger = LogManager.getLogger(ExporterController.class);

	/**
	 * Class to restore the Database.
	 */
	@Autowired
	RefactorGraph refactor;

	/**
	 * This Method created the Class ImportedGraph, which takes the Data from an
	 * REST and extracts the Information into the System. After that the Class
	 * BuiltGraph is called, which creates an Database which the Data.
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/RestImport", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {

			// input string http parameter requestparameter
			StopWatch watch = new StopWatch();
			watch.start();
			logger.info("Service to Import the Data from an Rest started.");
			ImportedGraph imp = new ImportedGraph();
			imp.restToString();
			refactor.restoreDatabase(imp.getGraphmlType());
			watch.stop();
			logger.info("Service to restore a Database finished.");
			logger.info("Service Duration in Seconds: {} ms", watch.getTime(TimeUnit.MILLISECONDS));
		} catch (Exception e) {
			logger.error("Service could not start Exception: {}", e);
		}
	}
}
