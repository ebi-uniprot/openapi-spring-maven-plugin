package ebi.ac.uk.uniprot.openapi.mavenplugin;

import org.junit.Ignore;
import org.junit.Test;

public class TagAnnotationTest extends BaseAnnotationTest {
    @Ignore
    @Test//FIXME we don't support swagger annotation e.g. @Tag at class level
    public void testTagAtClassLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.tags.classlevel", "tag-class-level.yaml");

        // compare the result
        compareYamlFiles("tag-class-level.yaml");
    }

    @Test
    public void testTagAtMethodLevel() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.tags.methodlevel", "tag-method-level.yaml");

        // compare the result
        compareYamlFiles("tag-method-level.yaml");
    }

    @Test
    public void testTagInsideOperation() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.tags.insideop", "tag-in-operation.yaml");

        // compare the result
        compareYamlFiles("tag-in-operation.yaml");
    }

    @Test
    public void testTagMixed() throws Exception {
        // generate OAS for the given package
        generateOAS3("ebi.ac.uk.uniprot.openapi.mavenplugin.swagger.tags.mixed", "tag-mixed.yaml");

        // compare the result
        compareYamlFiles("tag-mixed.yaml");
    }

}
