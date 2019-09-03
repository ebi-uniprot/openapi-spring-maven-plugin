package org.uniprot.mavenplugin;

import org.junit.Test;

public class ApiResponseAnnotationTest extends BaseAnnotationTest {

    @Test
    public void testApiResponseAtMethodLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.responses.method", "response-method-level.yaml");

        // compare the result
        compareYamlFiles("response-method-level.yaml");
    }

    @Test
    public void testApiResponseInsideOperation() throws Exception {
        // generate OAS for the given package
        generateOAS3("org.uniprot.mavenplugin.swagger.responses.operation", "response-in-operation.yaml");

        // compare the result
        compareYamlFiles("response-in-operation.yaml");
    }

}