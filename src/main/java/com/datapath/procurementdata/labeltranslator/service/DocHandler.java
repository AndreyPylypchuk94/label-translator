package com.datapath.procurementdata.labeltranslator.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class DocHandler {

    private final DbOperationService dbOperationService;
    private final TranslateService translateService;

    public void handle() {
        List<Document> documents;

        while (!isEmpty(documents = dbOperationService.getNotHandled(100))) {
            documents.forEach(d -> {
                Document dn = d.get("Device name", Document.class);
                String en = dn.getString("en");
                log.info("Handling text '{}'", en);
                String pl = translateService.translate(en);
                dn.put("pl", pl);
                dbOperationService.save(d);
            });
        }
    }
}
