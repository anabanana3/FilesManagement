package es.uv.adiez.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.uv.adiez.entities.File;
import es.uv.adiez.repositories.FileRepository;

@RestController
@RequestMapping("/files")
public class FileAPI {
	@Autowired
	private FileRepository fileRepo;
	
	@GetMapping
	public List<File> getFiles() {
		return fileRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public File getById(@PathVariable("id") String id) {
		Optional<File> file = fileRepo.findById(id);
		if(file.isEmpty()) return null;
		return file.get();
	}
	
	@GetMapping("/published/{id}")
	public File getPublisehdById(@PathVariable("id") String id) {
		Optional<File> file = fileRepo.findByIdAndStatus(id, File.Status.published);
		if(file.isEmpty()) return null;
		return file.get();
	}
	
	@GetMapping("/keyword/{keyword}")
	public List<File> getByKeyword(@PathVariable("keyword") String keyword) {
		List<File> files = fileRepo.findByKeywordsAndStatus(keyword, File.Status.published);
		return files;
	}
	
	@GetMapping("/status/{status}")
	public List<File> getByStatus(@PathVariable("status") File.Status status) {
		List<File> files = fileRepo.findByStatus(status);
		return files;
	}
	
	@GetMapping("/ids/{ids}")
	public List<File> getByIds(@PathVariable("ids") String ids) {
		List<String> items = Arrays.asList(ids.split("\\s*,\\s*"));
		List<File> files = fileRepo.findByIds(items);
		return files;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public File create(@RequestBody @Valid File file) {
		return fileRepo.save(file);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") String id) {
		fileRepo.deleteById(id);
	}
	
}