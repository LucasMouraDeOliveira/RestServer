package car.tp2.factory;

import java.util.List;

public class HtmlFactory {
	
	private String token;
	
	public HtmlFactory() {}
	
	public HtmlFactory(String token){
		this.token = "?token="+token;
	}
	
	public String buildList(String path, List<String> files){
		
		path = suppLastSlash(path);
		String html = "<html>\n"
					+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/jquery-3.1.1.min.js\"></script>\n"
					+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/javascript.js\"></script>\n"
					+ "<body>\n"
					+ "<input type=\"hidden\" value=\""+this.token+"\" id=\"token\">\n"
					+ "<input type=\"hidden\" value=\""+path+"\" id=\"path\">\n"
					+ "<ul>\n";
		int i = 0;
		for(String file : files){
			i++;
			int index = file.indexOf("type=");
			String filename = file;
			String type = "file";
			if(index != -1){
				type = file.substring(index+5,file.indexOf(";",index));
				filename = file.split("; ")[file.split("; ").length-1];
			}
			
			if(!file.isEmpty()){
				if(type.equals("file")){
					html+="<li id=\""+i+"\"><a href='/rest/tp2/file/"+path+"/"+filename+this.token+"'>"+filename+"</a> "+deletefile(filename)+renamefilebtn(i)+"</li>\n";
					html+=renamefile(path,filename,i);
				} else {
					html+="<li  id=\""+i+"\">D: <a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+filename+this.token+"'>"+filename+"</a>"+deletefile(filename)+renamefilebtn(i)+"</li>\n";
					html+=renamefile(path,filename,i);
				}
			}
		}
		
		if(!path.isEmpty()){
			html+="</br><li><a href='/rest/tp2/folder/"+(path.isEmpty()?"":path+"/")+".."+this.token+"'> retour</a></li>\n";
		}
		html+="</ul>\n"
			+ "</br></br></br>\n"
			+ createfile() + "\n"
			+ "</br></br></br>\n"
			+ uploadfile()+"\n"
		    + "</body>\n</html>\n";

		return html;
	}
	
	private String suppLastSlash(String path) {
		if(path.length() > 0 && path.endsWith("/")){
			return path.substring(0, path.length()-1);
		}
		return path;
	}

	public String renamefile(String path,String filename,int i){
		return "<li style=\" display : none\" id=\"rename"+i+"\"><form class=\"renamefile\">"
				+ "<input type=\"hidden\" name=\"from\" id=\"from\" value=\""+(path.isEmpty()?"":path+"/")+filename+"\" >"
				+ "<input type=\"text\" name=\"to\" id=\"to\" value=\""+(path.isEmpty()?"":path+"/")+filename+"\" >"
				+ "<input type=\"submit\" >"
				+ "</form></li>";
	}
	
	public String uploadfile(){
		return "<form id=\"postfile\">"
				+ "<input type=\"file\" name=\"file\" id=\"file\">"
				+ "<input type=\"submit\" >"
				+ "</form>";
	}
	
	public String createfile(){
		return "<form id=\"createfile\">"
				+ "<input type=\"text\" name=\"mkdir\" id=\"mkdir\">"
				+ "<input type=\"submit\" >"
				+ "</form>";
	}
	
	public String deletefile(String filename){
		String html = "<button onclick=\"supprime('"+filename+"')\">supprimer</button>";
		return html;
	}
	
	public String renamefilebtn(int id){
		String html = "<button onclick=\"showrename('"+id+"')\">renommer</button>";
		return html;
	}
	
	public String buildFormUser(){
		String html = "<html>\n"
				+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/jquery-3.1.1.min.js\"></script>\n"
				+ "<script type=\"text/javascript\" src=\"/rest/tp2/js/javascript.js\"></script>\n"
				+ "<body>\n"
		
				+ "<form id=\"connectuser\">"
				+ "Utilisateur <input type=\"text\" name=\"user\" id=\"user\"></br>"
				+ "Mot de passe <input type=\"text\" name=\"mdp\" id=\"mdp\"></br>"
				+ "<input type=\"submit\" >"
				+ "</form>"
		
	    		+ "</body>\n</html>\n";
		return html;
	}
}
