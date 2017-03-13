package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import factory.FtpFactory;
import factory.HtmlFactory;
import ftp.FtpClient;
import ftp.socket.FtpCommandSocket;
import user.UserManagment;
import utility.FtpConfig;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String getUserForm() {
		HtmlFactory htmlFactory = new HtmlFactory();
		return htmlFactory.buildFormUser();
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.POST)
	public String connect(WebRequest request) {
		String user = request.getParameter("user");
		String mdp = request.getParameter("mdp");
		FtpClient client = null;
		FtpFactory ftpFactory = new FtpFactory();
		FtpCommandSocket commandSocket = new FtpCommandSocket(ftpFactory);
		FtpConfig ftpConfig = new FtpConfig();
		client = new FtpClient(commandSocket, ftpFactory, ftpConfig);
		if(client.openSocket(ftpConfig.getCommandAddress(), ftpConfig.getCommandPort())){
			if(client.connect(user, mdp)){
				client.close();
				return UserManagment.getInstance().addUser(user, mdp);
			}
		}
		return null;
	}
	

}
