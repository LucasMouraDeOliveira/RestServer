package car.tp2;

import java.io.File;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * La classe principale de l'application.
 * Demarre un serveur sur le port 8080.
 * Utilise le prefixe /rest/ pour les URLs.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Starter {
	
	public static void main( final String[] args ) throws Exception {
		File tmp = new File("tmp");
		if(tmp.exists())
			recursiveDelete(tmp);
		
		Server server = new Server( 8080 );
		        
 		final ServletHolder servletHolder = new ServletHolder( new CXFServlet() );
 		final ServletContextHandler context = new ServletContextHandler(); 		
 		context.setContextPath( "/" );
 		context.addServlet( servletHolder, "/rest/*" ); 	
 		context.addEventListener( new ContextLoaderListener() );
 		
 		context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
 		context.setInitParameter( "contextConfigLocation", Config.class.getName() );
 		 		
        server.setHandler( context );
        server.start();
        server.join();	
	}
	
	private static boolean recursiveDelete(File parent) {
		for(File f : parent.listFiles()){
			if(f.isDirectory()){
				recursiveDelete(f);
			}else{
				f.delete();
			}
		}
		return parent.delete();
	}
}
