package car.tp2.ftp.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;

public class FtpDataSocket extends FtpSocket {
	
	protected DataInputStream reader;

	public FtpDataSocket(FtpFactory ftpFactory) throws IOException {
		super(ftpFactory);
	}
	
	@Override
	public void openReaders() throws IOException {
		this.reader = this.ftpFactory.buildDataSocketReader(this.socket);
	}
	
	@Override
	public void closeReaders() throws IOException {
		this.reader.close();
	}
	
	@Override
	public String readLine() throws IOException{
		return this.reader.readLine();
	}

	public File readDataInReader(String name) throws FtpException {
		DataOutputStream dos = null;
		File tmpFolder = new File("tmp");
		if(!tmpFolder.exists()){
			tmpFolder.mkdirs();
		}
		File f = new File("tmp/"+name);
		try {
			dos = new DataOutputStream(new FileOutputStream(f));
			byte[] data;
			while ((data = readDataByte(this.reader)) != null) {
				dos.write(data);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new FtpException("bug lors de l'ecriture du fichier");
		} finally {
			try {
				dos.close();
			} catch (IOException e) {}
		}
		return f;
	}

	private byte[] readDataByte(DataInputStream readerData) {
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
}
