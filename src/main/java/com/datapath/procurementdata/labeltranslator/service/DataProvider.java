package com.datapath.procurementdata.labeltranslator.service;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Path.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProvider {

    @Value("${input.file.path}")
    private String FILE_PATH;

    public Set<String> read() throws IOException {
        Set<String> result = new HashSet<>();

        InputStream stream = newInputStream(of(FILE_PATH));
        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        HSSFSheet sheet = workbook.getSheet("input");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 0; i < physicalNumberOfRows; i++) {
            log.info("Reading {} row from {} rows", i, physicalNumberOfRows);
            HSSFRow row = sheet.getRow(i);
            result.add(row.getCell(0).getStringCellValue().trim());
        }
        stream.close();
        return result;
    }

    public void write(Map<String, String> translateResult) throws IOException {
        Path file = of(FILE_PATH);
        InputStream stream = newInputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        HSSFSheet sheet = workbook.getSheet("input");
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        for (int i = 0; i < physicalNumberOfRows; i++) {
            log.info("Writing {} row from {} rows", i, physicalNumberOfRows);
            HSSFRow row = sheet.getRow(i);
            String text = row.getCell(0).getStringCellValue().trim();
            String translateText = translateResult.getOrDefault(text, text);
            HSSFCell resultCell = row.createCell(1);
            resultCell.setCellValue(translateText);
        }
        stream.close();
        OutputStream outputStream = newOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
    }
}
