package org.uniprot.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author tedleman
 */
public class SpringResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringResource.class);

    private Class<?> controllerClass;
    private List<Method> methods;
    private String controllerMapping; //FIXME should be an array
    private String resourceName;
    private String resourceKey;
    private String description;
    private String operationPath;
    private Set<RequestMethod> requestMethods;// can we have more than one method?

    /**
     *
     * @param clazz        Controller class
     * @param resourceName resource Name
     * @param resourceKey key containing the controller package, class controller class name, and controller-level @RequestMapping#value
     * @param description description of the contrroller
     */
    public SpringResource(Class<?> clazz, String resourceName, String resourceKey, String description) {
        this.controllerClass = clazz;
        this.resourceName = resourceName;
        this.resourceKey = resourceKey;
        this.description = description;
        this.methods = new ArrayList<>();

        String[] controllerRequestMappingValues = SpringUtils.getControllerResquestMapping(this.controllerClass);

        this.controllerMapping = StringUtils.removeEnd(controllerRequestMappingValues[0], "/");
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public void addMethod(Method m) {
        this.methods.add(m);
    }

    public String getControllerMapping() {
        return controllerMapping;
    }

    public void setControllerMapping(String controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResource(String resource) {
        this.resourceName = resource;
    }

    public String getResourcePath() {
        return "/" + resourceName;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationPath(){
        return this.operationPath;
    }

    public void setOperationPath(String operationPath){
        this.operationPath = operationPath;
    }

    public Set<RequestMethod> getRequestMethods(){
        return this.requestMethods;
    }

    public void setRequestMethods(Set<RequestMethod> requestMethods){
        this.requestMethods = requestMethods;
    }

    public void addRequestMethod(RequestMethod requestMethod){
        if(this.requestMethods == null){
            this.requestMethods = new HashSet<>();
        }
        this.requestMethods.add(requestMethod);
    }

}
