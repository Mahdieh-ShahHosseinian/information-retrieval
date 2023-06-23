package com.example.informationretrieval.practice_1.inverted_index;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Practice-1
 * <a href="https://www.baeldung.com/java-microsoft-excel">https://www.baeldung.com/java-microsoft-excel</a>
 */
public class ExcelWorker {
    private static final String XML_FILE_LOCATION = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\CHAT DATASET.xlsx";
    private final Workbook workbook;

    public ExcelWorker(File file) {
        try {
            this.workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, List<String>> readFile() {
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 1;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING -> data.get(i).add(cell.getRichStringCellValue().getString());
                    case NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                    }
                    case BOOLEAN -> data.get(i).add(cell.getBooleanCellValue() + "");
                    case FORMULA -> data.get(i).add(cell.getCellFormula() + "");
                    default -> data.get(i).add("-");
                }
            }
            i++;
        }
        return data;
    }

    public static void main(String[] args) {
        ExcelWorker excelWorker = new ExcelWorker(new File(XML_FILE_LOCATION));
        Map<Integer, List<String>> data = excelWorker.readFile();

        // create a new text file for each document(row in excel)
        data.forEach((i, d) -> {
            try {
                File file = new File(InvertedIndex.DATASET_DIRECTORY_PATH + i + ".txt");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(d.get(0).getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
