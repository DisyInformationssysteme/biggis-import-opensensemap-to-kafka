package net.disy.biggis.opensensemap.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("net.disy.biggis.opensensemap")
public class OpensensemapImportApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpensensemapImportApplication.class, args);
  }

}
