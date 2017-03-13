package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import ftp.FtpClient;
import user.User;
import user.UserManagment;

@RestController
@RequestMapping("/file")
public class FileController {

	@RequestMapping(value = "/download", params = { "path", "token" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void download(HttpServletResponse response, @RequestParam(value = "path") String path, @RequestParam(value = "token") String token) {

		// Vérification connexion
		User user = UserManagment.getInstance().getUser(token);
		if (user == null) {
			return;
		}
		// Traitement
		FtpClient client = null;
		client = new FtpClient(user);
		File file = null;
		file = client.download(path);
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

	@RequestMapping(value="/upload", method = RequestMethod.POST, params = { "path", "token" })
	public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "path") String path, @RequestParam(value = "token") String token) {
		// Vérification connexion
		User user = UserManagment.getInstance().getUser(token);
		if (user == null) {
			return "403";
		}
		try {
			InputStream fileInputStream = file.getInputStream();
			FtpClient client = null;
			client = new FtpClient(user);
			client.upload(fileInputStream, file.getOriginalFilename(), path);
			client.close();
		} catch (IOException e) {
			return "500";
		}
		return "200";
	}

	@RequestMapping(value = "/rename", params = { "token" }, method = RequestMethod.PUT)
	public String renameFile(WebRequest request, @RequestParam("token") String token) {
		// Vérification connexion
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		User user = UserManagment.getInstance().getUser(token);
		if (user == null) {
			return "user no connect";
		}
		FtpClient client = null;
		client = new FtpClient(user);
		boolean success = client.rename(from, to);
		client.close();
		if (success) {
			return "ok";
		} else {
			return "buggg";
		}
	}

	@RequestMapping(params = { "path", "token" }, method = RequestMethod.DELETE)
	public String deleteFile(@RequestParam("path") String path, @RequestParam("token") String token) {
		// Vérification connexion
		System.out.println(path);
		User user = UserManagment.getInstance().getUser(token);
		if (user == null) {
			return "403";
		}
		// Traitement
		FtpClient client = null;
		client = new FtpClient(user);
		boolean success = client.delete(path);
		client.close();
		if (success) {
			return "200";
		} else {
			return "500";
		}
	}
}