package com.datapath.procurementdata.labeltranslator.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toSet;

@Service
public class DataTokenizer {

    private final static Pattern PATTERN = compile("[А-Яа-я]");

    public Set<String> tokenize(Set<String> data) {
        return data.stream()
                .flatMap(d -> stream(d.split(", ")))
                .filter(w -> PATTERN.matcher(w).find())
                .filter(w -> !w.matches("^[А-Я]+$"))
                .map(String::trim)
                .collect(toSet());
    }

    public List<String> tokenize(String text) {
        return asList(text.split(", "));
    }
}
