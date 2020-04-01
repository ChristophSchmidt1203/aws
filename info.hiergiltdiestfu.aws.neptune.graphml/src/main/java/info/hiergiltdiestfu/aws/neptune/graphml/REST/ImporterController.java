package info.hiergiltdiestfu.aws.neptune.graphml.Rest;

import org.springframework.web.bind.annotation.RestController;

import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.Createdatabase.ImportedGraph;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/**
 * This Class is the Controller for the Service,
 * which gets Data from an REST-API and exctracts it into an Database.
 * @author LUNOACK
 *
 */
@RestController
public class ImporterController {

	final Logger logger = LogManager.getLogger(ExporterController.class);
	
	/**
	 * This Method created the Class ImportedGraph,
	 * which takes the Data from an REST and extracts the Information into the System.
	 * After that the Class BuiltGraph is called,
	 * which creates an Database which the Data.
	 * @param response
	 */
	@RequestMapping(value="/graphIMP", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			logger.info("Service to Import the Data from an Rest started.");
			ImportedGraph imp = new ImportedGraph();
			imp.restToString();
			new BuiltGraph(imp.getGraphmlType());
		} catch (Exception e) {
			logger.error("Service ended with an Exception: {}",e);
		}			
	}
}
