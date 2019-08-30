# Swagger Maven Plugin for Spring MVC

This maven plugin generates Open Specification 3.0.1 (OAS3) for your Spring Rest Project. It supports most of the Swagger 2+ and Spring Web annotation. It is simple to use and generates the OAS3 specifaction in yaml format.    

# Features

* Supports [Swagger Spec 2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md)
* Supports [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)
* Quickly generates  openapi3.yaml by command `mvn compile`

# Versions
- [1.0-SNAPSHOT] supports Swagger Spec [2.0] SpingMVC.


# Usage/Example
Import the plugin in your project by adding following configuration in your `plugins` block of pom file:


```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.uniprot</groupId>
            <artifactId>openapi-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <packageToScan>your.package.with.spring.controllers</packageToScan>
                <openApiDirectory>generated/swagger</openApiDirectory>
                <openApiFileName>openapi.yaml</openApiFileName>
                <serverBaseUrl>http://localhost/</serverBaseUrl>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>oas-generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

# Configuration for `configuration`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `packageToScan` | Mandatory param to pass the package name where Spring Controllers are.  |
| `openApiDirectory` | Path of the directory where you want to generate the Open API Specification yaml file. Defaults to `target/generated-sources/swagger/` |
| `openApiFileName` | Name of the Open API Specification yaml file. Defaults to `openapi3.yaml` |
| `serverBaseUrl` | Server URL. Default to `localhost` |

# Credit
The project uses codes or inspires from the codes from the below 3 projects. Thanks to them for writing such code.

##### 1.  https://github.com/springdoc/springdoc-openapi
##### 2.  https://github.com/kongchen/swagger-maven-plugin
##### 3.  https://github.com/swagger-api/swagger-core


