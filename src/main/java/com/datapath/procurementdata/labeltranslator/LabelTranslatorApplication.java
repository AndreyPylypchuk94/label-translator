package com.datapath.procurementdata.labeltranslator;

import com.datapath.procurementdata.labeltranslator.service.DataProvider;
import com.datapath.procurementdata.labeltranslator.service.DataTokenizer;
import com.datapath.procurementdata.labeltranslator.service.StorageProvider;
import com.datapath.procurementdata.labeltranslator.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LabelTranslatorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LabelTranslatorApplication.class, args);
    }

    @Value("${processing.step.read}")
    private boolean read;
    @Value("${processing.step.translate}")
    private boolean translate;
    @Value("${processing.step.write}")
    private boolean write;


    private final DataProvider dataProvider;
    private final DataTokenizer tokenizer;
    private final StorageProvider storageProvider;
    private final TranslateService translateService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Started");

        if (read) {
            log.info("Reading...");
            Set<String> data = dataProvider.read();
            log.info("Tokenizing...");
            Set<String> words = tokenizer.tokenize(data);
            log.info("Persisting...");
            storageProvider.persist(words);
        }

        if (translate) {
            log.info("Translating...");
            translateService.translate();
        }

        if (write) {
            log.info("Writing...");
            dataProvider.write();
        }

        log.info("Finished");
    }
}
