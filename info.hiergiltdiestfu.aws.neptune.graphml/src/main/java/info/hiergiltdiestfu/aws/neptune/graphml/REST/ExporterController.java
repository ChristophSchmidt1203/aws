package info.hiergiltdiestfu.aws.neptune.graphml.Rest;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.NeptuneAdapter;


/**
 * This is the Controller for the Service,
 * Which Exports the Information of a Database into a REST-API.
 * @author LUNOACK
 *
 */
@RestController
public class ExporterController {

	final Logger logger = LogManager.getLogger(ExporterController.class);
	
	/**
	 * This Method calls the Class NeptuneAdapter.
	 * Which extracts the Data from a Database and prints it on an REST-API
	 * @param response
	 */
	@RequestMapping(value="/graphML", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			logger.info("Sercice to Export the Data to an Rest started.");
			final var writer = response.getWriter();
			new NeptuneAdapter().serialize(writer);
			writer.flush();
			
		} catch (Exception e) {
			logger.error("Service ended with an Exception: {}",e);
		}
	}
	
}
