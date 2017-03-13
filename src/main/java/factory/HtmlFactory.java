package factory;

import java.util.List;

/**
 * genere l'html transmit à notre navigateur prefere
 * @author brico
 *
 */
public class HtmlFactory {
	
	private String token;
	
	public HtmlFactory() {}
	
	public HtmlFactory(String token){
		this.token = "?token="+token;
	}
	
	/**
	 * Html qui list les fichiers 
	 * @param path où se trouve le dossier courant sur le serveur ftp (pour les autres requetes)
	 * @param list des noms/type/etc des fichier du dossier courant
	 * @return html
	 */
	public String buildList(String path, List<String> files){
		
		path = suppLastSlash(path);
		String html = "<html>\n"
					+ "<script type=\"text/javascript\" src=\"/js/jquery-3.1.1.min.js\"></script>\n"
					+ "<script type=\"text/javascript\" src=\"/js/javascript.js\"></script>\n"
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
					html+="<li id=\""+i+"\"><a href='/file/download"+this.token+"&path="+(path.isEmpty()?"":path+"/")+filename+"'>"+filename+"</a> "+deletefile(filename)+renamefilebtn(i)+"</li>\n";
					html+=renamefile(path,filename,i);
				} else {
					html+="<li  id=\""+i+"\">D: <a href='/folder"+this.token+"&path="+(path.isEmpty()?"":path+"/")+filename+"'>"+filename+"</a>"+deletefile(filename)+renamefilebtn(i)+downloadDirbtn(path,filename)+"</li>\n";
					html+=renamefile(path,filename,i);
				}
			}
		}
		
		if(!path.isEmpty()){
			int index = path.lastIndexOf("/");
			if(index == -1)
				index = 0;
			String retour = path.substring(0, index);
			html+="</br><li><a href='/folder"+this.token+"&path="+retour+"'> retour</a></li>\n";
		}
		html+="</ul>\n"
			+ "</br></br></br>\n"
			+ createdir() + "\n"
			+ "</br></br></br>\n"
			+ uploadfile()+"\n"
		    + "</body>\n</html>\n";

		return html;
	}
	
	/**
	 * Supprime le dernier si il exists / du path (dossier/ -> dossier)
	 * @param String
	 * @return String
	 */
	private String suppLastSlash(String path) {
		if(path.length() > 0 && path.endsWith("/")){
			return path.substring(0, path.length()-1);
		}
		return path;
	}

	/**
	 * Formulaire pour renomme un fichier
	 * @param path dossier courant
	 * @param filename nom du fichier
	 * @param i numero de ligne ou il est afficher (pour le js)
	 * @return html
	 */
	public String renamefile(String path,String filename,int i){
		return "<li style=\" display : none\" id=\"rename"+i+"\"><form class=\"renamefile\">"
				+ "<input type=\"hidden\" name=\"from\" id=\"from\" value=\""+(path.isEmpty()?"":path+"/")+filename+"\" >"
				+ "<input type=\"text\" name=\"to\" id=\"to\" value=\""+(path.isEmpty()?"":path+"/")+filename+"\" >"
				+ "<input type=\"submit\" >"
				+ "</form></li>";
	}
	
	/**
	 * Formulaire d'upload
	 * @return html
	 */
	public String uploadfile(){
		return "<form id=\"postfile\">"
				+ "<input type=\"file\" name=\"file\" id=\"file\">"
				+ "<input type=\"submit\" >"
				+ "</form>";
	}
	
	/**
	 * Formulaire de creation de dossier
	 * @return html
	 */
	public String createdir(){
		return "<form id=\"createfile\">"
				+ "<input type=\"text\" name=\"mkdir\" id=\"mkdir\">"
				+ "<input type=\"submit\" >"
				+ "</form>";
	}
	
	/**
	 * Bouton pour supprimer le fichier ou dossier
	 * @param filename du fichier
	 * @return html
	 */
	public String deletefile(String filename){
		String html = "<button onclick=\"supprime('"+filename+"')\">supprimer</button>";
		return html;
	}
	
	/**
	 * Bouton pour renommer le fichier ou dossier
	 * @param numero de la ligne ou il est afficher (pour le js)
	 * @return html
	 */
	public String renamefilebtn(int id){
		String html = "<button onclick=\"showrename('"+id+"')\">renommer</button>";
		return html;
	}
	
	/**
	 * Bouton pour telecharger un dossier uniquement
	 * @param path du dossier courant
	 * @param filename du dossier
	 * @return html
	 */
	public String downloadDirbtn(String path,String filename){
		String html = "<input value='telecharger' type='button' onclick=\"window.location='/folder/download/"+this.token+"&path="+(path.isEmpty()?"":path+"/")+filename+"'\"/>";
		return html;
	}
	
	/**
	 * Html pour pouvoir se connecter
	 * @return html
	 */
	public String buildFormUser(){
		String html = "<html>\n"
				+ "<script type=\"text/javascript\" src=\"/js/jquery-3.1.1.min.js\"></script>\n"
				+ "<script type=\"text/javascript\" src=\"/js/javascript.js\"></script>\n"
				+ "<body>\n"
		
				+ "<form id=\"connectuser\">"
				+ "Utilisateur <input type=\"text\" name=\"user\" id=\"user\"></br>"
				+ "Mot de passe <input type=\"password\" name=\"mdp\" id=\"mdp\"></br>"
				+ "<input type=\"submit\" >"
				+ "</form>"
		
	    		+ "</body>\n</html>\n";
		return html;
	}
}
