package car.tp2.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.utility.FtpConfig;
import car.tp2.utility.HtmlFactory;
import car.tp2.utility.User;
import car.tp2.utility.UserManagment;


@Path("/user")
public class FtpUserResource {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getUserFrom() {
		HtmlFactory htmlFactory = new HtmlFactory();
		String html = htmlFactory.buildFormUser();
		return Response.ok(html).build();
	}
	
	@GET
	@Path("/connect")
	@Produces(MediaType.TEXT_HTML)
	public Response getTokenUser(@FormParam("user") String user,@FormParam("mdp") String mdp) {
		FtpClient client = null;
		try {
			FtpFactory ftpFactory = new FtpFactory();
			FtpCommandSocket commandSocket = new FtpCommandSocket(ftpFactory);
			FtpConfig ftpConfig = new FtpConfig();
			client = new FtpClient(commandSocket, ftpFactory, ftpConfig);
			client.openSocket(ftpConfig.getCommandAddress(), ftpConfig.getCommandPort());
			client.connect(user, mdp);
			client.close();
			return Response.ok(UserManagment.getInstance().addUser(user, mdp)).build();
		} catch (FtpException | IOException e) {
			try {
				client.close();
			} catch (IOException e1) {
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
