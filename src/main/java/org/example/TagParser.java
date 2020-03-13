package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TagParser {
    public List<String> getTaggedContent(String string,List<String> resultText){

        if (string.length() > 0 && hasTags(string)){
            String tagString = lookupNextTag(string);
            if (isTagValid(string,tagString)){
                String beginTag = buildBeginTagFrom(tagString);
                String enclosingTag = buildEnclosingTagFrom(tagString);
                String taggedText = getTaggedText(string,beginTag,enclosingTag);
                if (hasTags(taggedText)){
                    resultText = getTaggedContent(taggedText,resultText);
                } else if (taggedText.length() > 0){
                    resultText.add(taggedText);
                }

                string = deleteParsedContent(string,(beginTag + taggedText + enclosingTag).length());
            } else {
                string = dropInvalidTag(string,tagString);
            }
        } else {
            return resultText;
        }

        return getTaggedContent(string,resultText);
    }

    private List<String> getTaggedContent(String content){
        return getTaggedContent(content,new ArrayList<>());
    }

    public List<String> parseTaggedText(String content){
        List<String> splittedContent = Arrays.asList(content.split("\\n"));
        List<String> resultText = new ArrayList<>();
        for (String contentString : splittedContent){
            List<String> parsedText = getTaggedContent(contentString);
            if (parsedText.isEmpty()){
                resultText.add("None");
            } else {
                resultText.addAll(parsedText);
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
        int firstEnclosingTagIndex = content.indexOf(enclosingTag,beginIndex);
        int numberOfNestedTags = countNestedTagsIn(content.substring(beginIndex,firstEnclosingTagIndex),beginTag);
        int enclosingIndex = getEnclosingTagIndex(content,beginIndex,enclosingTag,numberOfNestedTags);
        return content.substring(beginIndex,enclosingIndex);
    }

    public int getEnclosingTagIndex(String content, int beginIndex, String enclosingTag, int numberOfNestedTags){
        int enclosingTagIndex = content.indexOf(enclosingTag,beginIndex);

        if (numberOfNestedTags == 0){
            if (enclosingTagIndex < 0){
                enclosingTagIndex = beginIndex - enclosingTag.length();
            }
            return enclosingTagIndex;
        }

        return getEnclosingTagIndex(content,enclosingTagIndex + enclosingTag.length(),enclosingTag,numberOfNestedTags - 1);
    }

    private int countNestedTagsIn(String string, String beginTag){
        int nestedTagsCount = 0;
        while (string.length() > 0 && hasTags(string)){
            String nestedTagString = lookupNextTag(string);
            String nestedTag =  buildBeginTagFrom(nestedTagString);

            if (nestedTag.equals(beginTag)){
                string = string.substring(string.indexOf(nestedTag) + nestedTag.length());
                nestedTagsCount++;
            } else {
                string = dropInvalidTag(string,nestedTagString);
            }
        }
        return nestedTagsCount;
    }

    private String deleteParsedContent(String string, int contentLength){
        return string.substring(contentLength);
    }

    private String dropInvalidTag(String string, String tagString){
        String beginTag = buildBeginTagFrom(tagString);
        return string.substring(string.indexOf(beginTag) + beginTag.length());
    }

    private boolean hasTags(String content){
        return Pattern.compile(".*<.{2,}>.*").matcher(content).matches();
    }

    private boolean isTagValid(String string, String tagString){
        String beginTag = buildBeginTagFrom(tagString);
        String enclosingTag = buildEnclosingTagFrom(tagString);

        if (string.contains(enclosingTag) &&
                (string.indexOf(beginTag) < string.indexOf(enclosingTag)) &&
                (!tagString.isBlank())){
            return true;
        }

        return false;
    }

    private String lookupNextTag(String content){
        int tagStart = content.indexOf("<") + 1;
        int tagEnd = content.indexOf(">");
        String tagString = "";

        if (tagStart > tagEnd){
            return lookupNextTag(content.substring(tagStart));
        }

        if (indexInValidRange(tagStart) && indexInValidRange(tagEnd)){
            tagString = content.substring(tagStart,tagEnd);
        }

        return tagString;
    }

    private boolean indexInValidRange(int index){
        return index >= 0;
    }
}
