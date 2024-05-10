package org.chc.ezim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EzimApplication {

    public static void main(String[] args) {
        SpringApplication.run(EzimApplication.class, args);
    }

}
