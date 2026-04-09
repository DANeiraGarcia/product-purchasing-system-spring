package co.edu.cesde.pps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PpsApplication {

    public static void main(String[] args) {

    DotenDevelopmentLoader.load();
    SpringApplication.run(PpsApplication.class,args) ;//requiere configuracion de variables de entorno
                                                      //directorio sera subido 09 abril
    }
}