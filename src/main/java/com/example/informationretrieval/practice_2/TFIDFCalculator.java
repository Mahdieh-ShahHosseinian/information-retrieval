package com.example.informationretrieval.practice_2;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.informationretrieval.practice_2.Type.DOCUMENT;
import static com.example.informationretrieval.practice_2.Type.QUERY;
import static com.example.informationretrieval.practice_2.Utility.*;

public class TFIDFCalculator {
    private static final int TOTAL_DOCUMENT_NO = 60;
    private static final int TOTAL_QUERY_NO = 10;

    private static final String DOCUMENT_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\document\\";
    private static final String QUERY_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\query\\";
    private static final String RESULT_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\result";
    public static final String RELEVANT_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\cisi\\relevant\\";

    private static final CharArraySet STOP_WORDS = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;

    private final Set<String> terms = new HashSet<>();

    private final Map<Integer, List<String>> documentToId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "Document To ID:");
        }
    };
    private final Map<Integer, List<String>> queryToId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "Query To ID:");
        }
    };

    private final Map<Integer, Map<String, Integer>> TFRawToDocumentId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(TF-Raw) Raw TermFrequency To Document-ID:");
        }
    };
    private final Map<Integer, Map<String, Integer>> TFRawToQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(TF-Raw) Raw TermFrequency To Query-ID:");
        }
    };

    private final Map<Integer, Map<String, Double>> TFWeightedToDocumentId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(TF-Weighted) Weighted TermFrequency To Document-ID:");
        }
    };
    private final Map<Integer, Map<String, Double>> TFWeightedToQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(TF-Weighted) Weighted TermFrequency To Query-ID:");
        }
    };

    private final Map<String, Integer> DFToTerm = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(DF) DocumentFrequency To Term:");
        }
    };

    private final Map<String, Double> IDFToTerm = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "(IDF) InverseDocumentFrequency To Term:");
        }
    };

    private final Map<List<Object>, Double> TFIDFToTermAndDocumentId = new HashMap<>() {
        @Override
        public String toString() {
            return dualKeyMapCustomToString(super.toString(), "(TF-IDF) TF * IDF To Term & Document-ID:");
        }
    };
    private final Map<List<Object>, Double> TFIDFToTermAndQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return dualKeyMapCustomToString(super.toString(), "(TF-IDF) TF * IDF To Term & Query-ID:");
        }
    };

    private final Map<Integer, Double> weightToDocumentId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "Document Weight To Document-ID:");
        }
    };
    private final Map<Integer, Double> weightToQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return singleKeyMapCustomToString(super.toString(), "Query Weight To Query-ID:");
        }
    };

    private final Map<List<Object>, Double> normalizedTFIDFToTermAndDocumentId = new HashMap<>() {
        @Override
        public String toString() {
            return dualKeyMapCustomToString(super.toString(), "(Normalized TF-IDF) Normalized TF * IDF To Term & Document-ID:");
        }
    };
    private final Map<List<Object>, Double> normalizedTFIDFToTermAndQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return dualKeyMapCustomToString(super.toString(), "(Normalized TF-IDF) Normalized TF * IDF To Term & Query-ID:");
        }
    };

    private final Map<Integer, List<Integer>> top10RetrievedDocumentIdToQueryId = new HashMap<>() {
        @Override
        public String toString() {
            return dualKeyMapCustomToString(super.toString(), "Top 10 Related Document-ID To Query-ID:");
        }
    };

    public static void main(String[] args) {
        TFIDFCalculator tfidfCalculator = new TFIDFCalculator();

        /* documents */
        List<List<String>> documents = getDocuments();
        // register documents
        tfidfCalculator.register(DOCUMENT, documents);
        // TF-RAW
        tfidfCalculator.calculateTFRaw(DOCUMENT);
        // TF-WEIGHTED
        tfidfCalculator.calculateTFWeighted(DOCUMENT);
        // DF
        tfidfCalculator.calculateDF();
        // IDF
        tfidfCalculator.calculateIDF();
        // TF-IDF
        tfidfCalculator.calculateTFIDF(DOCUMENT);
        // Normalized TF-IDF
        tfidfCalculator.normalizeDocumentTFIDF(DOCUMENT);

        /* queries */
        List<List<String>> queries = getQueries();
        // register queries
        tfidfCalculator.register(QUERY, queries);
        // TF-RAW
        tfidfCalculator.calculateTFRaw(QUERY);
        // TF-WEIGHTED
        tfidfCalculator.calculateTFWeighted(QUERY);
        // TF-IDF
        tfidfCalculator.calculateTFIDF(QUERY);
        // Normalized TF-IDF
        tfidfCalculator.normalizeDocumentTFIDF(QUERY);

        // Top K
        tfidfCalculator.getTopKRankedDocumentsForQueries(10);

        // Precision and Recall and F1
        tfidfCalculator.calculatePRF1();

        tfidfCalculator.printOutResult();
    }

    private static List<List<String>> getDocuments() {
        return getElements(TOTAL_DOCUMENT_NO, DOCUMENT_DIRECTORY_PATH);
    }

    private static List<List<String>> getQueries() {
        return getElements(TOTAL_QUERY_NO, QUERY_DIRECTORY_PATH);
    }

    private static List<List<String>> getElements(int dataNo, String dataPath) {
        List<List<String>> elements = new ArrayList<>();
        try {
            for (int i = 1; i <= dataNo; i++) {
                File file = new File(dataPath + i + ".txt");
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
                    line = line.replace("?", " ");
                    line = line.replace(",", " ");
                    line = line.replace(".", " ");
                    line = line.replace(";", " ");
                    line = line.replace("!", " ");
                    line = line.replace("\"", " ");
                    line = line.replace("-", " ");
                    line = line.replace("(", " ");
                    line = line.replace(")", " ");
//                    line = line.replace("'", " ");
                    line = line.replace(":", " ");
                    line = line.replace("<", " ");
                    line = line.replace(">", " ");
                    line = line.replace("[", " ");
                    line = line.replace("]", " ");

                    String[] words = line.split("\\s+");
                    words = removeStopWords(words);
                    elements.add(Arrays.asList(words));
                }

            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return elements;
    }

    private static String[] removeStopWords(String[] words) {
        List<String> res = Arrays.stream(words).filter(word -> !STOP_WORDS.contains(word)).toList();
        return res.toArray(new String[0]);
    }

    private void register(Type type, List<List<String>> lists) {
        Map<Integer, List<String>> listsToId = null;
        switch (type) {
            case DOCUMENT -> listsToId = this.documentToId;
            case QUERY -> listsToId = this.queryToId;
        }
        for (int i = 0; i < lists.size(); i++) {
            listsToId.put(i + 1, lists.get(i));
        }
    }

    /**
     * tf-raw: term frequency raw (unweighted)
     * <p>
     * tf(t, d)
     */
    private void calculateTFRaw(Type type) {
        Map<Integer, List<String>> contentListToId = null;
        switch (type) {
            case DOCUMENT -> contentListToId = this.documentToId;
            case QUERY -> contentListToId = this.queryToId;
        }

        AtomicInteger i = new AtomicInteger(1);
        contentListToId.forEach((id, contentList) -> {
            Map<String, Integer> TFs = new HashMap<>();
            for (String term : contentList) {
                TFs.merge(term, 1, Integer::sum);
            }
            switch (type) {
                case DOCUMENT -> this.TFRawToDocumentId.put(i.getAndIncrement(), TFs);
                case QUERY -> this.TFRawToQueryId.put(i.getAndIncrement(), TFs);
            }
        });
    }

    /**
     * tf-weighted: term frequency weighted
     * <p>
     * w(t,d) =  1 + log(tf(t,d))
     * <p>
     * w(t, d)
     */
    private void calculateTFWeighted(Type type) {
        Set<Map.Entry<Integer, Map<String, Integer>>> allTFs = null;
        switch (type) {
            case DOCUMENT -> allTFs = this.TFRawToDocumentId.entrySet();
            case QUERY -> allTFs = this.TFRawToQueryId.entrySet();
        }
        for (Map.Entry<Integer, Map<String, Integer>> TFsByType : allTFs) {
            Set<Map.Entry<String, Integer>> TFs = TFsByType.getValue().entrySet();
            Map<String, Double> tempTF = new HashMap<>();
            for (Map.Entry<String, Integer> TF : TFs) {
                tempTF.put(TF.getKey(), 1 + Math.log10(TF.getValue()));
                switch (type) {
                    case DOCUMENT -> this.TFWeightedToDocumentId.put(TFsByType.getKey(), tempTF);
                    case QUERY -> this.TFWeightedToQueryId.put(TFsByType.getKey(), tempTF);
                }
            }
        }
    }

    /**
     * df: document frequency
     * <p>
     * df(t)
     */
    private void calculateDF() {
        Collection<List<String>> documents = this.documentToId.values();

        for (List<String> document : documents) {
            this.terms.addAll(document);
        }

        for (String term : this.terms) {
            for (List<String> document : documents) {
                for (String word : document) {
                    if (word.equals(term)) {
                        this.DFToTerm.merge(term, 1, Integer::sum);
                        break;
                    }
                }
            }
        }
    }

    /**
     * idf: inverted document frequency
     * <p>
     * idf(t) = log(N / df(t))
     */
    private void calculateIDF() {
        this.DFToTerm.forEach((term, df) -> {
            Double idf = Math.log10((double) TOTAL_DOCUMENT_NO / df);
            this.IDFToTerm.put(term, idf);
        });
    }

    /**
     * tf-idf(t,d) = tf(t,d) * idf(t)
     */
    private void calculateTFIDF(Type type) {
        Map<Integer, Map<String, Double>> tfWeightedById = null;
        Map<List<Object>, Double> tfidfByTermAndQueryId = null;
        switch (type) {
            case DOCUMENT -> {
                tfWeightedById = this.TFWeightedToDocumentId;
                tfidfByTermAndQueryId = this.TFIDFToTermAndDocumentId;
            }
            case QUERY -> {
                tfWeightedById = this.TFWeightedToQueryId;
                tfidfByTermAndQueryId = this.TFIDFToTermAndQueryId;
            }
        }

        Map<List<Object>, Double> finalTfidfByTermAndQueryId = tfidfByTermAndQueryId;
        tfWeightedById.forEach((id, tf) -> tf.forEach((term, frequency) -> {
            Double idf = this.IDFToTerm.get(term);
            if (idf == null) { // in case if the term in query doesn't exist in documents repository
                idf = 0d;
            }
            double tfidf = frequency * idf;
            finalTfidfByTermAndQueryId.put(List.of(term, id), tfidf);

            this.calculateWeight(type, id, tfidf);
        }));
        this.finalizeWeightCalculation(type);
    }

    private void calculateWeight(Type type, int typeId, double tfidf) {
        Map<Integer, Double> weightById = null;
        switch (type) {
            case DOCUMENT -> weightById = this.weightToDocumentId;
            case QUERY -> weightById = this.weightToQueryId;

        }
        double temp = Math.pow(tfidf, 2.0);
        Double oldValue = weightById.get(typeId);
        if (oldValue != null) {
            temp += oldValue;
        }
        weightById.put(typeId, temp);
    }

    private void finalizeWeightCalculation(Type type) {
        Map<Integer, Double> typeWeightByTypeId = null;
        switch (type) {
            case DOCUMENT -> typeWeightByTypeId = this.weightToDocumentId;
            case QUERY -> typeWeightByTypeId = this.weightToQueryId;

        }
        Map<Integer, Double> finalTypeWeightByTypeId = typeWeightByTypeId;
        typeWeightByTypeId.forEach((id, weight) -> finalTypeWeightByTypeId.put(id, Math.sqrt(weight)));
    }

    private void normalizeDocumentTFIDF(Type type) {
        Set<Map.Entry<List<Object>, Double>> entries = null;
        Map<Integer, Double> weightById = null;
        Map<List<Object>, Double> normalizedTFIDFByTermAndId = null;
        switch (type) {
            case DOCUMENT -> {
                entries = this.TFIDFToTermAndDocumentId.entrySet();
                weightById = this.weightToDocumentId;
                normalizedTFIDFByTermAndId = this.normalizedTFIDFToTermAndDocumentId;
            }
            case QUERY -> {
                entries = this.TFIDFToTermAndQueryId.entrySet();
                weightById = this.weightToQueryId;
                normalizedTFIDFByTermAndId = this.normalizedTFIDFToTermAndQueryId;
            }
        }

        for (Map.Entry<List<Object>, Double> entry : entries) {
            Double weight = weightById.get(entry.getKey().get(1));
            normalizedTFIDFByTermAndId.put(entry.getKey(), entry.getValue() / weight);
        }
    }

    /**
     * cosine(query, document)
     */
    private double cosineSimilarity(double[] q, double[] d) {
        double result = 0d;
        for (int i = 0; i < q.length; i++) {
            result += q[i] * d[i];
        }
        return result;
    }

    private void getTopKRankedDocumentsForQueries(int k) {
        String[] terms = this.IDFToTerm.keySet().toArray(new String[0]);
        Set<Integer> queryIds = this.queryToId.keySet();
        Set<Integer> documentIds = this.documentToId.keySet();

        Map<List<Object>, Double> similarityToQueryIdAndDocumentId = new HashMap<>() {
            @Override
            public String toString() {
                return dualKeyMapCustomToString(super.toString(), "Similarity To Query-ID and Document-ID:");
            }
        };

        for (int queryId : queryIds) {
            double[] q = new double[terms.length];
            for (int j = 0; j < terms.length; j++) {
                Double tfidfForTermAndQuery = this.normalizedTFIDFToTermAndQueryId.get(List.of(terms[j], queryId));
                q[j] = (tfidfForTermAndQuery == null) ? 0d : tfidfForTermAndQuery;
            }

            for (int documentId : documentIds) {
                double[] d = new double[terms.length];
                for (int j = 0; j < terms.length; j++) {
                    Double tfidfForTermAndDocument = this.normalizedTFIDFToTermAndDocumentId.get(List.of(terms[j], documentId));
                    d[j] = (tfidfForTermAndDocument == null) ? 0d : tfidfForTermAndDocument;
                }
                similarityToQueryIdAndDocumentId.put(List.of(queryId, documentId), this.cosineSimilarity(q, d));

                writeContentToFile(RESULT_DIRECTORY_PATH, "\\similarity\\similarity " + queryId + " - unsorted", similarityToQueryIdAndDocumentId.toString());
                similarityToQueryIdAndDocumentId = sortByComparator(similarityToQueryIdAndDocumentId, "Similarity To Query-ID And Document-ID (Sorted):", true);
                writeContentToFile(RESULT_DIRECTORY_PATH, "\\similarity\\similarity " + queryId + " - sorted", similarityToQueryIdAndDocumentId.toString());

                Map<List<Object>, Double> topK = new LinkedHashMap<>() {
                    @Override
                    public String toString() {
                        return dualKeyMapCustomToString(super.toString(), "Top " + k + ":");
                    }
                };

                int i = 0;
                List<Integer> list = new ArrayList<>();
                for (Map.Entry<List<Object>, Double> entry : similarityToQueryIdAndDocumentId.entrySet()) {
                    topK.put(entry.getKey(), entry.getValue());
                    list.add((Integer) entry.getKey().get(1));
                    if (++i == 10) {
                        this.top10RetrievedDocumentIdToQueryId.put((Integer) entry.getKey().get(0), list);
                        break;
                    }
                }
                writeContentToFile(RESULT_DIRECTORY_PATH, "\\similarity\\similarity " + queryId + " - top " + k, topK.toString());
            }
        }
    }

    /**
     * Precision = Relevant Items Retrieved / retrieved Items
     * Recall = Relevant Items Retrieved / relevant Items
     */
    private void calculatePRF1() {
        List<PRF1> prf1List = new ArrayList<>() {
            @Override
            public String toString() {
                return this.listToString(super.toString());
            }

            private String listToString(String superToString) {
                String[] split = superToString.split(", Query-ID");
                StringBuilder builder = new StringBuilder();
                for (String s : split) {
                    builder.append(s);
                    builder.append(",");
                    builder.append('\n');
                    builder.append(" Query-ID");
                }
                return builder.toString();
            }
        };

        for (Map.Entry<Integer, List<Integer>> entry : this.top10RetrievedDocumentIdToQueryId.entrySet()) {
            int queryId = entry.getKey();
            List<Integer> retrievedItems = entry.getValue();
            List<Integer> relevantItems = this.getRelatedDocuments(queryId);
            List<Integer> relevantItemsRetrieved = this.getRelevantItemsRetrieved(retrievedItems, relevantItems);

            double precision = (double) relevantItemsRetrieved.size() / retrievedItems.size();
            double recall = (double) relevantItemsRetrieved.size() / relevantItems.size();
            double f1 = 1d / (((1d / precision) + (1d / recall)) / 2d);
            PRF1 prf1 = new PRF1(queryId, precision, recall, f1);
            prf1List.add(prf1);
        }
        writeContentToFile(RESULT_DIRECTORY_PATH + "\\precision recall f1", "prf1", prf1List.toString());
    }

    private List<Integer> getRelatedDocuments(int queryId) {
        List<Integer> relatedDocumentIds = new ArrayList<>();
        try {
            File file = new File(RELEVANT_DIRECTORY_PATH + "\\" + queryId + ".txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                relatedDocumentIds.add(scanner.nextInt());
                scanner.nextLine();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return relatedDocumentIds;
    }

    private List<Integer> getRelevantItemsRetrieved(List<Integer> top10RetrievedDocumentIds, List<Integer> relatedDocumentIds) {
        List<Integer> relevantItemsRetrieved = new ArrayList<>(top10RetrievedDocumentIds);
        relevantItemsRetrieved.retainAll(relatedDocumentIds);
        return relevantItemsRetrieved;
    }

    private void printOutResult() {
        writeContentToFile(RESULT_DIRECTORY_PATH, "document to id", this.documentToId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "query to id", this.queryToId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-raw to document-id", this.TFRawToDocumentId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-raw to query-id", this.TFRawToQueryId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-weighted to document-id", this.TFWeightedToDocumentId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-weighted to query-id", this.TFWeightedToQueryId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "df to term", this.DFToTerm.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "idf to term", this.IDFToTerm.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-idf to term and document-id", this.TFIDFToTermAndDocumentId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "tf-idf to term and query-id", this.TFIDFToTermAndQueryId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "weight to document-id", this.weightToDocumentId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "weight to query-id", this.weightToQueryId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "normalized tf-idf to term and document-id", this.normalizedTFIDFToTermAndDocumentId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "normalized tf-idf to term and query-id", this.normalizedTFIDFToTermAndQueryId.toString());
        writeContentToFile(RESULT_DIRECTORY_PATH, "top 10 retrieved document-id to query-id", this.top10RetrievedDocumentIdToQueryId.toString());
    }

    static class PRF1 {
        int queryId;
        double precision;
        double recall;
        double f1;

        public PRF1(int queryId, double precision, double recall, double f1) {
            this.queryId = queryId;
            this.precision = precision;
            this.recall = recall;
            this.f1 = f1;
        }

        @Override
        public String toString() {
            return "Query-ID=" + queryId + ": Precision=[" + precision + "], Recall=[" + recall + "], F1=[" + f1 + "]";
        }
    }
}