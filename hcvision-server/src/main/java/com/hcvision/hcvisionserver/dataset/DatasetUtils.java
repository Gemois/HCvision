package com.hcvision.hcvisionserver.dataset;


import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@UtilityClass
public class DatasetUtils {

    public static boolean isValidFileFormat(MultipartFile multipartFile) {
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


    public static String getNumericColumns(String filePath) {
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

    public static String  convertDatasetToJson(String filePath) {

        if (filePath.toLowerCase().endsWith(".csv")) {
            return convertCsvToJson(filePath);
        } else if (filePath.toLowerCase().endsWith(".xlsx")) {
            return convertXlsxToJson(filePath);
        } else {
            return null;
        }
    }

    public static String convertCsvToJson(String csvFilePath) {
        try (FileReader fileReader = new FileReader(csvFilePath);
             StringWriter stringWriter = new StringWriter();
             CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader)) {
            List<String> headers = csvParser.getHeaderNames();
            JSONArray jsonArray = new JSONArray();

            for (org.apache.commons.csv.CSVRecord record : csvParser) {
                JSONObject jsonRow = new JSONObject();
                for (String header : headers) {
                    jsonRow.put(header, record.get(header));
                }
                jsonArray.add(jsonRow);
            }

            jsonArray.writeJSONString(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String convertXlsxToJson(String xlsxFilePath) {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsxFilePath));
             StringWriter stringWriter = new StringWriter()) {
            Sheet sheet = workbook.getSheetAt(0);
            JSONArray jsonArray = new JSONArray();

            Iterator<Row> rowIterator = sheet.iterator();
            String[] headers = null;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                JSONObject jsonRow = new JSONObject();

                if (headers == null) {
                    headers = new String[row.getPhysicalNumberOfCells()];
                    for (int i = 0; i < headers.length; i++) {
                        headers[i] = row.getCell(i).getStringCellValue();
                    }
                } else {
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = row.getCell(i);
                        jsonRow.put(headers[i], getCellValue(cell));
                    }

                    jsonArray.add(jsonRow);
                }
            }

            jsonArray.writeJSONString(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        } else {
            return "";
        }
    }

    public  static boolean areAllElementsInArray(String[] selected, String[] numeric) {

        Set<String> selectedCols = new HashSet<>(Arrays.asList(selected));
        Set<String> numericCols = new HashSet<>(Arrays.asList(numeric));

        return numericCols.containsAll(selectedCols);
    }


    public static void deleteUserDirectory(File userDirectory) {
        File[] allFiles = userDirectory.listFiles();

        if (allFiles != null) {
            for (File file : allFiles) {
                if (file.isDirectory()) {
                    deleteUserDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        userDirectory.delete();
    }

}