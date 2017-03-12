package car.tp2.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.socket.FtpCommandSocket;
import car.tp2.utility.HtmlFactory;


@Path("/folder")
public class FtpFolderResource {

	@GET
	@Path("/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response listFiles(@PathParam("path") String path,@QueryParam("token") String token) {
		System.out.println("Path = " + path);
		try {
			FtpClient client = new FtpClient(token);
			List<String> files = client.list(path);
			String html = new HtmlFactory(token).buildList(path,files);
			client.close();
			return Response.ok(html).build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
