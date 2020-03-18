package info.hiergiltdiestfu.aws.neptune.graphml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeMethod;
import org.xml.sax.SAXException;

public class CompareXMLTest extends XMLTestCase{

	private final static String PATH =  "C:\\Users\\LUNOACK\\eclipse-workspace\\aws-master\\aws-master\\info.hiergiltdiestfu.aws.neptune.graphml\\src\\main\\resources\\XMLInput\\testimporter.xml";
	//Ressources Verzeichnis beziehen ohne absoluten 
	//signatur überprüfen 
	@SuppressWarnings("resource")
	@BeforeAll
	static void setup()  {
		try {
			
			ImportedGraph importer = new ImportedGraph();
			apixml = importer.restToString();
			
		 	BufferedReader reader = new BufferedReader(new FileReader(PATH));
		    String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");
		    
		    while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }
		    
		    filexml = stringBuilder.toString();
		}catch(Exception e) {
			System.err.print(e);
		}	
	}
	
	@BeforeMethod
	void setSettings() {
		XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
	}
	
	private static String apixml;
	private static String filexml;
	
	@Test
	void testXMLParsing() throws SAXException, IOException {
		assertXMLEqual(apixml,filexml);
	}
}
