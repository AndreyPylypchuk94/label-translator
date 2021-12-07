package com.datapath.procurementdata.labeltranslator.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateData {

    @JsonProperty("_id")
    private String ru;
    private String ua;
    private String en;
    private boolean translated;

    public TranslateData(String ru) {
        this.ru = ru;
    }
}
