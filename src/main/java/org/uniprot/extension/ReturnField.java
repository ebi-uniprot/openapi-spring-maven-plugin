package org.uniprot.extension;

public class ReturnField {
    private String name;
    private boolean optional;

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
