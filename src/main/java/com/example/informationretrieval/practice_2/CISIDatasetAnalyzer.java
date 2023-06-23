package com.example.informationretrieval.practice_2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CISIDatasetAnalyzer {
    public static final String DATASET_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\";
    public static final String DOCUMENT_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\document\\";
    public static final String QUERY_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\query\\";
    public static final String RELEVANT_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\relevant\\";

    public static void main(String[] args) {
        readAllAndQryFile(DATASET_DIRECTORY_PATH + "CISI.ALL.txt", DOCUMENT_DIRECTORY_PATH);
        readAllAndQryFile(DATASET_DIRECTORY_PATH + "CISI.QRY.txt", QUERY_DIRECTORY_PATH);
        readRelFile(DATASET_DIRECTORY_PATH + "CISI.REL.txt", RELEVANT_DIRECTORY_PATH);
    }

    private static void readAllAndQryFile(String path, String destinationPath) {
        File file = new File(path);
        try {
            int i = 1;
            Scanner scanner = new Scanner(new FileInputStream(file));
            boolean tagFound = false;
            boolean appending = false;
            StringBuilder text = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.equals(".W")) {
                    tagFound = true;
                } else if (line.equals(".X") || line.startsWith(".I ") || line.equals(".B")) {
                    tagFound = false;
                }

                if (tagFound) {
                    appending = true;
                    text.append(line);
                } else if (appending) {
                    // removing ".W" tag
                    text.replace(0, 2, "");
                    writeToFile(destinationPath, i++, text.toString());
                    appending = false;
                    text = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readRelFile(String path, String destination) {
        File file = new File(path);
        try {
            int queryIdTemp = 1;
            Scanner scanner = new Scanner(new FileInputStream(file));
            StringBuilder text = new StringBuilder();
            while (scanner.hasNextLine()) {
                int queryId = scanner.nextInt();
                int documentId = scanner.nextInt();
                scanner.nextLine();

                text.append(documentId).append("\n");

                if (queryId != queryIdTemp) {
                    writeToFile(destination, queryIdTemp, String.valueOf(text));
                    queryIdTemp = queryId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String destinationPath, int i, String text) throws IOException {
        File file = new File(destinationPath + i + ".txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
    }
}
