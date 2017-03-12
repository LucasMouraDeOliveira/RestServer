package car.tp2.ftp;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import car.tp2.ftp.FtpException;
import car.tp2.ftp.FtpFactory;
import car.tp2.ftp.socket.FtpDataSocket;

public class FtpDataSocketTest extends FtpSocketTest{
	
	protected FtpFactory ftpFactory;	
	
	protected Socket socket;
	
	protected SocketAddress address;
	
	protected String dataAddress;
	
	protected int dataPort;
	
	protected DataInputStream reader;

	protected BufferedReader reader2;
	
	protected DataOutputStream writer;

	@Before
	public void setUp() throws IOException{
		this.ftpFactory = mock(FtpFactory.class);
		this.socket = mock(Socket.class);
		this.address = mock(SocketAddress.class);
		this.reader = mock(DataInputStream.class);
		this.reader2 = mock(BufferedReader.class);
		this.writer = mock(DataOutputStream.class);
		when(this.ftpFactory.buildEmptySocket()).thenReturn(this.socket);
		when(this.ftpFactory.buildInetAddress(this.dataAddress, this.dataPort)).thenReturn(this.address);
		when(this.ftpFactory.buildDataSocketReader(this.socket)).thenReturn(this.reader);
		when(this.ftpFactory.buildCommandSocketReader(this.socket)).thenReturn(this.reader2);
		when(this.ftpFactory.buildDataSocketWriter(this.socket)).thenReturn(this.writer);
	}

	@Override
	public void testOpenSocketSucceeds() throws IOException, FtpException {
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		Assert.assertTrue(dataSocket.openSocket(this.dataAddress, this.dataPort));
		verify(this.socket).connect(this.address);
	}

	@Override
	public void testOpenSocketFails() throws IOException, FtpException {
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		doThrow(new IOException()).when(this.socket).connect(this.address);
		Assert.assertFalse(dataSocket.openSocket(this.dataAddress, this.dataPort));
		verify(this.socket).connect(this.address);
	}

	@Override
	public void testOpenReaders() throws IOException {
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		dataSocket.openReaders();
		verify(this.ftpFactory).buildDataSocketReader(this.socket);
		verify(this.ftpFactory).buildDataSocketWriter(this.socket);
	}

	@Override
	public void testCloseReaders() throws IOException {
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		dataSocket.openReaders();
		dataSocket.closeReaders();
		verify(this.reader).close();
		verify(this.writer).close();
	}

	@Override
	public void testCloseSocket() throws IOException, FtpException {
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		dataSocket.openSocket(this.dataAddress, this.dataPort);
		dataSocket.close();
		verify(this.socket).close();
	}
	
	@Test
	public void readLine() throws IOException, FtpException {
		when(this.reader2.readLine()).thenReturn("test");
		FtpDataSocket dataSocket = new FtpDataSocket(this.ftpFactory);
		dataSocket.openSocket(this.dataAddress, this.dataPort);
		Assert.assertEquals("test", dataSocket.readLine());
		verify(this.reader2).readLine();
	}

}
