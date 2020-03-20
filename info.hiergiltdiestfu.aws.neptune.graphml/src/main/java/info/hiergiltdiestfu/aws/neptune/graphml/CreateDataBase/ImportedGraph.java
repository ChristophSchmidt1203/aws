/*
 * 
 * 
 * 
 */
package info.hiergiltdiestfu.aws.neptune.graphml.CreateDataBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.graphdrawing.graphml.xmlns.GraphmlType;
import org.graphdrawing.graphml.xmlns.ObjectFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 
 * @author LUNOACK
 *
 */
public class ImportedGraph {

	private static final String PATH =  "XMLInput//testimporter.xml";
	private static Resource resource = new ClassPathResource(PATH);
	private GraphmlType type;
	/**
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	
	
	public ImportedGraph() {
	}
	/**
	 * Information from a Rest-API to a local String
	 * @return String with the Data from the API
	 * @throws MalformedURLException
	 */
	public String restToString() throws MalformedURLException {

		URL url = new URL("http://localhost:8081/graphML"); //Syntax aus Aplliprop
		StringBuilder response = new StringBuilder();
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String input ;
			while((input=in.readLine()) != null) {
				response.append(input + "\n");
			}
			
			in.close();
			stringtoXMLFile(response.toString());

		}catch(Exception e) {
			System.out.print(e);
		}
		return response.toString();
	}
	
	/**
	 * Creates a File with the data of the parameter
	 * @param input String which is written on the file
	 * @return 
	 * @throws IOException
	 */
	public void stringtoXMLFile(String input) throws IOException {
		
		try (	FileWriter writer = new FileWriter(resource.getFile().getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(writer)){
			
			bw.write(input);
			
		}catch(Exception e) {
			System.err.print(e);
		} 
		xmlfiletoEntity();
	}
	
	/**
	 * Takes the file and creates a GraphmlType, with all properties of the graph
	 */
	public void xmlfiletoEntity(){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			setType((GraphmlType) ((JAXBElement<?>)unmarshaller.unmarshal(new FileInputStream(resource.getFile()))).getValue());

		} catch (Exception e) {
			System.err.println(e);
		}
	}
	/**
	 * 
	 * @return Properties of the graph
	 */
	public GraphmlType getGraphmlType() {
		return getType();
	}
	public GraphmlType getType() {
		return type;
	}
	public void setType(GraphmlType type) {
		this.type = type;
	}
}