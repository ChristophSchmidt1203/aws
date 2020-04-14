package info.hiergiltdiestfu.aws.neptune.graphml.createdatabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration File of the Database. With which we are working in the
 * Services.
 * 
 * @author LUNOACK
 *
 */
@Configuration
public class DatabaseConfiguration {

	@Value("${db-host}")
	private String host;

	@Value("${db-port}")
	private String port;

	/**
	 * Database-Port of the working Database
	 */
	@Bean(name = "port")
	public String getdbPort() {
		return port;
	}

	/**
	 * Database-Host of the working Database
	 */
	@Bean(name = "host")
	public String getdbHost() {
		return host;
	}
}
