[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.ac.ebi.uniprot/openapi-maven-plugin/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/uk.ac.ebi.uniprot/openapi-maven-plugin)

# Swagger Maven Plugin for Spring MVC

This maven plugin generates Open Specification 3.0.1 (OAS3) for your Spring Rest Project. It supports most of the Swagger 2+ and Spring Web annotation. It is simple to use and generates the OAS3 specifaction in yaml format. Please note that it is the first release so there are few bugs, we will fix them.    

#Prerequisite
* Maven >= 3.3.9
* Java 8

# Features

* Supports [Swagger Spec 2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md)
* Supports [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)
* Quickly generates  openapi3.yaml by command `mvn compile`

# Versions
- [0.1] supports Swagger Spec [2.0] SpingMVC.


# Usage/Example
Import the plugin in your project by adding following configuration in your `plugins` block of pom file:


```xml
<build>
    <plugins>
        <plugin>
            <groupId>uk.ac.ebi.uniprot</groupId>
            <artifactId>openapi-maven-plugin</artifactId>
            <version>0.3</version>
            <configuration>
                <packageLocations>
			<packageLocation>your.package.with.spring.controllers</packageLocation>
	        	<packageLocation>your.package.with.spring.controllers</packageLocation>
                </packageLocations>
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
| `packageLocations` | Mandatory param to pass the `packageLocation` name(s) where Spring Controllers are.  |
| `openApiDirectory` | Path of the directory where you want to generate the Open API Specification yaml file. Defaults to `target/generated-sources/swagger/` |
| `openApiFileName` | Name of the Open API Specification yaml file. Defaults to `openapi3.yaml` |
| `serverBaseUrl` | Server URL. Default to `localhost` |

#Build the source 
* `mvn clean install`
* `mcn clean install -DskipTests` -- To skip the test

#Dependency conflicts 
If you face any dependency conflict after adding the plugin, use the below command to exclude duplicate dependency:

`mvn dependency:tree`

An example to exclude `slf4j-log4j12` :

```xml
        <dependency>
            <groupId>uk.ac.ebi.uniprot</groupId>
            <artifactId>openapi-maven-plugin</artifactId>
            <version>${openapi-maven-plugin.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

# Credit
The project uses codes or inspires from the codes from the below 3 projects. Thanks to them for writing such code.

##### 1.  https://github.com/springdoc/springdoc-openapi
##### 2.  https://github.com/kongchen/swagger-maven-plugin
##### 3.  https://github.com/swagger-api/swagger-core


