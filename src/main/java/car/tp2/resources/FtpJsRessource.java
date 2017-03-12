package car.tp2.resources;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.utility.HtmlFactory;

@Path("/js")
public class FtpJsRessource {
	
	@GET
	@Path("/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response listFiles(@PathParam("path") String path) {
		File file = new File("js/"+path);
		return Response
				.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
				.build();
	}
}
