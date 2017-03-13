package car.tp2.utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import utility.Connexion;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ConnexionTest {

	@Test
	public void testWrite(){
		String msg = "toto";
		PrintWriter writer = mock(PrintWriter.class);
		Connexion.write(writer, msg);
		verify(writer).write(msg+"\r\n");
	}

	@Test
	public void testRead() throws IOException{
		BufferedReader reader = mock(BufferedReader.class);
		when(reader.readLine()).thenReturn("test");
		Assert.assertEquals(Connexion.read(reader),"test");
	}
}
