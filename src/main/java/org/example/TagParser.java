package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class TagParser {

    public List<String> parseTaggedTextFrom(String textLine) {
        List<String> parsedText = getTaggedContent(textLine);

        if (parsedText.isEmpty()) {
            parsedText.add("None");
        }

        return parsedText;
    }

    private List<String> getTaggedContent(String string) {
        return getTaggedContent(string, new ArrayList<>());
    }

    private List<String> getTaggedContent(String string, List<String> resultText) {
        if (string.length() > 0 && hasTags(string)) {

            Tag tag = lookupNextTag(string);

            if (isTagValid(string, tag)) {
                String parsedText = getTaggedText(string, tag);

                if (hasTags(parsedText)) {
                    resultText = getTaggedContent(parsedText, resultText);
                } else if (parsedText.length() > 0) {
                    resultText.add(parsedText);
                }

                string = removeParsedTextFrom(string, tag.tagText(parsedText));

            } else {
                string = dropInvalidTag(string, tag);
            }

        } else {
            return resultText;
        }

        return getTaggedContent(string, resultText);
    }

    private String getTaggedText(String content, Tag tag) {
        String beginTag = tag.begin();
        String enclosingTag = tag.enclosing();

        int beginIndex = content.indexOf(beginTag) + beginTag.length();
        int firstEnclosingTagIndex = content.indexOf(enclosingTag, beginIndex);
        int numberOfNestedTags = countNestedTagsIn(content.substring(beginIndex, firstEnclosingTagIndex), tag);
        int enclosingIndex = getEnclosingTagIndex(content, beginIndex, enclosingTag, numberOfNestedTags);
        return content.substring(beginIndex, enclosingIndex);
    }

    private int getEnclosingTagIndex(String string, int beginIndex, String enclosingTag, int numberOfNestedTags) {
        int enclosingTagIndex = string.indexOf(enclosingTag, beginIndex);

        if (numberOfNestedTags == 0) {
            if (enclosingTagIndex < 0) {
                enclosingTagIndex = beginIndex - enclosingTag.length();
            }
            return enclosingTagIndex;
        }

        return getEnclosingTagIndex(string, enclosingTagIndex + enclosingTag.length(), enclosingTag, numberOfNestedTags - 1);
    }

    private int countNestedTagsIn(String string, Tag tag) {
        int nestedTagsCount = 0;
        while (string.length() > 0 && hasTags(string)) {
            Tag nestedTag = lookupNextTag(string);

            if (nestedTag.equals(tag)) {
                string = string.substring(string.indexOf(nestedTag.begin()) + tag.begin().length());
                nestedTagsCount++;
            } else {
                string = dropInvalidTag(string, nestedTag);
            }
        }

        return nestedTagsCount;
    }

    private String removeParsedTextFrom(String string, String parsedText) {
        int fromIndex = string.indexOf(parsedText) + parsedText.length();
        return string.substring(fromIndex);
    }

    private String dropInvalidTag(String string, Tag tag) {
        String beginTag = tag.begin();
        return string.substring(string.indexOf(beginTag) + beginTag.length());
    }

    private boolean hasTags(String string) {
        return Pattern.compile(".*<.{2,}>.*").matcher(string).matches();
    }

    private boolean isTagValid(String string, Tag tag) {
        String beginTag = tag.begin();
        String enclosingTag = tag.enclosing();

        if (string.contains(enclosingTag) &&
                (string.indexOf(beginTag) < string.indexOf(enclosingTag)) &&
                (!tag.isEmpty())) {
            return true;
        }

        return false;
    }

    private Tag lookupNextTag(String string) {
        int tagStart = string.indexOf("<") + 1;
        int tagEnd = string.indexOf(">");
        String tagString = "";

        if (tagStart > tagEnd) {
            return lookupNextTag(string.substring(tagStart));
        }

        if (indexInValidRange(tagStart) && indexInValidRange(tagEnd)) {
            tagString = string.substring(tagStart, tagEnd);
        }

        return Tag.of(tagString);
    }

    private boolean indexInValidRange(int index) {
        return index >= 0;
    }

    public static class Tag {
        private String tagString;

        private Tag(String tagString) {
            this.tagString = tagString;
        }

        public static Tag of(String tagString) {
            return new Tag(tagString);
        }

        public String tagText(String text) {
            return begin() + text + enclosing();
        }

        public boolean isEmpty() {
            return tagString.isEmpty();
        }

        public String begin() {
            return "<" + tagString + ">";
        }

        public String enclosing() {
            return "</" + tagString + ">";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tag tag = (Tag) o;
            return Objects.equals(tagString, tag.tagString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tagString);
        }
    }
}
