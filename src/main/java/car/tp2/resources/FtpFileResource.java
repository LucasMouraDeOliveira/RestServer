package car.tp2.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import car.tp2.ftp.FtpClient;
import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.socket.FtpCommandSocket;

@Path("/file")
public class FtpFileResource {
	
	@GET
	@Path("/{path : .*}")
	@Produces("application/octet-stream")
	public Response getFile(@PathParam("path") String path) {
		try {
			FtpClient client = new FtpClient();
			File file = null;
			file = client.download(path);
			if(file == null){
				return Response.status(Response.Status.NOT_FOUND).build();
			} else {
				return Response
						.ok(file, MediaType.APPLICATION_OCTET_STREAM)
						.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
						.build();
			}
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postFile(MultipartBody body){
		for(Attachment attachment : body.getAllAttachments()){
			try {
				InputStream inputStream = attachment.getDataHandler().getInputStream();
				String fileName = attachment.getDataHandler().getName();
				
			} catch (IOException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok("bien re�u").build();
	}
	
	@PUT
	@Path("/rename/{to : [^/]*}/{from : .*}")
	public Response renameFile(@PathParam("to") String to,@PathParam("from") String from){
		FtpClient client;
		try {
			client = new FtpClient();
			client.rename(from,to);
			return Response.ok().build();
		} catch (IOException | FtpException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@DELETE
	@Path("/{path : .*}")
	@Produces("application/octet-stream")
	public Response deleteFile(@PathParam("path") String path) {
		try {
			FtpClient client = new FtpClient();
			client.delete(path);
			return Response.ok().build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
