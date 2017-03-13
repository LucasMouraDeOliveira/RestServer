package car.tp2.ftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import factory.FtpFactory;
import ftp.FtpClient;
import ftp.FtpReply;
import ftp.FtpRequest;
import ftp.socket.FtpCommandSocket;
import utility.FtpConfig;

public class FtpClientTest {

	protected FtpClient ftpClient;
	protected FtpCommandSocket ftpSocket;
	protected FtpFactory ftpFactory;
	protected FtpConfig ftpConfig;
	protected FtpRequest validUserRequest, validPasswordRequest, invalidUserRequest, invalidPasswordRequest;
	protected FtpReply validUserReply, validPasswordReply, invalidUserReply, invalidPasswordReply;
	protected int commandPort, dataPort;
	protected String commandAddress, validUser, validPassword;
	protected String invalidUser, invalidPassword;
	
	@Before
	public void setUp() {
		
		// *************
		// *** MOCKS ***
		// *************
		
		this.ftpSocket = mock(FtpCommandSocket.class);
		this.ftpFactory = mock(FtpFactory.class);
		this.ftpConfig = mock(FtpConfig.class);
		
		// FtpRequest
		this.validUserRequest = mock(FtpRequest.class);
		this.validPasswordRequest = mock(FtpRequest.class);
		this.invalidUserRequest = mock(FtpRequest.class);
		this.invalidPasswordRequest = mock(FtpRequest.class);
		
		// FtpReply
		this.validUserReply = mock(FtpReply.class);
		this.validPasswordReply = mock(FtpReply.class);
		this.invalidUserReply = mock(FtpReply.class);
		this.invalidPasswordReply = mock(FtpReply.class);
		
		// **********************
		// *** Objet � tester ***
		// **********************
		
		this.ftpClient = new FtpClient(this.ftpSocket, this.ftpFactory, this.ftpConfig);
		
		// *************************
		// *** Variables simples ***
		// *************************
		
		// Adresse FTP
		this.commandAddress = "localhost";
		this.commandPort = 2020;
		this.dataPort = 2021;
		
		// Login / Mot de passe
		this.validUser = "lucas";
		this.validPassword = "password";
		this.invalidUser = "INVALID_USER";
		this.invalidPassword = "INVALID_PASSWORD";
		
		// ************
		// *** When ***
		// ************
		
		// FtpRequest
		when(this.ftpFactory.buildUserRequest(this.validUser)).thenReturn(validUserRequest);
		when(this.ftpFactory.buildPasswordRequest(this.validPassword)).thenReturn(validPasswordRequest);		
		when(this.ftpFactory.buildUserRequest(this.invalidUser)).thenReturn(invalidUserRequest);
		when(this.ftpFactory.buildPasswordRequest(this.invalidPassword)).thenReturn(invalidPasswordRequest);		
		
		// FtpReply
		when(this.ftpSocket.sendAndWaitForReply(validUserRequest)).thenReturn(validUserReply);
		when(this.ftpSocket.sendAndWaitForReply(validPasswordRequest)).thenReturn(validPasswordReply);
		when(this.ftpSocket.sendAndWaitForReply(invalidUserRequest)).thenReturn(invalidUserReply);
		when(this.ftpSocket.sendAndWaitForReply(invalidPasswordRequest)).thenReturn(invalidPasswordReply);
		
		// Validit� reply
		when(validUserReply.isOk("331")).thenReturn(true);
		when(validPasswordReply.isOk("230")).thenReturn(true);
		when(invalidUserReply.isOk("331")).thenReturn(false);
		when(invalidPasswordReply.isOk("230")).thenReturn(false);
		
	}

	@Test
	public void testOpenSocketOnCorrectPort() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpConfig.getCommandPort()).thenReturn(2020);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertTrue(this.ftpClient.isSocketOpen());
		Assert.assertEquals(this.commandPort, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test
	public void testOpenSocketFails() {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(false);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test
	public void testOpenSocketFailsMessageIsNull() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn(null);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());

	}
	
	@Test
	public void testOpenSocketFailsMessageIsNot220() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("500 error");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.isSocketOpen());
		Assert.assertEquals(0, this.ftpClient.getConfig().getCommandPort());
	}
	
	@Test
	public void testOpenSocketFailsIOException() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenThrow(new IOException());
		Assert.assertFalse(this.ftpClient.openSocket(this.commandAddress, this.commandPort));
	}
	
	@Test
	public void testConnectClient() throws IOException {
		//setUp
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.validUser, this.validPassword);
		// verify / oracle
		Assert.assertTrue(this.ftpClient.isConnected());
	}
	
	@Test
	public void testConnectClientFailsUserUnknown() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.connect(this.invalidUser, this.validPassword));
	}
	
	@Test
	public void testConnectClientFailsPasswordInvalid() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.connect(this.validUser, this.invalidPassword));
	}
	
	@Test
	public void testSetPassive() throws IOException {
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(setPassiveReply.isOk("229")).thenReturn(true);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||2021|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		this.ftpClient.connect(this.validUser, this.validPassword);
		this.ftpClient.setPassive();
		
		Assert.assertEquals(this.dataPort, this.ftpClient.getDataPort());
	}
	
	@Test
	public void testSetPassiveFailsNotConnected() throws IOException {
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		Assert.assertFalse(this.ftpClient.setPassive());
	}
	
	@Test
	public void testSetPassiveFailsReplyIsNot229() throws IOException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.validUser)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.validPassword)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		when(setPassiveReply.isOk("229")).thenReturn(false);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||2021|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.validUser, this.validPassword);
		this.ftpClient.setPassive();
	}
	
	@Test
	public void testSetPassiveFailsReplyPortIsNotNumeric() throws IOException {
		FtpRequest userRequest = mock(FtpRequest.class);
		FtpRequest passwordRequest = mock(FtpRequest.class);
		FtpRequest setPassiveRequest = mock(FtpRequest.class);
		FtpReply userReply = mock(FtpReply.class);
		FtpReply passwordReply = mock(FtpReply.class);
		FtpReply setPassiveReply = mock(FtpReply.class);
		when(this.ftpSocket.openSocket(this.commandAddress, this.commandPort)).thenReturn(true);
		when(this.ftpSocket.readLine()).thenReturn("220 OK");
		when(this.ftpFactory.buildUserRequest(this.validUser)).thenReturn(userRequest);
		when(this.ftpFactory.buildPasswordRequest(this.validPassword)).thenReturn(passwordRequest);
		when(this.ftpFactory.buildSetPassiveRequest()).thenReturn(setPassiveRequest);
		when(this.ftpSocket.sendAndWaitForReply(userRequest)).thenReturn(userReply);
		when(this.ftpSocket.sendAndWaitForReply(passwordRequest)).thenReturn(passwordReply);
		when(this.ftpSocket.sendAndWaitForReply(setPassiveRequest)).thenReturn(setPassiveReply);
		when(userReply.isOk("331")).thenReturn(true);
		when(passwordReply.isOk("230")).thenReturn(true);
		when(setPassiveReply.isOk("229")).thenReturn(true);
		when(setPassiveReply.getMessage()).thenReturn("Passage en mode passif etendu (|||bad_format|) ");
		when(this.ftpConfig.getDataPort()).thenReturn(2021);
		this.ftpClient.openSocket(this.commandAddress, this.commandPort);
		// function call
		this.ftpClient.connect(this.validUser, this.validPassword);
		this.ftpClient.setPassive();
		// verify / oracle
		Assert.assertEquals(this.dataPort, this.ftpClient.getDataPort());
	}
	
}
