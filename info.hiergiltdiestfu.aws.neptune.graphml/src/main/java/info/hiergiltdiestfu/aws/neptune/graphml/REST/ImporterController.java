package info.hiergiltdiestfu.aws.neptune.graphml.REST;

import org.springframework.web.bind.annotation.RestController;

import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.BuiltGraph;
import info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase.ImportedGraph;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class ImporterController {

	
	@RequestMapping(value="/graphIMP", method=RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
	public void graphML(HttpServletResponse response) {
		try {
			ImportedGraph imp = new ImportedGraph();
			imp.restToString();
			new BuiltGraph(imp.getGraphmlType());
		} catch (Exception e) {
			try {
				response.sendError(500, e.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
