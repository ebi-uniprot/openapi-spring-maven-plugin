package org.uniprot.mavenplugin.utils;

import org.codehaus.jettison.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class TestUtils {

    public static String yamlToJson(String yamlString) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlString);
        return new JSONObject(map).toString();
    }


}
