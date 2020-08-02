### Instructions to reach solution for the Hello Lab based on the Test Cases

- We need to create PageApplication.java and HelloController.java based on test classes
- Create a package **org.dell.kube.pages**  under *src/main/java*
- Create class PageApplication.java in the package with below content
```java
package org.dell.kube.pages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PageApplication {

	public static void main(String[] args) {
		SpringApplication.run(PageApplication.class, args);
	}
}
```
- Create HomeController.java with below content in **org.dell.kube.pages**  package
```java
package org.dell.kube.pages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {


    @GetMapping
    public String getPage(){
        return "Hello from page : YellowPages";
    }


}
```
- Add **actuator** dependency to the list of dependencies in build.gradle inside the dependencies closure
```groovy
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```
- Create application.properties files under *resources* folder under *src/test* and *src/main* folder. Mark both the resources folderes as test resources root and resources root respectively.
- Add the below content in both the properties files
```properties
spring.application.name=pages
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```
- use the below command to clean,test and build the application
```shell script
./gradlew clean build
```
- use the below command to start the application
```shell script
./gradlew bootRun
```
- Open the application @ http://localhost:8080 in the browser to test the application. Check the actuator end points @ http://localhost:8080/actuator
- Push code to github repository by using following commands
```shell script
git add .
git commit -m "MESSAGE"
git push origin hello-work:master -f
```
- To work on the next lab execute the below command
```shell script
git checkout docker-start -b docker-work
```
