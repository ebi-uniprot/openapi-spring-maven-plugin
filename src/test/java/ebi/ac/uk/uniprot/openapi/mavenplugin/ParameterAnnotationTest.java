package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ParameterAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testArraySchemaResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.arrayschema", "param-array-schema.yaml");

        // compare the result
        compareYamlFiles("param-array-schema.yaml");
    }

    @Test
    public void testParamResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.resource", "param-resource.yaml");

        // compare the result
        compareYamlFiles("param-resource.yaml");
    }

    @Test
    public void testParamRepeatResource() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.repeatable", "param-repeat-resource.yaml");

        // compare the result
        compareYamlFiles("param-repeat-resource.yaml");
    }

}
