package car.tp2.ftp;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import factory.FtpFactory;
import ftp.FtpReply;
import ftp.FtpRequest;
import ftp.socket.FtpCommandSocket;

public class FtpCommandSocketTest extends FtpSocketTest{
	
	protected FtpFactory ftpFactory;
	
	protected Socket socket;
	
	protected SocketAddress address;
	
	protected BufferedReader reader;
	
	protected PrintWriter writer;
	
	protected String commandAddress;
	
	protected int commandPort;
	
	protected FtpRequest request;
	
	protected FtpReply reply;
	
	@Before
	public void setUp() throws IOException {
		this.ftpFactory = mock(FtpFactory.class);
		this.socket = mock(Socket.class);
		this.address = mock(SocketAddress.class);
		this.reader = mock(BufferedReader.class);
		this.writer = mock(PrintWriter.class);
		this.request = mock(FtpRequest.class);
		this.reply = mock(FtpReply.class);
		when(this.ftpFactory.buildEmptySocket()).thenReturn(this.socket);
		when(this.ftpFactory.buildInetAddress(commandAddress, commandPort)).thenReturn(this.address);
		when(this.ftpFactory.buildCommandSocketReader(this.socket)).thenReturn(this.reader);
		when(this.ftpFactory.buildCommandSocketWriter(this.socket)).thenReturn(this.writer);
	}
	
	@Override
	public void testOpenSocketSucceeds() throws IOException {
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		Assert.assertTrue(commandSocket.openSocket(this.commandAddress, this.commandPort));
		verify(this.socket).connect(this.address);
	}

	@Override
	public void testOpenSocketFails() throws IOException {
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		doThrow(new IOException()).when(this.socket).connect(this.address);
		Assert.assertFalse(commandSocket.openSocket(this.commandAddress, this.commandPort));
		verify(this.socket).connect(this.address);
	}

	@Override
	public void testOpenReaders() throws IOException {
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openReaders();
		verify(this.ftpFactory).buildCommandSocketReader(this.socket);
		verify(this.ftpFactory).buildCommandSocketWriter(this.socket);
	}

	@Override
	public void testCloseReaders() throws IOException {
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openReaders();
		commandSocket.closeReaders();
		verify(this.reader).close();
		verify(this.writer).close();
	}

	@Override
	public void testCloseSocket() throws IOException {
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openSocket(this.commandAddress, this.commandPort);
		commandSocket.close();
		verify(this.socket).close();
	}
	
	@Test
	public void readLineSucceeds() throws IOException {
		when(this.reader.readLine()).thenReturn("200 Success");
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openSocket(this.commandAddress, this.commandPort);
		Assert.assertEquals("200 Success", commandSocket.readLine());
		verify(this.reader).readLine();
	}
	
	@Test
	public void testSendRequest() throws IOException{
		when(this.request.getText()).thenReturn("test");
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openSocket(this.commandAddress, this.commandPort);
		commandSocket.send(this.request);
		verify(this.writer).write("test\n");
		verify(this.writer).flush();
	}
	
	@Test
	public void testSendAndWaitForReply() throws IOException {
		when(this.reader.readLine()).thenReturn("200 Success");
		when(this.request.getText()).thenReturn("test");
		when(this.ftpFactory.buildResponse(this.reader.readLine())).thenReturn(this.reply);
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openSocket(this.commandAddress, this.commandPort);
		commandSocket.sendAndWaitForReply(this.request);
		verify(this.writer).write("test\n");
		verify(this.writer).flush();
		verify(this.ftpFactory).buildResponse(this.reader.readLine());
	}
	
	@Test
	public void testSendAndWaitForReplyFails() throws IOException {
		when(this.reader.readLine()).thenThrow(new IOException());
		when(this.request.getText()).thenReturn("test");
		FtpCommandSocket commandSocket = new FtpCommandSocket(this.ftpFactory);
		commandSocket.openSocket(this.commandAddress, this.commandPort);
		commandSocket.sendAndWaitForReply(this.request);
		verify(this.writer).write("test\n");
		verify(this.writer).flush();
	}

}
