package car.tp2.resources;

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
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.socket.FtpCommandSocket;


@Path("/folder")
public class FtpFolderResource {

	@GET
	@Path("/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response listFiles(@PathParam("path") String path) {
		System.out.println("Path = " + path);
		try {
			FtpFactory ftpFactory = new FtpFactory();
			FtpCommandSocket commandSocket = new FtpCommandSocket(ftpFactory);
			FtpClient client = new FtpClient(commandSocket, ftpFactory);
			client.openSocket("localhost", 2021);
			client.connect("lucas", "l");
			client.setPassive();
			List<String> files = client.list(path);
			String html = "<html><body><ul>";
			for(String file : files){
				if(!file.isEmpty()){
					//TODO attendre le refactoring de la commande LIST pour différencier proprement les fichiers des dossiers
					if(file.contains(".")){
						html+="<li><a href='/rest/tp2/file/"+path+"/"+file+"'>"+file+"</a></li>";
					} else {
						html+="<li><a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+file+"'>"+file+"</a></li>";
					}
				}
			}
			
			if(!path.isEmpty()){
				html+="<li><a href='/rest/tp2/folder/"+path+"/..'>..</a></li>";
			}
			html+="</ul></body></html>";
			return Response.ok(html).build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
