package info.hiergiltdiestfu.aws.neptune.graphml.rest;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.DatabaseConfiguration;
import info.hiergiltdiestfu.aws.neptune.graphml.createdatabase.NeptuneAdapter;

/**
 * This is the Controller for the Service, Which Exports the Information of a
 * Database into a REST-API.
 * 
 * @author LUNOACK
 */
@RestController
public class ExporterController {

	final Logger logger = LogManager.getLogger(ExporterController.class);

	/**
	 * The Configuration of the Database-host and -port.
	 */
	@Autowired
	private DatabaseConfiguration config;

	/**
	 * This Method calls the Class NeptuneAdapter. Which extracts the Data from a
	 * Database and prints it on an REST-API
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/RestExport", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			logger.info("Sercice to Export the Data to an Rest started.");
			final var writer = response.getWriter();
			NeptuneAdapter neptune = new NeptuneAdapter(config.getdbPort(), config.getdbHost());
			neptune.serialize(writer);
			writer.flush();
			watch.stop();
			logger.info("Service to create a Backup File finished.");
			logger.info("Service Duration in Seconds: {} ms", watch.getTime(TimeUnit.MILLISECONDS));

		} catch (Exception e) {
			logger.error("Service could not start Exception: {}", e);
		}
	}

}
