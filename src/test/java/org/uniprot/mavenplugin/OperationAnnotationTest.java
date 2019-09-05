package org.uniprot.mavenplugin;

import org.junit.Test;

public class OperationAnnotationTest extends BaseAnnotationTest{
    @Test
    public void testExternalDocumentationResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.externaldoc", "externaldoc-resource.yaml");

        // compare the result
        compareYamlFiles("externaldoc-resource.yaml");
    }

    @Test
    public void testFullyAnnotatedesource() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.fullyannotated", "fully-annot-resource.yaml");

        // compare the result
        compareYamlFiles("fully-annot-resource.yaml");
    }

    @Test
    public void testHiddenResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.hidden", "hidden-resource.yaml");

        // compare the result
        compareYamlFiles("hidden-resource.yaml");
    }

    @Test
    public void testNotAnnottSameResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.samepath.notannot", "not-annot-same-name-resource.yaml");

        // compare the result
        compareYamlFiles("not-annot-same-name-resource.yaml");
    }

    @Test
    public void testOpWithoutAnnot() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.noannot", "operation-no-annot-resource.yaml");

        // compare the result
        compareYamlFiles("operation-no-annot-resource.yaml");
    }

    @Test
    public void testServerAnnotResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.server", "server-annot-resource.yaml");

        // compare the result
        compareYamlFiles("server-annot-resource.yaml");
    }

    @Test
    public void testControllerInheritance1() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.inheritance1", "inher1-resource.yaml");

        // compare the result
        compareYamlFiles("inher1-resource.yaml");
    }

    @Test
    public void testControllerInheritance2() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.operation.inheritance2", "inher2-resource.yaml");

        // compare the result
        compareYamlFiles("inher2-resource.yaml");
    }
}
