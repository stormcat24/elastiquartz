package com.github.stormcat24.elastiquartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author stormcat24
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
