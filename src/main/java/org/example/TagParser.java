package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TagParser {
    public List<String> getTaggedContent(String content){
        List<String> splittedContent = Arrays.asList(content.split("\\n"));
        List<String> resultText = new ArrayList<>();
        System.out.println(splittedContent);
        for (String contentString : splittedContent){
            while (contentString.length() > 0 && hasMoreTags(contentString)){
                String tagString = lookupNextTag(contentString);
                if (isTagValid(contentString,tagString)){
                    String beginTag = buildBeginTagFrom(tagString);
                    String enclosingTag = buildEnclosingTagFrom(tagString);
                    String taggedText = getTaggedText(contentString,beginTag,enclosingTag);
                    resultText.add(taggedText);
                    contentString = deleteRetrievedText(contentString,enclosingTag);
                } else {
                    contentString = dropInvalidTag(contentString,tagString);
                }
            }
        }

        return resultText;
    }

    private String buildBeginTagFrom(String tagString){
        return "<"+tagString+">";
    }

    private String buildEnclosingTagFrom(String tagString){
        return "</"+tagString+">";
    }

    private String getTaggedText(String content, String beginTag, String enclosingTag){

        int beginIndex = content.indexOf(beginTag)+beginTag.length();
        int enclosingIndex = content.indexOf(enclosingTag,beginIndex);
        return content.substring(beginIndex,enclosingIndex);
    }

    private String deleteRetrievedText(String string, String enclosingTag){
        return string.substring(string.indexOf(enclosingTag) + enclosingTag.length());
    }

    private String dropInvalidTag(String string, String tagString){
        String beginTag = buildBeginTagFrom(tagString);
        return string.substring(string.indexOf(beginTag) + beginTag.length());
    }

    private boolean hasMoreTags(String content){
        return Pattern.compile(".*<.+>.*").matcher(content).matches();
    }

    private boolean isTagValid(String string, String tagString){
        String beginTag = buildBeginTagFrom(tagString);
        String enclosingTag = buildEnclosingTagFrom(tagString);

        if (string.contains(enclosingTag)){
            String taggedString = getTaggedText(string,beginTag,enclosingTag);
            return !taggedString.contains(beginTag);
        }

        return false;
    }

    private String lookupNextTag(String content){
        String tagString = content.substring(content.indexOf("<") + 1,content.indexOf(">"));
        return tagString;
    }
}
