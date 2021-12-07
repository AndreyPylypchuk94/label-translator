package com.datapath.procurementdata.labeltranslator.service;

import com.datapath.procurementdata.labeltranslator.domain.TranslateData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.google.cloud.translate.Translate.TranslateOption.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class TranslateService {

    private final StorageProvider storageProvider;
    private final Translate translate;

    public TranslateService(@Value("${google.cred.path}") String credPath, StorageProvider storageProvider) throws IOException {
        this.storageProvider = storageProvider;

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credPath))
                .createScoped(newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        this.translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public void translate() throws JsonProcessingException {
        List<TranslateData> notTranslated;
        do {
            notTranslated = storageProvider.getNotTranslated(100);
            if (isEmpty(notTranslated)) break;
            notTranslated.forEach(w -> {
                log.info("Translating '{}' word", w.getRu());
                Translation translationUA = translate.translate(w.getRu(), sourceLanguage("ru"), targetLanguage("uk"), model("base"));
                Translation translationEN = translate.translate(w.getRu(), sourceLanguage("ru"), targetLanguage("en"), model("base"));
                w.setUa(translationUA.getTranslatedText());
                w.setEn(translationEN.getTranslatedText());
                w.setTranslated(true);
                storageProvider.save(w);
            });
        } while (!isEmpty(notTranslated));
    }
}
