package com.example.informationretrieval.practice_3_1;

public class App {
    private static final String DATASET_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\crawler\\dataset\\";
    private static final int NUMBER = 200;

    public static void main(String[] args) {
        // crawling
        String url = "https://stackoverflow.com";
        Crawler crawler = new Crawler(url, DATASET_DIRECTORY_PATH, NUMBER);
        crawler.crawl();

        // indexing
        LuceneIndexer luceneIndexer = new LuceneIndexer(DATASET_DIRECTORY_PATH, NUMBER);
        luceneIndexer.createIndex();

        // searching
        LuceneSearcher luceneSearcher = new LuceneSearcher();
        luceneSearcher.createTermQuery("ubuntu");
        luceneSearcher.createTermQuery("angular");
        luceneSearcher.createPhraseQuery("developers & technologists");
        luceneSearcher.createPhraseQuery("How to use multiple filters on an array in JavaScript?");
    }
}
