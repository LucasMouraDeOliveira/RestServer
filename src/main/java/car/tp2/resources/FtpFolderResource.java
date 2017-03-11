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
			FtpClient client = new FtpClient();
			List<String> files = client.list(path);
			String html = "<html><body><ul>";
			for(String file : files){
				int index = file.indexOf("type=");
				String type = file.substring(index+5,file.indexOf(";",index));
				String filename = file.split("; ")[file.split("; ").length-1];
				if(!file.isEmpty()){
					if(type.equals("file")){
						html+="<li><a href='/rest/tp2/file/"+path+"/"+filename+"'>"+filename+"</a></li>";
					} else {
						html+="<li>D: <a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+filename+"'>"+filename+"</a></li>";
					}
				}
			}
			
			if(!path.isEmpty()){
				html+="</br><li><a href='/rest/tp2/folder/"+path+"/..'> retour</a></li>";
			}
			html+="</ul></body></html>";
			client.close();
			return Response.ok(html).build();
		} catch (FtpException | IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
