package uk.ac.ebi.uniprot.openapi.mavenplugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.uniprot.openapi.extension.ModelFieldMetaReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestModelFieldMetaReaderImpl implements ModelFieldMetaReader {
    @Override
    public List<Map<String, Object>> read(String metaFilePath) {
        List<Map<String, Object>> modelFieldMetaList = new ArrayList<>();
        if(StringUtils.isNotBlank(metaFilePath)){
            File file = new File(metaFilePath);
            try {
                String jsonString = FileUtils.readFileToString(file, "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                modelFieldMetaList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>(){});
            } catch (IOException e) {
            }
        }
        return modelFieldMetaList;
    }
}
