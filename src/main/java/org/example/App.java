package org.example;

public class App {
    public static void main(String[] args) {
        String content = "<h1>Nayeem loves counseling</h1>\n" +
                "<Amee>safat codes like a ninja</amee>\n" +
                "<SA premium>Imtiaz has a secret crush</SA premium>";
        System.out.println(content);
        TagParser tagParser = new TagParser();
        tagParser.getTaggedContent(content).forEach(System.out::println);
    }
}
