package controller;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/js")
public class JsController {
	
	/**
	 * Route pour recupere les fichier js
	 * @return JS FILE
	 */
	@RequestMapping(value="/{path:.+}", method=RequestMethod.GET)
	@ResponseBody 
	public FileSystemResource getJs(@PathVariable("path") String path) {
		return new FileSystemResource(new File("js/"+path));
	}

}
