package com.datapath.procurementdata.labeltranslator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "label-translation-data")
public class TranslateData {

    @Field("_id")
    private String ru;
    private String ua;
    private String en;
    private String pl;
    private boolean translated;

    public TranslateData(String ru) {
        this.ru = ru;
    }
}
