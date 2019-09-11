package ebi.ac.uk.uniprot.openapi.core;

import ebi.ac.uk.uniprot.openapi.utils.SpringControllerMethod;
import ebi.ac.uk.uniprot.openapi.utils.SpringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Map;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * The logic of forming tha map is taken from https://github.com/kongchen/swagger-maven-plugin
 * @author sahmad
 */
public class ControllerAnalyser {

    public Map<String, SpringControllerMethod> analyseController(Class<?> controllerClazz, Map<String, SpringControllerMethod> resourceMap) {
        // get controller level RequestMapping.value mapping
        String[] controllerRequestMappingValues = SpringUtils.getControllerResquestMapping(controllerClazz);

        // Iterate over all value attributes of the class-level RequestMapping annotation
        for (String controllerRequestMappingValue : controllerRequestMappingValues) {
            for (Method method : controllerClazz.getMethods()) {
                // Skip methods introduced by compiler
                if (method.isSynthetic()) {
                    continue;
                }

                RequestMapping methodRequestMapping = findMergedAnnotation(method, RequestMapping.class);

                // Look for method-level @RequestMapping annotation
                if (methodRequestMapping != null) {
                    // get type of HTTP method
                    RequestMethod[] requestMappingRequestMethods = methodRequestMapping.method();

                    // For each method-level @RequestMapping annotation, iterate over HTTP Verb
                    for (RequestMethod requestMappingRequestMethod : requestMappingRequestMethods) {
                        String[] methodRequestMappingValues = methodRequestMapping.value();

                        // Check for cases where method-level @RequestMapping#value is not set, and use the controllers @RequestMapping
                        if (methodRequestMappingValues.length == 0) {
                            // The map key is a concat of the following:
                            //   1. The controller package
                            //   2. The controller class name
                            //   3. The controller-level @RequestMapping#value
                            //   4. The HTTP Method Type e.g. GET
                            String resourceKey = controllerClazz.getCanonicalName() + controllerRequestMappingValue + requestMappingRequestMethod;
                            if (!resourceMap.containsKey(resourceKey)) {
                                SpringControllerMethod springControllerMethod = new SpringControllerMethod(controllerClazz, method, controllerRequestMappingValue, resourceKey);
                                String operationPath = controllerRequestMappingValue;
                                springControllerMethod.setOperationPath(operationPath);
                                springControllerMethod.setRequestMethod(requestMappingRequestMethod);
                                resourceMap.put(resourceKey, springControllerMethod);
                            }
                        } else {
                            // Here we know that method-level @RequestMapping#value is populated, so
                            // iterate over all the @RequestMapping#value attributes, and add them to the resource map.
                            for (String methodRequestMappingValue : methodRequestMappingValues) {
                                // The map key is a concat of the following:
                                //   1. The controller package
                                //   2. The controller class name
                                //   3. The controller-level @RequestMapping#value
                                //   4. The method level @RequestMapping#value
                                //   5. The HTTP Method Type e.g. GET
                                String resourceKey = controllerClazz.getCanonicalName() + controllerRequestMappingValue
                                        + methodRequestMappingValue + requestMappingRequestMethod;
                                if (!(controllerRequestMappingValue + methodRequestMappingValue).isEmpty()) {
                                    if (!resourceMap.containsKey(resourceKey)) {
                                        SpringControllerMethod springControllerMethod = new SpringControllerMethod(controllerClazz, method,
                                                methodRequestMappingValue, resourceKey);

                                        String operationPath = controllerRequestMappingValue + methodRequestMappingValue;
                                        springControllerMethod.setOperationPath(operationPath);
                                        springControllerMethod.setRequestMethod(requestMappingRequestMethod);
                                        resourceMap.put(resourceKey, springControllerMethod);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return resourceMap;
    }
}
