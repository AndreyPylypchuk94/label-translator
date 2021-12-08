package com.datapath.procurementdata.labeltranslator.service;

import com.datapath.procurementdata.labeltranslator.domain.TranslateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.*;

import static java.lang.String.join;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Path.of;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
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

        InputStream stream = newInputStream(of(FILE_PATH));
        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        HSSFSheet sheet = workbook.getSheet("Товары");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < physicalNumberOfRows; i++) {
            log.info("Reading {} row from {} rows", i, physicalNumberOfRows);
            HSSFRow row = sheet.getRow(i);
            result.add(row.getCell(1).getStringCellValue());
            result.add(row.getCell(2).getStringCellValue());
        }
        stream.close();
        return result;
    }

    public void write() throws IOException {
        Path file = of(FILE_PATH);
        InputStream stream = newInputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        HSSFSheet sheet = workbook.getSheet("Товары");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < physicalNumberOfRows; i++) {
            log.info("Writing {} row from {} rows", i, physicalNumberOfRows);
            HSSFRow row = sheet.getRow(i);
            put(row, 1, 5, 7);
            put(row, 2, 6, 8);
        }
        stream.close();
        OutputStream outputStream = newOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
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

        HSSFCell uaCell = row.createCell(uaIdx);
        uaCell.setCellValue(join(", ", uaWords));
        HSSFCell enCell = row.createCell(enIdx);
        enCell.setCellValue(join(", ", enWords));
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
