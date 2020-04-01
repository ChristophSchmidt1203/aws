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
 * to Export the Information of the Database into AWS.
 * @author LUNOACK
 *
 */
@Controller
public class AWSExporterController {
	
	final Logger logger = LogManager.getLogger(AWSExporterController.class);
	
	@Autowired
	AWSExporter exporteraws;
	/**
	 * This Method creates the Class AWSExporter.
	 * And calls the Method uploadFile() which is responsible for creating a Backup-File and Uploading it into AWS.
	 * @param response
	 */
	@RequestMapping(value="/graphAWS", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			logger.info("Service to create a Backup File was called.");
			exporteraws.createTempFile();
			
		} catch (Exception e) {
			logger.error("Service finished with Exception: {}",e);
		}
	}
}
