package com.datapath.procurementdata.labeltranslator.service;

import com.datapath.procurementdata.labeltranslator.domain.TranslateData;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static java.lang.String.join;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Path.of;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class DataProvider {

    @Value("${input.file.path}")
    private String FILE_PATH;

    private final DataTokenizer tokenizer;
    private final StorageProvider storageProvider;

    private final Map<String, TranslateData> translateDataByRuValue = new HashMap<>();

    public Set<String> read() throws IOException {
        Set<String> result = new HashSet<>();

        HSSFWorkbook workbook = new HSSFWorkbook(newInputStream(of(FILE_PATH)));
        HSSFSheet sheet = workbook.getSheet("Товары");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < physicalNumberOfRows; i++) {
            HSSFRow row = sheet.getRow(i);
            result.add(row.getCell(1).getStringCellValue());
            result.add(row.getCell(2).getStringCellValue());
        }
        return result;
    }

    public void write() throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(newInputStream(of(FILE_PATH)));
        HSSFSheet sheet = workbook.getSheet("Товары");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < physicalNumberOfRows; i++) {
            HSSFRow row = sheet.getRow(i);
            put(row, 1, 5, 7);
            put(row, 2, 6, 8);
        }
    }

    private void put(HSSFRow row, int sourceIdx, int uaIdx, int enIdx) {
        String text = row.getCell(sourceIdx).getStringCellValue();
        List<String> words = tokenizer.tokenize(text);

        List<String> uaWords = new LinkedList<>();
        List<String> enWords = new LinkedList<>();

        words.forEach(w -> {
            TranslateData wordTranslate = get(w);

            if (isNull(wordTranslate)) {
                uaWords.add(w);
                enWords.add(w);
            } else {
                uaWords.add(wordTranslate.getUa() == null ? w : wordTranslate.getUa());
                enWords.add(wordTranslate.getEn() == null ? w : wordTranslate.getEn());
            }
        });

        row.getCell(uaIdx).setCellValue(join(", ", uaWords));
        row.getCell(enIdx).setCellValue(join(", ", enWords));
    }

    private TranslateData get(String w) {
        TranslateData wordTranslate = translateDataByRuValue.get(w);

        if (isNull(wordTranslate)) {
            wordTranslate = storageProvider.findById(w);
            if (nonNull(wordTranslate)) {
                translateDataByRuValue.put(w, wordTranslate);
            }
        }

        return wordTranslate;
    }
}
