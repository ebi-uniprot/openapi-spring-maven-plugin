package uk.ac.ebi.uniprot.openapi.core;

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
                } else {
                    populateDefault(method);
                }
                break;
            case GET:
                GetMapping reqGetMappringMethod = ReflectionUtils.getAnnotation(method, GetMapping.class);
                if (reqGetMappringMethod != null) {
                    methodProduces = reqGetMappringMethod.produces();
                    methodConsumes = reqGetMappringMethod.consumes();
                } else {
                    populateDefault(method);
                }
                break;
            case DELETE:
                DeleteMapping reqDeleteMappringMethod = ReflectionUtils.getAnnotation(method, DeleteMapping.class);
                if (reqDeleteMappringMethod != null) {
                    methodProduces = reqDeleteMappringMethod.produces();
                    methodConsumes = reqDeleteMappringMethod.consumes();
                } else {
                    populateDefault(method);
                }
                break;
            case PUT:
                PutMapping reqPutMappringMethod = ReflectionUtils.getAnnotation(method, PutMapping.class);
                if (reqPutMappringMethod != null) {
                    methodProduces = reqPutMappringMethod.produces();
                    methodConsumes = reqPutMappringMethod.consumes();
                } else {
                    populateDefault(method);
                }
                break;
            default:
                populateDefault(method);
                break;
        }
    }

    private void populateDefault(Method method) {
        RequestMapping reqMappingMethod = ReflectionUtils.getAnnotation(method, RequestMapping.class);
        if (reqMappingMethod != null) {
            methodProduces = reqMappingMethod.produces();
            methodConsumes = reqMappingMethod.consumes();
        }
    }

    public String[] getAllConsumes() {
        return ArrayUtils.isNotEmpty(methodConsumes) ? methodConsumes : classConsumes;
    }

    public String[] getAllProduces() {
        return ArrayUtils.isNotEmpty(methodProduces)? methodProduces : classProduces;
    }

}
