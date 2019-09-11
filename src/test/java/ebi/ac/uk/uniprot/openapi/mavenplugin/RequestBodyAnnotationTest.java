package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class RequestBodyAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testGeneralRequestBody() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.general", "request-body-general.yaml");

        // compare the result
        compareYamlFiles("request-body-general.yaml");
    }

    @Test
    public void testRequestBodyAroundMethod() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.onlymethod", "request-body-method.yaml");

        // compare the result
        compareYamlFiles("request-body-method.yaml");
    }

    @Test
    public void testRequestBodyAroundMethodAndParam() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.requestbody.methodandparam", "request-body-method-param.yaml");

        // compare the result
        compareYamlFiles("request-body-method-param.yaml");
    }

}
