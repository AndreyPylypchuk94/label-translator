package com.datapath.procurementdata.labeltranslator.service;

import com.datapath.procurementdata.labeltranslator.domain.TranslateData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageProvider {

    private static final Query NOT_TRANSLATED_QUERY = new Query(where("translated").is(false));

    private final MongoTemplate template;

    public void persist(Set<String> words) {
        int i = 1;
        for (String w : words) {
            log.info("Saving '{}' ({} from {})", w, i, words.size());

            TranslateData data = findById(w);

            if (isNull(data)) {
                data = new TranslateData(w);
            } else {
                data.setTranslated(false);
            }

            save(data);

            i++;
        }
    }

    public List<TranslateData> getNotTranslated(int limit) {
        return template.find(NOT_TRANSLATED_QUERY.limit(limit), TranslateData.class);
    }

    public TranslateData findByIdTranslated(String word) {
        return template.findOne(new Query(where("_id").is(word)
                        .andOperator(where("translated").is(true))),
                TranslateData.class
        );
    }

    public TranslateData findById(String word) {
        return template.findOne(new Query(where("_id").is(word)),
                TranslateData.class
        );
    }

    @SneakyThrows
    public void save(TranslateData data) {
        template.save(data);
    }
}
