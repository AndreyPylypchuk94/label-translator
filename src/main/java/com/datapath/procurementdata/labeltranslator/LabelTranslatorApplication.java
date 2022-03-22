package com.datapath.procurementdata.labeltranslator;

import com.datapath.procurementdata.labeltranslator.service.DataProvider;
import com.datapath.procurementdata.labeltranslator.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LabelTranslatorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LabelTranslatorApplication.class, args);
    }

    private final DataProvider dataProvider;
    private final TranslateService translateService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Started");

        log.info("Reading...");
        Set<String> data = dataProvider.read();

        log.info("Translating...");
        Map<String, String> translateResult = translateService.translate(data);

        log.info("Writing...");
        dataProvider.write(translateResult);

        log.info("Finished");
    }
}
