package com.rivermeadow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jinloes on 2/3/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Autowired
    public String encryptionKey(ZtrusteeService ztrusteeService,
            @Value("#{systemProperties['enc_key_handle']?:'prod-enc-key'}")
            final String encKeyHandle) {
        return ztrusteeService.getHandleData(encKeyHandle);
    }
}
