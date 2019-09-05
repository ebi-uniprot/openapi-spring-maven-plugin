package org.uniprot.extension;

public class QueryField {
    private String name;
    private String type;
    private String format;
    private String example;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public String getExample() {
        return example;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
