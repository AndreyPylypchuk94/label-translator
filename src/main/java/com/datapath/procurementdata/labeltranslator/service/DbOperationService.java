package com.datapath.procurementdata.labeltranslator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbOperationService {

    private static final Query NOT_TRANSLATED_QUERY = new Query(new Criteria().andOperator(
            where("Device name.en").exists(true),
            where("Device name.pl").exists(false)
    ));

    @Value("${collection.name}")
    private String COLLECTION_NAME;

    private final MongoTemplate template;

    public List<Document> getNotHandled(int limit) {
        return template.find(NOT_TRANSLATED_QUERY.limit(limit), Document.class, COLLECTION_NAME);
    }

    public void save(Document doc) {
        template.save(doc, COLLECTION_NAME);
    }
}
