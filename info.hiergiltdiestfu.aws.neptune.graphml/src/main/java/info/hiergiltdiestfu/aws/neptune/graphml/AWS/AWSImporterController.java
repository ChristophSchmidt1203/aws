package info.hiergiltdiestfu.aws.neptune.graphml.AWS;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AWSImporterController {

	@RequestMapping(value="/graphAWSIMP", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			new AWSImporter();
			
		} catch (Exception e) {
			try {
				response.sendError(500, e.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
