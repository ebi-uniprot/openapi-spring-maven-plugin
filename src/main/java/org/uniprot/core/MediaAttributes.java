package org.uniprot.core;

import io.swagger.v3.core.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
/**
 * Originally written by https://github.com/springdoc
 *
 * @author Modified by sahmad
 */
public class MediaAttributes {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaAttributes.class);

    private String[] classProduces;
    private String[] classConsumes;
    private String[] methodProduces;
    private String[] methodConsumes;

    public String[] getClassProduces() {
        return classProduces;
    }

    public void setClassProduces(String[] classProduces) {
        this.classProduces = classProduces;
    }

    public String[] getClassConsumes() {
        return classConsumes;
    }

    public void setClassConsumes(String[] classConsumes) {
        this.classConsumes = classConsumes;
    }

    public String[] getMethodProduces() {
        return methodProduces;
    }

    public void setMethodProduces(String[] methodProduces) {
        this.methodProduces = methodProduces;
    }

    public String[] getMethodConsumes() {
        return methodConsumes;
    }

    public void setMethodConsumes(String[] methodConsumes) {
        this.methodConsumes = methodConsumes;
    }

    public void calculateConsumesProduces(RequestMethod requestMethod, Method method) {
        switch (requestMethod) {
            case POST:
                PostMapping reqPostMappringMethod = ReflectionUtils.getAnnotation(method, PostMapping.class);
                if (reqPostMappringMethod != null) {
                    methodProduces = reqPostMappringMethod.produces();
                    methodConsumes = reqPostMappringMethod.consumes();
                }
                break;
            case GET:
                GetMapping reqGetMappringMethod = ReflectionUtils.getAnnotation(method, GetMapping.class);
                if (reqGetMappringMethod != null) {
                    methodProduces = reqGetMappringMethod.produces();
                    methodConsumes = reqGetMappringMethod.consumes();
                }
                break;
            case DELETE:
                DeleteMapping reqDeleteMappringMethod = ReflectionUtils.getAnnotation(method, DeleteMapping.class);
                if (reqDeleteMappringMethod != null) {
                    methodProduces = reqDeleteMappringMethod.produces();
                    methodConsumes = reqDeleteMappringMethod.consumes();
                }
                break;
            case PUT:
                PutMapping reqPutMappringMethod = ReflectionUtils.getAnnotation(method, PutMapping.class);
                if (reqPutMappringMethod != null) {
                    methodProduces = reqPutMappringMethod.produces();
                    methodConsumes = reqPutMappringMethod.consumes();
                }
                break;
            default:
                RequestMapping reqMappringMethod = ReflectionUtils.getAnnotation(method, RequestMapping.class);
                if (reqMappringMethod != null) {
                    methodProduces = reqMappringMethod.produces();
                    methodConsumes = reqMappringMethod.consumes();
                }
                break;
        }
    }

    public String[] getAllConsumes() {
        return ArrayUtils.addAll(methodConsumes, classConsumes);
    }

    public String[] getAllProduces() {
        return ArrayUtils.addAll(methodProduces, classProduces);
    }

}
