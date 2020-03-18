package info.hiergiltdiestfu.aws.neptune.graphml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class Application {
	
	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
		
	}

}
