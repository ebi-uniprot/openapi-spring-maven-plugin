package ebi.ac.uk.uniprot.openapi.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ModelAttributeMetaModelTest {

    @Test
    public void testReadModel() throws IOException {
        File file = new File("src/test/resources/query_param_meta.json");
        String jsonString = FileUtils.readFileToString(file, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> paramMetaList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>(){});
        Assert.assertNotNull(paramMetaList);
//        TypeFactory typeFactory = objectMapper.getTypeFactory();
//        ModelAttributeMetaModel metaModel = objectMapper.readValue(jsonString, ModelAttributeMetaModel.class);
//        Assert.assertNotNull(metaModel);
//        Assert.assertNotNull(metaModel.getQueryFields());
//        Assert.assertNotNull(metaModel.getSortFields());
//        Assert.assertNotNull(metaModel.getReturnFields());
//        Assert.assertEquals(5, metaModel.getQueryFields().size());
//        Assert.assertEquals(1, metaModel.getSortFields().size());
//        Assert.assertEquals(10, metaModel.getReturnFields().size());
    }
}
