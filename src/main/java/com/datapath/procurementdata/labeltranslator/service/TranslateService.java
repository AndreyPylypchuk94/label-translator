package com.datapath.procurementdata.labeltranslator.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.google.cloud.translate.Translate.TranslateOption.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class TranslateService {

    private final Translate translate;
    private final HashMap<String, String> CACHE;

    public TranslateService(@Value("${google.cred.path}") String credPath) throws IOException {
        CACHE = new HashMap<>();
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credPath))
                .createScoped(newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        this.translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String translate(String source) {
        String target = CACHE.get(source);

        if (nonNull(target)) return target;

        target = translate.translate(source, sourceLanguage("en"), targetLanguage("pl"), model("base"))
                .getTranslatedText();

        CACHE.put(source, target);

        return target;
    }
}
