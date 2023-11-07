package com.hcvision.hcvisionserver.dataset;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class DatasetValidator {

    public boolean isValidFileFormat(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase() : "";

            if (fileExtension.equals("csv")) {
                try {
                    CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT);
                    return !csvParser.getRecords().isEmpty();
                } catch (IOException e) {
                    return false;
                }
            } else if (fileExtension.equals("xls") || fileExtension.equals("xlsx")) {
                try {
                    Workbook workbook = WorkbookFactory.create(inputStream);
                    return workbook != null;
                } catch (IOException e) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}