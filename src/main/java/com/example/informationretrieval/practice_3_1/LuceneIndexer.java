package com.example.informationretrieval.practice_3_1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.example.informationretrieval.practice_3_1.HTMLTag.BODY;
import static com.example.informationretrieval.practice_3_1.HTMLTag.HEAD;

/**
 * <a href="https://howtodoinjava.com/lucene/lucene-index-search-examples/">https://howtodoinjava.com/lucene/lucene-index-search-examples/</a>
 * an IndexWriter is responsible for creating the index and an IndexSearcher for searching the index.
 */
public class LuceneIndexer {
    private static final String INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\crawler\\index\\";

    private final String datasetDirectoryPath;
    private final int totalDocumentNumber;

    private final IndexWriter headIndexWriter;
    private final IndexWriter bodyIndexWriter;

    public LuceneIndexer(String datasetDirectoryPath, int totalDocumentNumber) {
        this.datasetDirectoryPath = datasetDirectoryPath;
        this.totalDocumentNumber = totalDocumentNumber;

        // Analyzer without stemming
        Analyzer standardAnalyzer = new StandardAnalyzer();
        this.headIndexWriter = this.createWriter(standardAnalyzer, HEAD);
        this.bodyIndexWriter = this.createWriter(standardAnalyzer, BODY);
    }

    public void createIndex() {
        createIndex(headIndexWriter, HEAD);
        createIndex(bodyIndexWriter, BODY);
    }

    private void createIndex(IndexWriter indexWriter, HTMLTag htmlTag) {
        String path = datasetDirectoryPath;
        switch (htmlTag) {
            case HEAD -> path += "\\head\\";
            case BODY -> path += "\\body\\";
        }

        try {
            List<Document> documents = new ArrayList<>();

            for (int i = 1; i <= this.totalDocumentNumber; i++) {
                File file = new File(path + i + ".txt");
                Document document = this.createDocument(file);
                documents.add(document);
            }

            indexWriter.deleteAll();

            indexWriter.addDocuments(documents);
            indexWriter.commit();
            indexWriter.close();
            // Now lucene indexes created in configured folder path
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private IndexWriter createWriter(Analyzer analyzer, HTMLTag htmlTag) {
        try {
            Path path = null;
            switch (htmlTag) {
                case HEAD -> path = Paths.get(INDEX_DIR + "\\head\\");
                case BODY -> path = Paths.get(INDEX_DIR + "\\body\\");
            }
            FSDirectory dir = FSDirectory.open(path);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, config);
            return writer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocument(File file) throws IOException {
        Document document = new Document();
        document.add(new StringField("path", file.getName(), Field.Store.YES));
        document.add(new TextField("contents", new String(Files.readAllBytes(Path.of(file.getPath()))), Field.Store.YES));
        return document;
    }
}
