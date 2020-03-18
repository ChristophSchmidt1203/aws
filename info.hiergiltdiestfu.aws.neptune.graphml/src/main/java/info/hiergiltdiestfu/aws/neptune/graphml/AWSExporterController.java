package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AWSExporterController {
	@RequestMapping(value="/graphAWS", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			File temp = File.createTempFile("graph", ".txt", new File("C:\\Users\\LUNOACK\\Desktop"));
			Writer writer = new FileWriter(temp);
			new NeptuneAdapter().serialize(writer);
			AWSExporter export = new AWSExporter();
			writer.flush();
			if(temp.delete())
				System.out.println("TRUE");
			
		} catch (Exception e) {
			try {
				response.sendError(500, e.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
