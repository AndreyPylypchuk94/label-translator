package com.datapath.procurementdata.labeltranslator.service;

import com.datapath.procurementdata.labeltranslator.domain.TranslateData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageProvider {

    private static final Query NOT_TRANSLATED_QUERY = new Query(where("translated").is(false));

    @Value("${collection.name}")
    private String COLLECTION_NAME;

    private final MongoTemplate template;
    private final ObjectMapper mapper;

    public void persist(Set<String> words) throws JsonProcessingException {
        int i = 1;
        for (String w : words)
            if (!template.exists(new Query(where("_id").is(w)), COLLECTION_NAME)) {
                log.info("Saving '{}' ({} from {})", w, i, words.size());
                template.save(mapper.writeValueAsString(new TranslateData(w)), COLLECTION_NAME);
                i++;
            }

    }

    public List<TranslateData> getNotTranslated(int limit) throws JsonProcessingException {
        List<TranslateData> result = new ArrayList<>();
        List<String> list = template.find(NOT_TRANSLATED_QUERY.limit(limit), String.class, COLLECTION_NAME);

        for (String src : list) {
            result.add(mapper.readValue(src, TranslateData.class));
        }
        return result;
    }

    public TranslateData findById(String word) {
        return template.findOne(new Query(where("_id").is(word)
                        .andOperator(where("translated").is(true))),
                TranslateData.class,
                COLLECTION_NAME
        );
    }

    @SneakyThrows
    public void save(TranslateData data) {
        template.save(mapper.writeValueAsString(data), COLLECTION_NAME);
    }
}
