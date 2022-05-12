package com.datapath.procurementdata.labeltranslator;

import com.datapath.procurementdata.labeltranslator.service.DocHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@AllArgsConstructor
public class LabelTranslatorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LabelTranslatorApplication.class, args);
    }

    private final DocHandler handler;

    @Override
    public void run(String... args) {
        log.info("Started");

        handler.handle();

        log.info("Finished");
    }
}
