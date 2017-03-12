package car.tp2.utility;

import java.util.List;

public class HtmlFactory {
	
	public String buildList(String path, List<String> files){
		System.out.println(path);
		String html = "<html>"
				+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/jquery-3.1.1.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/javascript.js\"></script>"
				+ "<body><ul>";
		for(String file : files){
			int index = file.indexOf("type=");
			String filename = file;
			String type = "file";
			if(index != -1){
				type = file.substring(index+5,file.indexOf(";",index));
				filename = file.split("; ")[file.split("; ").length-1];
			}
			if(path.endsWith("/")){
				path = path.substring(0, path.length()-1);
			}
			
			if(!file.isEmpty()){
				if(type.equals("file")){
					html+="<li><a href='/rest/tp2/file/"+path+"/"+filename+"'>"+filename+"</a></li>";
				} else {
					html+="<li>D: <a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+filename+"'>"+filename+"</a></li>";
				}
			}
		}
		
		if(!path.isEmpty()){
			html+="</br><li><a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+"..'> retour</a></li>";
		}
		html+="</ul></br></br></br>"
			+ "<input type=\"hidden\" value=\""+path+"\" id=\"path\">"
			+ "<form id=\"postfile\">"
			+ "<input type=\"file\" name=\"file\" id=\"file\">"
			+ "<input type=\"submit\" >"
			+ "</form>";
		
		
		html+="</body></html>";

		return html;
	}
	
	public String supprimerfile(String path,String filename){
		String html = "<button href=\""+"http://localhost:8080/rest/tp2/file/"+path+"/"+filename+"\" >";
		
		return html;
		
	}
}
