package uk.ac.ebi.uniprot.openapi.mavenplugin.echo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Originally written at origin	https://github.com/kongchen/swagger-maven-plugin.git
 */

@Tag(name = "Set of simple endpoints that return whatever value you pass in")
@RequestMapping(value = "/echo", produces = {"application/json", "application/xml"})
public class EchoResource {

    // Tests for @PathVariable
    @RequestMapping(value = "/pathVariableExpectParameterName/{parameterName}", method = RequestMethod.GET)
    @Operation(summary = "")
    public String pathVariableExpectParameterName(@PathVariable String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/pathVariableExpectVariableName/{pathVariableName}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String pathVariableExpectVariableName(@PathVariable(name = "pathVariableName") String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/pathVariableExpectVariableValue/{pathVariableValue}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String pathVariableExpectVariableValue(@PathVariable(value = "pathVariableValue") String parameterName) {
        return parameterName;
    }

    // Tests for @RequestParam
    @RequestMapping(value = "/requestParamExpectParameterName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestParamExpectParameterName(@RequestParam String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/requestParamExpectParamName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestParamExpectParamName(@RequestParam(name = "requestParamName") String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/requestParamExpectParamValue", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestParamExpectParamValue(@RequestParam(value = "requestParamValue") String parameterName) {
        return parameterName;
    }

    // Tests for @RequestHeader
    @RequestMapping(value = "/requestHeaderExpectParameterName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestHeaderExpectParameterName(@RequestHeader String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/requestHeaderExpectHeaderName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestHeaderExpectHeaderName(@RequestHeader(name = "requestHeaderName") String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/requestHeaderExpectHeaderValue", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String requestHeaderExpectHeaderValue(@RequestHeader(value = "requestHeaderValue") String parameterName) {
        return parameterName;
    }

    // Tests for @CookieValue
    @RequestMapping(value = "/cookieValueExpectParameterName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String cookieValueExpectParameterName(@CookieValue String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/cookieValueExpectCookieName", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String cookieValueExpectCookieName(@CookieValue(name = "cookieValueName") String parameterName) {
        return parameterName;
    }

    @RequestMapping(value = "/cookieValueExpectCookieValue", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "")
    public String cookieValueExpectCookieValue(@CookieValue(value = "cookieValueValue") String parameterName) {
        return parameterName;
    }

//    // Tests for @RequestPart FIXME
//    @RequestMapping(value = "/requestPartExpectParameterName", method = RequestMethod.GET, produces = "application/json")
//    @Operation(summary = "")// TODO doesn't match in old code the param should be in in: "formData"
//    public String requestPartExpectParameterName(@RequestPart String parameterName) throws IOException {
//        return parameterName;
//    }
//
//    @RequestMapping(value = "/requestPartExpectPartName", method = RequestMethod.GET, produces = "application/json")
//    @Operation(summary = "")// TODO in: "formData"
//    public String requestPartExpectPartName(@RequestPart(name = "requestPartName") String parameterName) {
//        return parameterName;
//    }
//
//    @RequestMapping(value = "/requestPartExpectPartValue", method = RequestMethod.GET, produces = "application/json")
//    @Operation(summary = "")
//    public String requestPartExpectPartValue(@RequestPart(value = "requestPartValue") String parameterName) {
//        return parameterName;
//    }
}
