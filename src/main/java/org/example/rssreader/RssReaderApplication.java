package org.example.rssreader;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.example.rssreader.config.AppInitializer;

import java.io.File;

public class RssReaderApplication {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        tomcat.setPort(PORT);
        tomcat.getConnector();

        File baseDir = new File("target/tomcat");
        File docBase = new File("src/main/webapp");

        if (!docBase.exists()) {
            docBase.mkdirs();
        }

        tomcat.setBaseDir(baseDir.getAbsolutePath());

        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        context.addServletContainerInitializer(
                (classes, servletContext) -> new AppInitializer().onStartup(servletContext),
                null
        );

        tomcat.start();

        System.out.println("RSS Reader started: http://localhost:" + PORT);

        tomcat.getServer().await();
    }
}