package com.example.informationretrieval.practice_2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Type {
    DOCUMENT, QUERY
}

//    private void calculateTFRaw(Type type) {
//        Map<Integer, List<String>> listsToId = null;
//        switch (type) {
//            case DOCUMENT -> listsToId = this.documentToId;
//            case QUERY -> listsToId = this.queryToId;
//        }
//
//        int i = 1;
//        listsToId.forEach((id, content) -> {
//            Map<String, Integer> TFs = new HashMap<>();
//            for (String term : content.get(i)) {
//                TFs.merge(term, 1, Integer::sum);
//            }
//            switch (type) {
//                case DOCUMENT -> this.TFRawToDocumentId.put(i + 1, TFs);
//                case QUERY -> this.TFRawToQueryId.put(i + 1, TFs);
//            }
//        });
//
//        for (int i = 0; i < listsToId.size(); i++) {
//            Map<String, Integer> TFs = new HashMap<>();
//            for (String term : listsToId.get(i)) {
//                TFs.merge(term, 1, Integer::sum);
//            }
//            switch (type) {
//                case DOCUMENT -> this.TFRawToDocumentId.put(i + 1, TFs);
//                case QUERY -> this.TFRawToQueryId.put(i + 1, TFs);
//            }
//        }
//    }
