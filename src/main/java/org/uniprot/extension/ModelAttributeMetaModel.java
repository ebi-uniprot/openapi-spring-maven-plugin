package org.uniprot.extension;

import java.util.List;

public class ModelAttributeMetaModel {
    private List<QueryField> queryFields;
    private List<SortField> sortFields;
    private List<ReturnField> returnFields;

    public List<QueryField> getQueryFields() {
        return queryFields;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public List<ReturnField> getReturnFields() {
        return returnFields;
    }

    public void setQueryFields(List<QueryField> queryFields) {
        this.queryFields = queryFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public void setReturnFields(List<ReturnField> returnFields) {
        this.returnFields = returnFields;
    }

}
