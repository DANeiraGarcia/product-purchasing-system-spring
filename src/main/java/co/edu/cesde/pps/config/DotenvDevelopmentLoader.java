package co.edu.cesde.pps.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvDevelopmentLoader {

    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));
    }
}
