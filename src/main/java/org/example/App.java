package org.example;

import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String content = "<h1>Nayeem loves counseling</h1>\n" +
                "<h1><h1>Sanjay has no watch</h1></h1><par>So wait for a while</par>\n" +
                "<Amee>safat codes like a ninja</amee>\n" +
                "<SA premium>Imtiaz has a secret crush</SA premium>";
        List<String> split = Arrays.asList(content.split("\\n"));
        TagParser tagParser = new TagParser();
        split.stream().map(tagParser::parseTaggedTextFrom).flatMap(List::stream).forEach(System.out::println);
    }
}
