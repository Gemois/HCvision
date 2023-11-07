package com.hcvision.hcvisionserver.dataset;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetUtils {

    public boolean isValidFileFormat(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase() : "";

            if (fileExtension.equals("csv")) {
                try {
                    CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT);
                    return csvParser.getRecords().size() > 1;
                } catch (IOException e) {
                    return false;
                }
            } else if (fileExtension.equals("xlsx")) {
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


    public String getNumericColumns(String filePath) {
        List<String> numericColumnNames = new ArrayList<>();

        if (filePath.endsWith(".csv")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String headerLine = reader.readLine();
                if (headerLine != null) {
                    String[] headers = headerLine.split(",");
                    String[] values = reader.readLine().split(",");
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        boolean isNumeric = isNumeric(values[i]);
                        if (isNumeric) {
                            numericColumnNames.add(removeDoubleQuotes(headers[i]));
                        }
                    }
                }
            } catch (IOException ignored) {
            }

        } else if (filePath.endsWith(".xlsx")) {

            try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                DataFormatter dataFormatter = new DataFormatter();
                Row headerRow = sheet.getRow(0);
                Row firstDataRow = sheet.getRow(1);

                for (int i = 0; i < headerRow.getLastCellNum() && i < firstDataRow.getLastCellNum(); i++) {
                    String cellValue = dataFormatter.formatCellValue(firstDataRow.getCell(i));
                    if (isNumeric(cellValue)) {
                        numericColumnNames.add(removeDoubleQuotes(dataFormatter.formatCellValue(headerRow.getCell(i))));
                    }
                }
            } catch (IOException ignored) {
            }
        }

        return String.join(",", numericColumnNames);
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String removeDoubleQuotes(String str) {
        return str.replaceAll("\"", "");
    }

}