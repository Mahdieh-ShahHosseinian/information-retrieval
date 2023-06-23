package com.example.informationretrieval.practice_3_1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.example.informationretrieval.practice_3_1.HTMLTag.BODY;
import static com.example.informationretrieval.practice_3_1.HTMLTag.HEAD;

/**
 * <a href="https://riptutorial.com/lucene/example/19933/booleanquery">https://riptutorial.com/lucene/example/19933/booleanquery</a>
 * <a href="https://howtodoinjava.com/lucene/lucene-index-search-examples/">https://howtodoinjava.com/lucene/lucene-index-search-examples/</a>
 * <a href="https://howtodoinjava.com/lucene/lucene-wildcardquery-search-example/">https://howtodoinjava.com/lucene/lucene-wildcardquery-search-example/</a>
 * <a href="https://www.baeldung.com/lucene">https://www.baeldung.com/lucene</a>
 */
public class LuceneSearcher {
    private static final String INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\crawler\\index\\";

    private final Analyzer standardAnalyzer;
    private IndexSearcher headIndexSearcher;
    private IndexSearcher bodyIndexSearcher;

    public LuceneSearcher() {
        this.standardAnalyzer = new StandardAnalyzer();
        this.createSearcher(HEAD);
        this.createSearcher(BODY);
    }

    public void createTermQuery(String term) {
        try {
            Set<String> foundDocuments = new HashSet<>();

            TopDocs foundHeadIndexedDocuments = this.searchInContent(headIndexSearcher, term);
            TopDocs foundBodyIndexedDocuments = this.searchInContent(bodyIndexSearcher, term);

            System.out.println("Example: Term query for \"" + term + "\"");
            // Total found documents
            System.out.println("Total retrieved docs (found in title-page): " + foundHeadIndexedDocuments.totalHits + "\n");
            System.out.println("Total retrieved docs (found in body-page): " + foundBodyIndexedDocuments.totalHits + "\n");

            /* print out the result */
            // query found in head of document. found documents are:
            for (ScoreDoc sd : foundHeadIndexedDocuments.scoreDocs) {
                Document d = this.headIndexSearcher.doc(sd.doc);
                String documentPath = d.get("path");
                foundDocuments.add(documentPath);
                System.out.println(documentPath + ", document-title-content: " + d.get("contents") + ", Score : " + sd.score);
            }

            // query found in body of document. found documents are:
            for (ScoreDoc sd : foundBodyIndexedDocuments.scoreDocs) {
                Document d = this.bodyIndexSearcher.doc(sd.doc);
                String documentPath = d.get("path");
                if (!foundDocuments.contains(documentPath)) {
                    foundDocuments.add(documentPath);
                    System.out.println(documentPath + ", document-body-content: " + d.get("contents") + ", Score : " + sd.score);
                }
            }
            System.out.println("\n===========================\n");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private TopDocs searchInContent(IndexSearcher indexSearcher, String textToFind) throws ParseException, IOException {
        //Create search query
        QueryParser qp = new QueryParser("contents", this.standardAnalyzer);
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = indexSearcher.search(query, 50);
        return hits;
    }

    public void createPhraseQuery(String phrase) {
        try {
            String[] words = phrase.split("\\s+");

            // create phraseQuery
            QueryBuilder queryBuilder = new QueryBuilder(this.standardAnalyzer);
            Query query = queryBuilder.createPhraseQuery("contents", Arrays.toString(words));

            Set<String> foundDocuments = new HashSet<>();

            TopDocs foundHeadIndexedDocuments = this.headIndexSearcher.search(query, 50);
            TopDocs foundBodyIndexedDocuments = this.bodyIndexSearcher.search(query, 50);

            System.out.println("Example: Phrase query for \"" + phrase + "\"");
            // Total found documents
            System.out.println("Total retrieved docs (found in head-page): " + foundHeadIndexedDocuments.totalHits + "\n");
            System.out.println("Total retrieved docs (found in body-page): " + foundBodyIndexedDocuments.totalHits + "\n");

            /* print out the result */
            // query found in head of document. found documents are:
            for (ScoreDoc sd : foundHeadIndexedDocuments.scoreDocs) {
                Document d = this.headIndexSearcher.doc(sd.doc);
                String documentPath = d.get("path");
                foundDocuments.add(documentPath);
                System.out.println(documentPath + ", document-title-content: " + d.get("contents") + ", Score : " + sd.score);
            }

            // query found in body of document. found documents are:
            for (ScoreDoc sd : foundBodyIndexedDocuments.scoreDocs) {
                Document d = this.bodyIndexSearcher.doc(sd.doc);
                String documentPath = d.get("path");
                if (!foundDocuments.contains(documentPath)) {
                    foundDocuments.add(documentPath);
                    System.out.println(documentPath + ", document-body-content: " + d.get("contents") + ", Score : " + sd.score);
                }
            }

            System.out.println("\n===========================\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSearcher(HTMLTag htmlTag) {
        try {
            Path path = null;
            switch (htmlTag) {
                case HEAD -> path = Paths.get(INDEX_DIR + "\\head\\");
                case BODY -> path = Paths.get(INDEX_DIR + "\\body\\");
            }
            Directory dir = FSDirectory.open(path);
            IndexReader reader = DirectoryReader.open(dir);
            switch (htmlTag) {
                case HEAD -> this.headIndexSearcher = new IndexSearcher(reader);
                case BODY -> this.bodyIndexSearcher = new IndexSearcher(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
