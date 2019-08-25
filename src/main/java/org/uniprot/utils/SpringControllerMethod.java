package org.uniprot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

/**
 * @author tedleman
 */
public class SpringControllerMethod {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringControllerMethod.class);

    private Class<?> controllerClass;
    private Method method;
    private String resourceName;
    private String resourceKey;
    private String description;
    private String operationPath;
    private RequestMethod requestMethod;

    /**
     *
     * @param clazz        Controller class
     * @param method        A Controller method
     * @param resourceName resource Name
     * @param resourceKey key containing the controller package, class controller class name, and controller-level @RequestMapping#value and possibly method level request mapin
     */
    public SpringControllerMethod(Class<?> clazz, Method method, String resourceName, String resourceKey) {
        this.controllerClass = clazz;
        this.resourceName = resourceName;
        this.resourceKey = resourceKey;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getOperationPath(){
        return this.operationPath;
    }

    public void setOperationPath(String operationPath){
        this.operationPath = operationPath;
    }

    public RequestMethod getRequestMethod(){
        return this.requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod){
        this.requestMethod = requestMethod;
    }

}
