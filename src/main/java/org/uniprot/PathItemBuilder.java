package org.uniprot;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springframework.web.bind.annotation.RequestMethod;

public class PathItemBuilder {

    public PathItem buildPathItem(RequestMethod requestMethod, Operation operation, String operationPath, Paths paths) {
        PathItem pathItemObject;
        if (paths.containsKey(operationPath)) {
            pathItemObject = paths.get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        switch (requestMethod) {
            case POST:
                pathItemObject.post(operation);
                break;
            case GET:
                pathItemObject.get(operation);
                break;
            case DELETE:
                pathItemObject.delete(operation);
                break;
            case PUT:
                pathItemObject.put(operation);
                break;
            case PATCH:
                pathItemObject.patch(operation);
                break;
            case TRACE:
                pathItemObject.trace(operation);
                break;
            case HEAD:
                pathItemObject.head(operation);
                break;
            case OPTIONS:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
        return pathItemObject;
    }
}
