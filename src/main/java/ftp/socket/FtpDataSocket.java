package ftp.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import factory.FtpFactory;

/**
 * Classe gérant la communication avec le serveur FTP via sa socket de données.
 * La classe définit des flux d'entrées et de sorties et implémente les méthodes de lecture et écriture dans ces flux.
 * 
 * @author Lucas Moura de Oliveira
 *
 */
public class FtpDataSocket extends FtpSocket {
	
	protected BufferedReader readerJustePourLaFonctionListe;
	
	protected InputStream reader;
	
	protected OutputStream writer;

	public FtpDataSocket(FtpFactory ftpFactory) {
		super(ftpFactory);
	}
	
	@Override
	public void openReaders() throws IOException {
		this.reader = this.ftpFactory.buildDataSocketReader(this.socket);
		this.readerJustePourLaFonctionListe = this.ftpFactory.buildCommandSocketReader(this.socket);
		this.writer = this.ftpFactory.buildDataSocketWriter(this.socket);
	}
	
	@Override
	public void closeReaders() {
		try{
			this.reader.close();
			this.readerJustePourLaFonctionListe.close();
			this.writer.close();
		} catch(IOException e){/*pas de probl�me*/}
	}
	
	@Override
	public String readLine() throws IOException{
		return this.readerJustePourLaFonctionListe.readLine();
	}

	/**
	 * Lit les données dans le reader de la socket et les écrit dans un fichier
	 * 
	 * @param fileName le nom du fichier
	 * 
	 * @return un fichier contenant les donn�es lues dans le reader
	 * 
	 */
	public File readFileFromReader(String fileName) {
		File tmpFile = createTemporaryFile(fileName);
		OutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		byte[] data;
		while((data = readDataByte(this.reader)) != null){
			if(!writeDataInFile(fileStream, data))
				return null;
		}
		try {
			fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}
	
	/**
	 * Ecrit un tableau de byte dans un fichier
	 * 
	 * @param fileStream le writer du fichier
	 * @param data les donn�es � �crire
	 * 
	 * @return vrai si l'�criture a r�ussi, faux sinon
	 */
	private boolean writeDataInFile(OutputStream fileStream, byte[] data) {
		try {
			fileStream.write(data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Cr�e un fichier temporaire dans un dossier tmp � la racine du serveur
	 * 
	 * @param fileName le nom du fichier � cr�er
	 * 
	 * @return le fichier cr�e
	 */
	private File createTemporaryFile(String fileName) {
		File tmpFolder = new File("tmp");
		if(fileName.lastIndexOf("/") != -1)
			tmpFolder = new File("tmp/" +fileName.substring(0,fileName.lastIndexOf("/")));
		
		if(!tmpFolder.exists()){
			tmpFolder.mkdirs();
		}
		return new File("tmp/"+fileName);
	}

	private byte[] readDataByte(InputStream readerData) {
		byte[] data = new byte[4096];
		int numread = 0;
		try {
			numread = readerData.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (numread <= 0)
			return null;
		else if (numread == data.length)
			return data;
		else
			return Arrays.copyOf(data, numread);
	}

	public boolean writeDataInWriter(InputStream inputStream) {
		DataInputStream inputStreamReader = new DataInputStream(inputStream);
		try {
			byte[] data;
			while ((data = readDataByte(inputStreamReader)) != null) {
				this.writer.write(data);
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				inputStreamReader.close();
			} catch (IOException e) { /*pas trop grave */}
		}
	}
}
