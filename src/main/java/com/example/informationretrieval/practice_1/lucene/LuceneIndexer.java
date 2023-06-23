package com.example.informationretrieval.practice_1.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

/**
 * <a href="https://howtodoinjava.com/lucene/lucene-index-search-examples/">https://howtodoinjava.com/lucene/lucene-index-search-examples/</a>
 * an IndexWriter is responsible for creating the index and an IndexSearcher for searching the index.
 */
public class LuceneIndexer {
    public static final String DATASET_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\dataset\\";

    private static final int TOTAL_DOCUMENT_NO = 50;
    private static final String INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\index\\";
    private static final String STEM_INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\stemmed-index\\";

    public static void main(String[] args) throws IOException {
        LuceneIndexer luceneIndexer = new LuceneIndexer();

        // Analyzer without stemming
        Analyzer standardAnalyzer = new StandardAnalyzer();

        // Analyzer with stemming
        Analyzer englishAnalyzer = new EnglishAnalyzer();

        IndexWriter indexWriter = luceneIndexer.createWriter(standardAnalyzer, INDEX_DIR);
        createIndex(luceneIndexer, indexWriter);

        IndexWriter stemmedIndexWriter = luceneIndexer.createWriter(englishAnalyzer, STEM_INDEX_DIR);
        createIndex(luceneIndexer, stemmedIndexWriter);
    }

    private static void createIndex(LuceneIndexer luceneIndexer, IndexWriter indexWriter) throws IOException {
        List<Document> documents = new ArrayList<>();

        for (int i = 1; i <= TOTAL_DOCUMENT_NO; i++) {
            File file = new File(DATASET_DIRECTORY_PATH + i + ".txt");
            Document document = luceneIndexer.createDocument(file);
            documents.add(document);
        }

        indexWriter.deleteAll();

        indexWriter.addDocuments(documents);
        indexWriter.commit();
        indexWriter.close();
        // Now lucene indexes created in configured folder path
    }

    private IndexWriter createWriter(Analyzer analyzer, String path) throws IOException {
        FSDirectory dir = FSDirectory.open(Paths.get(path));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        return writer;
    }

    private Document createDocument(String... words) {
        Document document = new Document();
        for (String word : words) {
            document.add(new TextField(word, word, Field.Store.YES));
        }
        return document;
    }

    private Document createDocument(File file) throws IOException {
        Document document = new Document();
        document.add(new StringField("path", file.getName(), Field.Store.YES));
        document.add(new TextField("contents", new String(Files.readAllBytes(Path.of(file.getPath()))), Field.Store.YES));
        return document;
    }
}
