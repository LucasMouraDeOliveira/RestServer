package car.tp2.ftp;

import java.io.IOException;

import org.junit.Test;

import car.tp2.ftp.FtpException;

public abstract class FtpSocketTest {

	@Test
	public abstract void testOpenSocketSucceeds() throws IOException, FtpException;
	
	@Test
	public abstract void testOpenSocketFails() throws IOException, FtpException;
	
	@Test
	public abstract void testOpenReaders() throws IOException;
	
	@Test
	public abstract void testCloseReaders() throws IOException;
	
	@Test
	public abstract void testCloseSocket() throws IOException, FtpException;

}
