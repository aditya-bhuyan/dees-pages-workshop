## Instruction to start the *Hello Application* from scratch
- Download the project zip file and extract it inside workspace folder
- Create a repository in git with the name **pages**. Keep everything default, while creating the repository, don't change anything other than default.
- Copy the *git remote add origin <repo address>*  and execute it in the directory 
- Create a build.gradle file with following content

```groovy
plugins {
	id 'org.springframework.boot' version '2.3.1.RELEASE'
	id 'io.spring.dependency-management' version '1.0.9.RELEASE'
	id 'java'
}

group = 'com.example'
sourceCompatibility = '11'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}

test {
	useJUnitPlatform()
}
```
- Create the gradle ecosystem by using the following commands
```shell script
    gradle wrapper --gradle-version 6.4.1 --distribution-type all
``` 
- Create a .gitignore file with following content
```text
HELP.md
.gradle
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/main/**
!**/src/test/**

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
out/

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/

### VS Code ###
.vscode/
```
- Open the project in Intellij Idea, select the import gradle project option in buttom right corner and  set project SDK to JDK 11
- Create two folders **src/main/java** and **src/test/java** under project root directory. Mark them as sources root and test root respectively.
- Create two packages **org.dell.kube.pages** and **org.dell.kube.pagesapi** under *src/test/java*
- Create a Test class called **PagesApplicationTests.java** under package **org.dell.kube.pages** with below content
```java
package org.dell.kube.pages;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PagesApplicationTests {

	@Test
	void contextLoads() {
	}

}
```
- Create a Test class called **HomeControllerTests.java** under package **org.dell.kube.pages** with below content
```java
package org.dell.kube.pages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTest {
    private final String message = "YellowPages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController();

        assertThat(controller.getPage()).contains(message);
    }


}
```
- Create a Test class called **HomeApiTest** under the package **org.dell.kube.pagesapi** with below content
```java
package org.dell.kube.pagesapi;

import org.dell.kube.pages.PageApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = PageApplication.class, webEnvironment = RANDOM_PORT)
public class HomeApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Test
    public void readTest() {
        String body = this.restTemplate.getForObject("/", String.class);
        assertThat(body).contains("YellowPages");
    }

    @Test
    public void healthTest(){
        String body = this.restTemplate.getForObject("/actuator/health", String.class);
        assertThat(body).contains("UP");
    }
}
```
---------------------------------------
TDD Ended
---------------------------------------
TDD Solution Starts
---------------------------------------
- Create a settings.gradle file in the root project directory with below content
```groovy
rootProject.name = 'pages'
```
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
- Create HomeController.java with below content in same package
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
- open the http://localhost:8080 in the browser to test the application
-------------------
TDD Solution for Hello Ends
-------------------
Dockerization Starts
-------------------
# Follow Instructions to dockerize and Kubernetize the Pages Application

## Dockerization
- Build the application using gradle
- Add the following lines in Dockerfile
```shell script
  FROM adoptopenjdk:11-jre-openj9
  ARG JAR_FILE=build/libs/*.jar
  COPY ${JAR_FILE} app.jar
  ENTRYPOINT ["java","-jar","/app.jar"]
```
- run the following commands to generate the Docker image
```shell script
docker build -t <docker_username>/<docker_repo>:<tag> .
``` 
- run the following command to run the image
```shell script
docker run -p 8080:8080 -t <docker_username>/<docker_repo>:<tag>
```
Then open the application at http://localhost:8080 to test it.

- Push the image to docker
```shell script
docker push <docker_username>/<docker_repo>:<tag>
```

### Running the image in Kubernetes
- Fill the pages-services.yaml
  * Assign **8080** to *targetPort* and *port*
  * Assign **TCP** to *protocol*
  * Assign the value **pages** to *app*, *servicefor* and *name*
- Fill the pages-deployment.yaml
  * Assign the value **pages** to *app*, *servicefor* and *name* 
  * Assign <docker_username>/<docker_repo>:\<tag> for image
- Run the following commands in kubernetes to run the application 
```shell script
kubectl -f deployment/pages-service.yaml
kubectl -f deployment/pages-deployment.yaml
```
- Verify the deployment and service are created by using the following command
```shell script
kubectl get deployment pages
kubectl get service pages
```
- Identify the external ip of the **pages** service
- Open the pages url in http://<external-ip>:\<nodePort> to test the application

