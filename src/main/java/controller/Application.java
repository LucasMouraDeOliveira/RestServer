package controller;

import java.io.File;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	File tmp = new File("tmp");
		if(tmp.exists())
			recursiveDelete(tmp);
    	SpringApplication.run(Application.class, args);
    }
    
    /**
     * Pour supprimer le dossier tmp à chaque lancement de l'appli
     * @param parent ( dossier à supprimer )
     * @return si le delete a marché
     */
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

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }

}