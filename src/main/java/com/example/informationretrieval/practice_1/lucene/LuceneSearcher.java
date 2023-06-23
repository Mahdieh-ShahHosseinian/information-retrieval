package com.example.informationretrieval.practice_1.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * <a href="https://riptutorial.com/lucene/example/19933/booleanquery">https://riptutorial.com/lucene/example/19933/booleanquery</a>
 * <a href="https://howtodoinjava.com/lucene/lucene-index-search-examples/">https://howtodoinjava.com/lucene/lucene-index-search-examples/</a>
 * <a href="https://howtodoinjava.com/lucene/lucene-wildcardquery-search-example/">https://howtodoinjava.com/lucene/lucene-wildcardquery-search-example/</a>
 * <a href="https://www.baeldung.com/lucene">https://www.baeldung.com/lucene</a>
 */
public class LuceneSearcher {
    private static final String INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\index\\";
    private static final String STEM_INDEX_DIR = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\stemmed-index\\";

    public static void main(String[] args) throws Exception {
        LuceneSearcher luceneSearcher = new LuceneSearcher();

        IndexSearcher indexSearcher = luceneSearcher.createSearcher(INDEX_DIR);
        IndexSearcher stemmedIndexSearcher = luceneSearcher.createSearcher(STEM_INDEX_DIR);

        // analyzer without stemming
        Analyzer standardAnalyzer = new StandardAnalyzer();

        // analyzer with stemming
        Analyzer englishAnalyzer = new EnglishAnalyzer();

        /* WildcardQuery */
        // not need to stemming

        // Create wildcard query-1 - Wildcard "*" Example
        luceneSearcher.createWildcardQuery(indexSearcher, standardAnalyzer, "inter*");
        // Create wildcard query-2 - Wildcard "?" Example
        luceneSearcher.createWildcardQuery(indexSearcher, standardAnalyzer, "????le");


        /* TermQuery */

        // Create term query-1 - Term "company" Example
        luceneSearcher.createTermQuery(stemmedIndexSearcher, englishAnalyzer, "company");
        // Create term query-2 - Term "world" Example
        luceneSearcher.createTermQuery(stemmedIndexSearcher, englishAnalyzer, "world");

        /* PhraseQuery */
        // not need to stemming

        // Create phrase query-1 - Phrase "related services" Example
        String phrase1 = "related services";
        luceneSearcher.createPhraseQuery(indexSearcher, standardAnalyzer, phrase1);
        // Create phrase query-2 - Phrase "fan of google" Example
        String phrase2 = "fan of google";
        luceneSearcher.createPhraseQuery(indexSearcher, standardAnalyzer, phrase2);

        /* BooleanQuery */

        // Create boolean query-1 - query "services AND NOT products" Example
        luceneSearcher.createBooleanQuery1(stemmedIndexSearcher);
        // Create boolean query-2 - query "google AND NOT monopoly OR microsoft" Example
        luceneSearcher.createBooleanQuery2(stemmedIndexSearcher);
    }

    private void createWildcardQuery(IndexSearcher searcher, Analyzer analyzer, String wildcard) throws IOException {
        Query query = new WildcardQuery(new Term("contents", wildcard));

        // Search the lucene documents
        TopDocs hits = searcher.search(query, 50, Sort.INDEXORDER);

        System.out.println("Example: Wildcard query for \"" + wildcard + "\"");
        System.out.println("Search terms found in: " + hits.totalHits + " docs\n");

        UnifiedHighlighter highlighter = new UnifiedHighlighter(searcher, analyzer);
        String[] fragments = highlighter.highlight("contents", query, hits);

        for (String f : fragments) {
            System.out.println(f);
        }
        System.out.println("\n===========================\n");
    }

