package uk.ac.ebi.uniprot.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sahmad
 */
public class FileHelper {
    private static final Logger  LOGGER = LoggerFactory.getLogger(FileHelper.class);

    public static void writeToFile(String content, String directory, String fileName) throws MojoFailureException {
        File outputDir = new File(directory);

        if (outputDir.isFile()) {
            throw new MojoFailureException(String.format("OpenAPI outputDirectory [%s] must be a directory!", directory));
        }

        if (!outputDir.exists()) {
            try {
                FileUtils.forceMkdir(outputDir);
            } catch (IOException e) {
                throw new MojoFailureException(String.format("Create OpenAPI-outputDirectory[%s] failed.", directory));
            }
        }

        try {
            FileUtils.write(new File(outputDir, fileName), content, "UTF-8");
        } catch (IOException e) {
            throw new MojoFailureException("Unable to write the file", e);
        }
    }

    public static List<Map<String, Object>> readMetaList(String metaFilePath){
        List<Map<String, Object>> modelFieldMetaList = new ArrayList<>();
        if(StringUtils.isNotBlank(metaFilePath)){
            File file = new File(metaFilePath);
            try {
                String jsonString = FileUtils.readFileToString(file, "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                modelFieldMetaList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>(){});
            } catch (IOException e) {
                LOGGER.warn("Unable to read file {}", file);
            }
        }
        return modelFieldMetaList;
    }
}
