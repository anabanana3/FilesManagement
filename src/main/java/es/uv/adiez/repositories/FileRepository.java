package es.uv.adiez.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.uv.adiez.entities.File;

public interface FileRepository  extends MongoRepository<File, String>{
	Optional<File> findById(int fileId);
	List<File> findByKeywords(String tags);
	List<File> findByKeywordsIn(List<String> tags);
	List<File> findByStatus(File.Status status);
	@Query("{_id: { $in: ?0 } })")
    List<File> findByIds(List<String> ids);
}
