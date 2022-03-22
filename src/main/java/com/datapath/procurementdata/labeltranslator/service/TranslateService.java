package com.datapath.procurementdata.labeltranslator.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.cloud.translate.Translate.TranslateOption.*;
import static com.google.common.collect.Lists.newArrayList;

@Slf4j
@Service
public class TranslateService {

    private final Translate translate;

    public TranslateService(@Value("${google.cred.path}") String credPath) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credPath))
                .createScoped(newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        this.translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public Map<String, String> translate(Set<String> data) {
        Map<String, String> result = new HashMap<>();

        data.forEach(d -> {
            log.info("Translating '{}' word", d);
            Translation translate = this.translate.translate(d, sourceLanguage("uk"), targetLanguage("la"), model("base"));
            result.put(d, translate.getTranslatedText());
        });

        return result;
    }
}
