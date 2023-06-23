package com.example.informationretrieval.practice_3_1;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static com.example.informationretrieval.practice_3_1.HTMLTag.BODY;
import static com.example.informationretrieval.practice_3_1.HTMLTag.HEAD;

public class Crawler {
    private static final String CRAWLED_LINKS_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\crawler\\links\\";
    private static int DATASET_INDEX = 1;

    private final String datasetDirectoryPath;
    private final int limit;

    private final Queue<String> linksQueue = new LinkedList<>();
    private final Set<String> storedPages = new HashSet<>();

    public Crawler(String url, String datasetDirectoryPath, int limit) {
        this.datasetDirectoryPath = datasetDirectoryPath;
        this.limit = limit;
        processPage(url);
    }

    public void crawl() {
        do {
            String link = linksQueue.remove();
            storedPages.add(link);
            processPage(link);
        } while (DATASET_INDEX <= limit);
        createResultFile(storedPages.toString());
    }

    private void processPage(String url) {
        System.out.println("Fetching " + url + " ...");

        try {
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get();
            Elements elements = doc.select("a[href]");
            registerDocumentText(doc);

            for (org.jsoup.nodes.Element element : elements) {
                String link = element.attr("abs:href");
                if (!linksQueue.contains(link) && !storedPages.contains(link) && !link.equals("javascript:void(0)") && !link.equals(url)) {
                    System.out.println(" * a: <" + link + ">  (" + trim(element.text()) + ")");
                    linksQueue.add(link);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void registerDocumentText(Document document) {
        String head = getDocumentHead(document);
        String body = getDocumentBody(document);
        createDocumentFile(head, HEAD);
        createDocumentFile(body, BODY);
        DATASET_INDEX++;
    }

    private String getDocumentHead(Document doc) {
        return doc.head().text();
    }

    private String getDocumentBody(Document doc) {
        return doc.text();
    }

    private void createDocumentFile(String text, HTMLTag htmlTag) {
        String path = null;
        switch (htmlTag) {
            case HEAD -> path = datasetDirectoryPath + "\\head\\";
            case BODY -> path = datasetDirectoryPath + "\\body\\";
        }
        File file = new File(path + DATASET_INDEX + ".txt");
        writeToFile(file, text);
    }

    private void createResultFile(String text) {
        File file = new File(CRAWLED_LINKS_DIRECTORY_PATH + "result.txt");
        writeToFile(file, text);
    }

    private void writeToFile(File file, String text) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String trim(String s) {
        if (s.length() > 35) {
            return s.substring(0, 35 - 1) + ".";
        } else {
            return s;
        }
    }
}