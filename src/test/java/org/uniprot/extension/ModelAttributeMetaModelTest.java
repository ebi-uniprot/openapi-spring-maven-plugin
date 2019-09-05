package org.uniprot.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ModelAttributeMetaModelTest {

    @Test
    public void testReadModel() throws IOException {
        File file = new File("src/test/resources/parameters_meta.json");
        String jsonString = FileUtils.readFileToString(file, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        ModelAttributeMetaModel metaModel = objectMapper.readValue(jsonString, typeFactory.constructType(ModelAttributeMetaModel.class));
        Assert.assertNotNull(metaModel);
        Assert.assertNotNull(metaModel.getQueryFields());
        Assert.assertNotNull(metaModel.getSortFields());
        Assert.assertNotNull(metaModel.getReturnFields());
        Assert.assertEquals(5, metaModel.getQueryFields().size());
        Assert.assertEquals(1, metaModel.getSortFields().size());
        Assert.assertEquals(10, metaModel.getReturnFields().size());
    }
}
