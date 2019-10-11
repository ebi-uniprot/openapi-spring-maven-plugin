package uk.ac.ebi.uniprot.openapi.mavenplugin;

import org.junit.Ignore;
import org.junit.Test;

public class TagAnnotationTest extends BaseAnnotationTest {
    @Ignore
    @Test//FIXME we don't support swagger annotation e.g. @Tag at class level
    public void testTagAtClassLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3( "tag-class-level.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.classlevel");

        // compare the result
        compareYamlFiles("tag-class-level.yaml");
    }

    @Test
    public void testTagAtMethodLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3( "tag-method-level.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.methodlevel");

        // compare the result
        compareYamlFiles("tag-method-level.yaml");
    }

    @Test
    public void testTagInsideOperation() throws Exception {
        // generate OAS for the given package
        generateOAS3( "tag-in-operation.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.insideop");

        // compare the result
        compareYamlFiles("tag-in-operation.yaml");
    }

    @Test
    public void testTagMixed() throws Exception {
        // generate OAS for the given package
        generateOAS3( "tag-mixed.yaml",
                "uk.ac.ebi.uniprot.openapi.mavenplugin.swagger.tags.mixed");

        // compare the result
        compareYamlFiles("tag-mixed.yaml");
    }

}
