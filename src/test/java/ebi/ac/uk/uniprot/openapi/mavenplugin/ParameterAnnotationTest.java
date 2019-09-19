package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Test;

public class ParameterAnnotationTest extends BaseAnnotationTest {
    @Test
    public void testArraySchemaResource() throws Exception {
        // generate OAS for the given package
        generateOAS3( "param-array-schema.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.arrayschema");

        // compare the result
        compareYamlFiles("param-array-schema.yaml");
    }

    @Test
    public void testParamResource() throws Exception {
        // generate OAS for the given package
        generateOAS3( "param-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.resource");

        // compare the result
        compareYamlFiles("param-resource.yaml");
    }

    @Test
    public void testParamRepeatResource() throws Exception {
        // generate OAS for the given package
        generateOAS3( "param-repeat-resource.yaml",
                "ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.parameter.repeatable");

        // compare the result
        compareYamlFiles("param-repeat-resource.yaml");
    }

}
