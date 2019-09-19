package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ApiResponseAnnotationTest extends BaseAnnotationTest {

    @Test
    public void testApiResponseAtMethodLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3("response-method-level.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.responses.method");

        // compare the result
        compareYamlFiles("response-method-level.yaml");
    }

    @Test
    public void testApiResponseInsideOperation() throws Exception {
        // generate OAS for the given package
        generateOAS3( "response-in-operation.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.responses.operation");

        // compare the result
        compareYamlFiles("response-in-operation.yaml");
    }

}
