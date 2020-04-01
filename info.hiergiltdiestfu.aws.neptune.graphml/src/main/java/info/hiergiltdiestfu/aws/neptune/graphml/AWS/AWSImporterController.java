package info.hiergiltdiestfu.aws.neptune.graphml.Aws;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This Class is the Controller for the Service,
 * to Import the Data of AWS into a Database.
 * @author LUNOACK
 *
 */
@Controller
public class AWSImporterController {

	final Logger logger = LogManager.getLogger(AWSImporterController.class);
	
	@Autowired
	private AWSImporter importaws;
	
	/**
	 * This Method created the Class AWSImporter.
	 * And calls the function createDatabase(), 
	 * which is responsible to Import the Data from AWS and putting it into a new Database
	 * @param response
	 */
	@RequestMapping(value="/graphAWSIMP", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			logger.info("Service to restore a Database was called.");
			importaws.createDatabase();
			
		} catch (Exception e) {
			logger.error("Sevice finished with an Exception: {}",e);
		}
	}
}