    private void createTermQuery(IndexSearcher searcher, Analyzer analyzer, String term) throws Exception {
        TopDocs foundDocs = this.searchInContent(searcher, analyzer, term);

        System.out.println("Example: Term query for \"" + term + "\"");
        // Total found documents
        System.out.println("Total retrieved docs: " + foundDocs.totalHits + "\n");

        // print out the result
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("path") + ", document-content: " + d.get("contents") + ", Score : " + sd.score);
        }
        System.out.println("\n===========================\n");
    }

    private TopDocs searchInContent(IndexSearcher searcher, Analyzer analyzer, String textToFind) throws Exception {
        //Create search query
        QueryParser qp = new QueryParser("contents", analyzer);
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 50);
        return hits;
    }

    private void createPhraseQuery(IndexSearcher searcher, Analyzer analyzer, String phrase) throws IOException {
        String[] words = phrase.split("\\s+");

        // create phraseQuery
        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
        Query query = queryBuilder.createPhraseQuery("contents", Arrays.toString(words));

        // display search results
        TopDocs foundDocs = searcher.search(query, 50);

        System.out.println("Example: Phrase query for \"" + phrase + "\"");
        // Total found documents
        System.out.println("Total retrieved docs: " + foundDocs.totalHits + "\n");

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("path") + ", document-content: " + d.get("contents") + ", Score : " + sd.score);
        }
        System.out.println("\n===========================\n");
    }

    /**
     * BooleanClause.Occur.MUST - The sub-query must be matched.
     * BooleanClause.Occur.SHOULD - The sub-query may not be matched, but will be scored more highly if it is. If there are no MUST clauses, then at least one SHOULD clause must be matched.
     * BooleanClause.Occur.MUST_NOT - The sub-query must not match the document.
     */
    private void createBooleanQuery1(IndexSearcher searcher) throws IOException {
        String word1 = "services";
        String word2 = "products";

        Term term1 = new Term("contents", this.stemWord(word1));
        Term term2 = new Term("contents", this.stemWord(word2));

        TermQuery query1 = new TermQuery(term1);
        TermQuery query2 = new TermQuery(term2);

        // create boolean query
        BooleanQuery query = new BooleanQuery.Builder()
                .add(query1, BooleanClause.Occur.MUST)
                .add(query2, BooleanClause.Occur.MUST_NOT)
                .build();

        // display search results
        TopDocs foundDocs = searcher.search(query, 50);

        System.out.println("Example: Boolean query for \"" + word1 + " AND NOT " + word2 + "\"");
        // Total found documents
        System.out.println("Total retrieved docs: " + foundDocs.totalHits + "\n");

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("path") + ", document-content: " + d.get("contents") + ", Score : " + sd.score);
        }
        System.out.println("\n===========================\n");
    }

    /**
     * In this example, a document will match if it has "google", but not "monopoly", and will get a higher score if it also has "microsoft".
     */
    private void createBooleanQuery2(IndexSearcher searcher) throws IOException {
        String word1 = "google", word2 = "monopoly", word3 = "microsoft";

        Term term1 = new Term("contents", this.stemWord(word1));
        Term term2 = new Term("contents", this.stemWord(word2));
        Term term3 = new Term("contents", this.stemWord(word3));

        TermQuery query1 = new TermQuery(term1);
        TermQuery query2 = new TermQuery(term2);
        TermQuery query3 = new TermQuery(term3);

        // create boolean query
        BooleanQuery query = new BooleanQuery.Builder()
                .add(query1, BooleanClause.Occur.MUST)
                .add(query2, BooleanClause.Occur.MUST_NOT)
                .add(query3, BooleanClause.Occur.SHOULD)
                .build();

        // display search results
        TopDocs foundDocs = searcher.search(query, 50);

        System.out.println("Example: Boolean query for \"" + word1 + " AND NOT " + word2 + " OR " + word3 + "\"");
        // Total found documents
        System.out.println("Total retrieved docs: " + foundDocs.totalHits + "\n");

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("path") + ", document-content: " + d.get("contents") + ", Score : " + sd.score);
        }
        System.out.println("\n===========================\n");
    }

    private String stemWord(String word) {
        PorterStemmer porterStemmer = new PorterStemmer();
        porterStemmer.setCurrent(word);
        porterStemmer.stem();
        return porterStemmer.getCurrent();
    }

    private IndexSearcher createSearcher(String path) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(path));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}
