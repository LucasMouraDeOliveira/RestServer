package car.tp2.ftp;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import car.tp2.ftp.socket.FtpSocket;

public class FtpSocketTest {
	
	protected FtpSocket ftpSocket;
	
	protected FtpFactory ftpFactory;
	
	protected FtpRequest request;
	
	protected FtpReply reply;
	
	protected BufferedReader reader;
	
	protected PrintWriter writer;
	
	protected Socket socket;
	
	protected SocketAddress address;
	
	protected String commandAddress;
	
	protected int commandPort;
	
	@Before
	public void setUp() throws IOException {
		this.commandAddress = "localhost";
		this.commandPort = 2020;
		this.socket = mock(Socket.class);
		this.ftpFactory =  mock(FtpFactory.class);
		this.reader = mock(BufferedReader.class);
		this.writer = mock(PrintWriter.class);
		this.request = mock(FtpRequest.class);
		this. reply = mock(FtpReply.class);
		this. address = mock(SocketAddress.class);
		when(this.ftpFactory.buildEmptySocket()).thenReturn(this.socket);
		when(this.ftpFactory.buildInetAddress(this.commandAddress, this.commandPort)).thenReturn(this.address);
		when(this.ftpFactory.buildSocketReader(this.socket)).thenReturn(reader);
		when(this.ftpFactory.buildSocketWriter(this.socket)).thenReturn(writer);
		this.ftpSocket = new FtpSocket(this.ftpFactory);
	}

	@Test
	public void testOpenSocket() {
		Assert.assertTrue(this.ftpSocket.openSocket(this.commandAddress, this.commandPort));
	}
	
	@Test
	public void testOpenSocketFailsAddressInvalid() throws IOException {
		doThrow(new IOException()).when(this.socket).connect(address);
		Assert.assertFalse(this.ftpSocket.openSocket(this.commandAddress, this.commandPort));
	}
	
	@Test
	public void testSendUserRequest() throws FtpException, IOException {
		String user = "lucas";
		when(reader.readLine()).thenReturn("331 OK");
		when(request.getText()).thenReturn("USER " + user);
		when(reply.isOk("331")).thenReturn(true);
		this.ftpSocket.openSocket(this.commandAddress, this.commandPort);
		when(this.ftpFactory.buildUserRequest(user)).thenReturn(request);
		when(this.ftpFactory.buildResponse("331 OK")).thenReturn(reply);
		reply = this.ftpSocket.sendAndWaitForReply(this.ftpFactory.buildUserRequest(user));
		Assert.assertTrue(reply.isOk("331"));
	}
	
	@Test
	public void testSendPasswordRequest() throws FtpException, IOException {
		String password = "password";
		when(request.getText()).thenReturn("PASS " + password);
		when(reader.readLine()).thenReturn("230 OK");
		when(reply.isOk("230")).thenReturn(true);
		this.ftpSocket.openSocket(this.commandAddress, this.commandPort);
		when(this.ftpFactory.buildPasswordRequest(password)).thenReturn(request);
		when(this.ftpFactory.buildResponse("230 OK")).thenReturn(reply);
		reply = this.ftpSocket.sendAndWaitForReply(this.ftpFactory.buildPasswordRequest(password));
		Assert.assertTrue(reply.isOk("230"));
	}
}
