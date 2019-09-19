package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class OperationAnnotationTest extends BaseAnnotationTest{
    @Test
    public void testExternalDocumentationResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("externaldoc-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.externaldoc");

        // compare the result
        compareYamlFiles("externaldoc-resource.yaml");
    }

    @Test
    public void testFullyAnnotatedesource() throws Exception {
        // generate OAS for the given package
        generateOAS3("fully-annot-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.fullyannotated");

        // compare the result
        compareYamlFiles("fully-annot-resource.yaml");
    }

    @Test
    public void testHiddenResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("hidden-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.hidden");

        // compare the result
        compareYamlFiles("hidden-resource.yaml");
    }

    @Test
    public void testNotAnnottSameResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("not-annot-same-name-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.samepath.notannot");

        // compare the result
        compareYamlFiles("not-annot-same-name-resource.yaml");
    }

    @Test
    public void testOpWithoutAnnot() throws Exception {
        // generate OAS for the given package
        generateOAS3( "operation-no-annot-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.noannot");

        // compare the result
        compareYamlFiles("operation-no-annot-resource.yaml");
    }

    @Test
    public void testServerAnnotResource() throws Exception {
        // generate OAS for the given package
        generateOAS3( "server-annot-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.server");

        // compare the result
        compareYamlFiles("server-annot-resource.yaml");
    }

    @Test
    public void testControllerInheritance1() throws Exception {
        // generate OAS for the given package
        generateOAS3( "inher1-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.inheritance1");

        // compare the result
        compareYamlFiles("inher1-resource.yaml");
    }

    @Test
    public void testControllerInheritance2() throws Exception {
        // generate OAS for the given package
        generateOAS3( "inher2-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.operation.inheritance2");

        // compare the result
        compareYamlFiles("inher2-resource.yaml");
    }
}
