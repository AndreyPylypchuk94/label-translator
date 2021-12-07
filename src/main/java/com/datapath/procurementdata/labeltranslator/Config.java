package com.datapath.procurementdata.labeltranslator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }
}
