package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class RequestBodyAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testGeneralRequestBody() throws Exception {
        // generate OAS for the given package
        generateOAS3( "request-body-general.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.general");

        // compare the result
        compareYamlFiles("request-body-general.yaml");
    }

    @Test
    public void testRequestBodyAroundMethod() throws Exception {
        // generate OAS for the given package
        generateOAS3( "request-body-method.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.onlymethod");

        // compare the result
        compareYamlFiles("request-body-method.yaml");
    }

    @Test
    public void testRequestBodyAroundMethodAndParam() throws Exception {
        // generate OAS for the given package
        generateOAS3( "request-body-method-param.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.methodandparam");

        // compare the result
        compareYamlFiles("request-body-method-param.yaml");
    }

}
