package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import factory.HtmlFactory;
import ftp.FtpClient;
import user.User;
import user.UserManagment;

@RestController
@RequestMapping("/folder")
public class FolderController {
	
	@RequestMapping(params={"path", "token"}, method=RequestMethod.GET)
	@ResponseBody 
	public String listFiles(@RequestParam(value="path") String path, @RequestParam(value="token") String token) {
		User user = UserManagment.getInstance().getUser(token);
		if(user == null) {
			return "grosse erreur";
		}
		// Traitement
		FtpClient client = null;
		client = new FtpClient(user);
		List<String> files = client.list(path);
		boolean success = (files != null);
		client.close();
		if(success){
			String html = new HtmlFactory(token).buildList(path,files);
			return html;
		} else {
			return "grosse erreur";
		}
	}
	
	@RequestMapping(value="/mkdir", params={"path", "token"}, method=RequestMethod.POST)
	public String createDirectory(@RequestParam("path") String path,@RequestParam(value="token") String token){
		// Vérification connexion
		User user = UserManagment.getInstance().getUser(token);
		if(user == null){
			return "user not connect";
		}
		// Traitement
		FtpClient client = null;
		client = new FtpClient(user);
		boolean success = client.mkdir(path);
		client.close();
		if(success){
			return "ok";
		} else {
			return "bug upload";
		}
	}
	
	@RequestMapping(value = "/download", params = { "path", "token" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadFolder(HttpServletResponse response, @RequestParam(value = "path") String path, @RequestParam(value = "token") String token) {
		// Vérification connexion
		User user = UserManagment.getInstance().getUser(token);
		if (user == null) {
			return;
		}
		// Traitement
		FtpClient client = null;
		client = new FtpClient(user);
		File file = null;
		file = client.downloadFolder(path);
		client.close();
		if (file == null) {
			return;
		} else {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
			response.setContentLength((int) file.length());
			InputStream inputStream;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (IOException e) {
				/* on ne retourne rien */}
		}
	}

}
