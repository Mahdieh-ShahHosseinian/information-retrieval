package com.example.informationretrieval.practice_2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Utility {
    public static String singleKeyMapCustomToString(String superToString, String name) {
        String[] split = superToString.split(",");
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append('\n');
        for (String s : split) {
            builder.append(s);
            builder.append('\n');
        }
        builder.append("===============================" +
                               "=======================" +
                               "=======================");
        return builder.toString();
    }

    public static String dualKeyMapCustomToString(String superToString, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append('\n');
        Scanner s = new Scanner(superToString);
        s.useDelimiter(" ");
        while (s.hasNext()) {
            String next = s.next();
            if (next.startsWith("[")) {
                builder.append('\n');
            }
            builder.append(next);
        }
        builder.append('\n');
        builder.append("===============================" +
                               "=======================" +
                               "=======================");
        return builder.toString();
    }

    public static Map<List<Object>, Double> sortByComparator(Map<List<Object>, Double> unsortedMap, String mapName, final boolean descending) {
        List<Map.Entry<List<Object>, Double>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> {
            if (descending) {
                return o2.getValue().compareTo(o1.getValue());
            } else {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<List<Object>, Double> sortedMap = new LinkedHashMap<>() {
            @Override
            public String toString() {
                return dualKeyMapCustomToString(super.toString(), mapName);
            }
        };
        for (Map.Entry<List<Object>, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void writeContentToFile(String filePath, String filename, String content) {
        try {
            File file = new File(filePath + "\\" + filename + ".txt");
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
