package car.tp2.resources;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import car.tp2.factory.HtmlFactory;
import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.user.User;
import car.tp2.user.UserManagment;


@Path("/folder")
public class FolderResource {
	
	@GET
	@Path("/download/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response downloadFolder(@PathParam("path") String path,@QueryParam("token") String token) {
		FtpClient client = null;
		User user = UserManagment.getInstance().getUser(token);
		user = new User("lucas", "l");
		if(user == null)
			return Response.status(Response.Status.FORBIDDEN).build();
		try {
			System.out.println("download "+path);
			client = new FtpClient(user);
			File file = client.downloadFolder(path);
			return Response
					.ok(file, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
					.build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			client.close();
		}
	}
	
	@GET
	@Path("/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response listFiles(@PathParam("path") String path,@QueryParam("token") String token) {
		FtpClient client = null;
		User user = UserManagment.getInstance().getUser(token);
		if(user == null)
			return Response.status(Response.Status.FORBIDDEN).build();
		try {
			client = new FtpClient(user);
			List<String> files = client.list(path);
			String html = new HtmlFactory(token).buildList(path,files);
			client.close();
			return Response.ok(html).build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			client.close();
		}
	}
}
