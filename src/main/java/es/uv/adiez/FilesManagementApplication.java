package es.uv.adiez;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.uv.adiez.services.ImportService;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"es.uv.adiez.repositories"})
public class FilesManagementApplication implements ApplicationRunner{

	 @Autowired
	 private ImportService is;
	 
	public static void main(String[] args) {
		SpringApplication.run(FilesManagementApplication.class, args);
	}
	
	@Override
	 public void run(ApplicationArguments args) throws JsonMappingException, JsonProcessingException {
		Resource resource = new ClassPathResource("data.txt");
		is.doImport(resource);
        
	}

}
