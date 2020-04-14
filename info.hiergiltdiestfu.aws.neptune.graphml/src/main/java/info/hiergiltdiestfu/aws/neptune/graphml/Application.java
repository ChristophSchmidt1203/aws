package info.hiergiltdiestfu.aws.neptune.graphml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class Application {
	
	static Logger logger = LogManager.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		logger.info("Start Application");
		SpringApplication.run(Application.class, args);
		
	}

}
