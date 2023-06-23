package com.example.informationretrieval.practice_1.inverted_index;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Practice-1
 */
public class InvertedIndex {
    public static final String DATASET_DIRECTORY_PATH = "E:\\New folder\\Workspaces\\IdeaProjects\\information-retrieval\\src\\main\\resources\\dataset\\";

    private static final int TOTAL_DOCUMENT_NO = 50;
    private final Map<String, TermInfo> invertedIndexMapByWord = new TreeMap<>();

    public InvertedIndex() {

    }

    public void addTerm(String newTerm, Integer documentNo) {
        TermInfo termInfo = this.invertedIndexMapByWord.get(newTerm);
        if (termInfo != null) {
            termInfo.addFrequency();
            termInfo.addPostingsList(documentNo);
        } else {
            this.invertedIndexMapByWord.put(newTerm, new TermInfo(documentNo));
        }
    }

    public Map<String, TermInfo> getInvertedIndexMapByWord() {
        return invertedIndexMapByWord;
    }

    public static void main(String[] args) throws IOException {
        InvertedIndex invertedIndex = new InvertedIndex();

        // read and index documents
        for (int i = 1; i <= TOTAL_DOCUMENT_NO; i++) {
            Integer documentNo = i;
            File file = new File(DATASET_DIRECTORY_PATH + documentNo + ".txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase(Locale.ROOT);
                line = line.replace("?", "");
                line = line.replace(",", "");
                line = line.replace(".", "");
                line = line.replace(";", "");
                line = line.replace("!", "");
                line = line.replace("\"", "");

                String[] words = line.split("\\s+");
                for (String word : words) {
                    invertedIndex.addTerm(word, documentNo);
                }
            }
            scanner.close();
        }
        // inverted indexed created.

        // input query
        Scanner scanner = new Scanner(System.in);
        System.out.println("Sample query: NOT yes AND human");
        System.out.println("Sample query: world OR company");
        System.out.println("Enter your query:");
        String query = scanner.nextLine().toLowerCase(Locale.ROOT);

        // parsing query
        String[] queryItems = query.split("\\s+");

        List<Integer> documents = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            documents.add(i);
        }

        List<Integer> resultPostingsList = new ArrayList<>();

        for (int i = 0; i < queryItems.length; i++) {
            String item = queryItems[i];
            if (i == 0) {
                if (item.equals("not")) { // NOT x
                    List<Integer> temp = new ArrayList<>(documents);
                    TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(queryItems[++i]);
                    if (termInfo != null) {
                        temp.removeAll(termInfo.getPostingsList());
                        resultPostingsList.addAll(temp);
                    }
                } else { // x
                    TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(item);
                    if (termInfo != null) {
                        resultPostingsList = termInfo.getPostingsList();
                    }
                }
                continue;
            }

            switch (item) {
                case "not" -> { // ... NOT x
                    List<Integer> temp = new ArrayList<>(documents);
                    temp.removeAll(invertedIndex.getInvertedIndexMapByWord().get(queryItems[++i]).getPostingsList());
                    resultPostingsList.addAll(temp);
                }
                case "and" -> {
                    String right = queryItems[++i];
                    if (right.equals("not")) { // ... and NOT x
                        List<Integer> temp = new ArrayList<>(documents);
                        TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(queryItems[++i]);
                        if (termInfo != null) {
                            temp.removeAll(termInfo.getPostingsList());
                            resultPostingsList.retainAll(temp);
                        }
                    } else { // ... and x
                        List<Integer> rightOperandPostingsList;
                        TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(right);
                        if (termInfo != null) {
                            rightOperandPostingsList = termInfo.getPostingsList();
                            resultPostingsList.retainAll(rightOperandPostingsList);
                        }
                    }
                }
                case "or" -> {
                    String right = queryItems[++i];
                    if (right.equals("not")) { // ... or NOT x
                        List<Integer> temp = new ArrayList<>(documents);
                        TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(queryItems[++i]);
                        if (termInfo != null) {
                            temp.removeAll(termInfo.getPostingsList());
                            resultPostingsList.addAll(temp);
                        }
                    } else { // ... or x
                        List<Integer> rightOperandPostingsList;
                        TermInfo termInfo = invertedIndex.getInvertedIndexMapByWord().get(right);
                        if (termInfo != null) {
                            rightOperandPostingsList = termInfo.getPostingsList();
                            resultPostingsList.addAll(rightOperandPostingsList);
                        }
                    }
                }
            }
        }

        System.out.println("Retrieved docs:\n" + resultPostingsList);
    }
}
