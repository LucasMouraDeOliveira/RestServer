package car.tp2.deprecated;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import car.tp2.utility.Connexion;

/**
 * Client FTP qui communique avec le serveur FTP pour réaliser les diverses opérations sur les fichiers
 * 
 * @author Lucas Moura de Oliveira
 */
@Deprecated
public class FtpClientDeprecated {
	
	protected Socket FTPCommandSocket;
	
	protected Socket FtpDataSocket;
	
	protected BufferedReader reader;
	
	protected PrintWriter writer;
	
	protected int port;
	
	/**
	 * Initialise le client et le connecte au serveur FTP
	 */
	public FtpClientDeprecated(){
		try {
			this.FTPCommandSocket = new Socket("localhost", 2021);
			this.reader = new BufferedReader(new InputStreamReader(this.FTPCommandSocket.getInputStream()));
			this.writer = new PrintWriter(this.FTPCommandSocket.getOutputStream());
			Connexion.read(reader);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connecte l'application au serveur FTP avec un utilisateur par défaut et passe le serveur en mode passif
	 */
	public void connect() {
		Connexion.write(writer, "USER lucas");
		Connexion.read(reader);
		Connexion.write(writer, "PASS l");
		Connexion.read(reader);
		Connexion.write(writer, "EPSV");
		String retour = Connexion.read(reader);
		String[] split = retour.split("\\|");
		this.port = Integer.valueOf(split[3]);
	}
	
	/**
	 * Ferme la socket de commande, son writer et son reader
	 */
	public void close() {
		try {
			this.FTPCommandSocket.close();
			this.writer.close();
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Récupère un fichier sur le serveur FTP
	 * 
	 * @param path le chemin d'accès au fichier
	 * @return un fichier correspondant au chemin d'accès passé en paramètre sur le serveur FTP
	 * @throws IOException s'il y a une erreur de communication quelconque avec le serveur FTP
	 */
	public File get(String path) throws IOException{
		String fileName = path.substring(path.lastIndexOf("/")+1,path.length());
		Connexion.write(writer, "RETR " + path);
		this.FtpDataSocket = new Socket("localhost", port);
		DataInputStream readerData = new DataInputStream(this.FtpDataSocket.getInputStream());
		//Début
		Connexion.read(reader);
		//Message
		DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(fileName)));
		byte[] bytes;
		while((bytes = Connexion.readBinary(readerData)) != null){
			writer.write(bytes);
		}
		writer.close();
		//Fin
		Connexion.read(reader);
		return new File(fileName);
	}
	
	/**
	 * Liste les noms des fichiers contenus dans le dossier passé en paramètre
	 * @param path le nom du dossier
	 * @return une liste de noms de fichiers contenus dans le dossier
	 * @throws IOException s'il y a une erreur de communication quelconque avec le serveur FTP
	 */
	public List<String> listFiles(String path) throws IOException {
		Connexion.write(writer, "CWD " + path);
		Connexion.read(reader);
		Connexion.write(writer, "LIST");
		this.FtpDataSocket = new Socket("localhost", port);
		BufferedReader readerData = new BufferedReader(new InputStreamReader(this.FtpDataSocket.getInputStream()));
		//Début
		Connexion.read(reader);
		//Message
		List<String> files = new ArrayList<String>();
		String file;
		while((file = Connexion.read(readerData)) != null){
			files.add(file);
		}
		writer.close();
		//Fin
		Connexion.read(reader);
		return files;
	}
	

}
