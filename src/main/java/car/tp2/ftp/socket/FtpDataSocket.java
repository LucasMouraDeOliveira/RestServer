package car.tp2.ftp.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import car.tp2.factory.FtpFactory;
import car.tp2.ftp.FtpException;

public class FtpDataSocket extends FtpSocket {
	
	protected BufferedReader readerJustePourLaFonctionListe;
	
	protected InputStream reader;
	
	protected OutputStream writer;

	public FtpDataSocket(FtpFactory ftpFactory) throws IOException {
		super(ftpFactory);
	}
	
	@Override
	public void openReaders() throws IOException {
		this.reader = this.ftpFactory.buildDataSocketReader(this.socket);
		this.readerJustePourLaFonctionListe = this.ftpFactory.buildCommandSocketReader(this.socket);
		this.writer = this.ftpFactory.buildDataSocketWriter(this.socket);
	}
	
	@Override
	public void closeReaders() throws IOException {
		this.reader.close();
		this.readerJustePourLaFonctionListe.close();
		this.writer.close();
	}
	
	@Override
	public String readLine() throws IOException{
		return this.readerJustePourLaFonctionListe.readLine();
	}

	/**
	 * Lit les donn�es dans le reader de la socket et les �crit dans un fichier
	 * 
	 * @param fileName le nom du fichier
	 * 
	 * @return un fichier contenant les donn�es lues dans le reader
	 * 
	 * @throws FtpException si l'�criture dans le fichier �choue
	 */
	public File readFileFromReader(String fileName) throws FtpException{
		File tmpFile = createTemporaryFile(fileName);
		OutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e) {
			throw new FtpException("Erreur a la cr�ation du writer pour le fichier " + tmpFile.getName());
		}
		byte[] data;
		while((data = readDataByte(this.reader)) != null){
			if(!writeDataInFile(fileStream, data))
				throw new FtpException("Erreur d'�criture dans le fichier " + tmpFile.getName());
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

	public void writeDataInWriter(InputStream inputStream) throws FtpException{
		DataInputStream inputStreamReader = new DataInputStream(inputStream);
		try {
			byte[] data;
			while ((data = readDataByte(inputStreamReader)) != null) {
				this.writer.write(data);
			}
		} catch (IOException e) {
			throw new FtpException("Erreur d'�criture");
		} finally {
			try {
				inputStreamReader.close();
			} catch (IOException e) { /*pas trop grave */}
		}
	}
}
