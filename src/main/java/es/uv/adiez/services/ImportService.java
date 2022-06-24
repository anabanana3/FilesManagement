package es.uv.adiez.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoBulkWriteException;

import es.uv.adiez.entities.FileSQL;
import es.uv.adiez.entities.User;
import es.uv.adiez.entities.File;
import es.uv.adiez.repositories.FileRepository;

@Service
public class ImportService {
	
	@Autowired
    private FileRepository fs;
	@Value("${enpoint.usersAPI}")
	private String usersURL;

	private List<File> generateMongoDocs(List<String> lines) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    List<File> docs = new ArrayList<>();
	    for (String e : lines) {
	    	File x = mapper.readValue(e, File.class);
	        docs.add(x);
	    }
	    System.out.println("[I] ImportService: readed "+docs.size()+" items");
	    return docs;
	}
	
	private int insert(List<File> mongoDocs) {
	    try {
	    	fs.saveAll(mongoDocs); 
	    	saveInSQL(mongoDocs);
	        return 1;
	    } 
	    catch (DataIntegrityViolationException e) {
	        if (e.getCause() instanceof MongoBulkWriteException) {
	            return ((MongoBulkWriteException) e.getCause())
	              .getWriteResult()
	              .getInsertedCount();
	        }
	        return 0;
	    }
	}
		
	public int doImport(Resource resource) throws JsonMappingException, JsonProcessingException {
	    ArrayList<String> jsonlines = new ArrayList<>();
	    try (Scanner s = new Scanner(resource.getFile())) {
	        while (s.hasNext()) {
	        	jsonlines.add(s.nextLine());
	        }
	    }
	    catch(IOException e) {
	    	System.out.println("[ERROR] data file not found");
	    }
	    
	    List<File> mongoDocs = generateMongoDocs(jsonlines);
	    return insert(mongoDocs);
	}
	
	public void saveInSQL(List<File> mongoDocs) {
		mongoDocs.stream().forEach(f -> {
			User u = new User("diezmara@producer.uv.es", "", "11111F", "", User.PersonType.F);
			FileSQL file = new FileSQL(f.getId(), null, u, 0, 0 );
			createSQL(file);
		});
	}
	
	public Boolean createSQL(FileSQL file) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/fileAPI";
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	     
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, file, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 201) {
		   return true;
	   }
	   return false;
	}
}
