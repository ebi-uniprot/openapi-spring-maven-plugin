package org.uniprot.core;

public class SearchRow {
    private String name;
    private String type;
    private String format;
    private String example;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "SearchRow{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                ", example='" + example + '\'' +
                '}';
    }
}
