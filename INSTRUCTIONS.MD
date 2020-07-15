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
------------------
Dockerization Solution Ends
---------------------
Configuration Starts
---------------------
# Follow Instructions to Externalize the Welcome Message

## Code Change
- Verify the *HomeControllerTest.java* file to check the change in test. The *HomeController.java* needs to be changed to pass the test case
- In *HomeController.java* create a String variable **pageContent** and intialize it through constructor using @Value annotation. Use the same variable in getPage() method to return welcome message. 
```java
package org.dell.kube.pages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    private String pageContent;

    public HomeController(@Value("${page.content}") String pageContent){
        this.pageContent=pageContent;

    }
    @GetMapping
    public String getPage(){
        return "Hello from page : "+pageContent+" ";
    }


}
```
- Initialize the variable PAGE_CONTENT in build.gradle for bootRun and test closures by adding below statements
```groovy
bootRun.environment([
		"PAGE_CONTENT": "YellowPages",
])

test.environment([
		"PAGE_CONTENT": "YellowPages",
])
```
This would supply the value for pageContent during test and bootRun gradle tasks.
- Build and Test the application using
```shell script
./gradlew clean build
./gradlew bootRun
```
### Dockerization
- Build the application using gradle
- run the following commands to generate the Docker image
```shell script
docker build -t <docker_username>/<docker_repo>:config .
``` 
- run the following command to run the image
```shell script
docker run -p 8080:8080 -t <docker_username>/<docker_repo>:config
```
Then open the application at http://localhost:8080 to test it.

- Push the image to docker
```shell script
docker push <docker_username>/<docker_repo>:config
```

### Running the image in Kubernetes
- Create a new file **pages-config.yaml* in deployment folder to initialize the variable in kubernetes. Use below content
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: pages-config-map
data:
  PAGE_CONTENT: "Green-Pages coming from Yellow-world!"
```
- In *pages-deployment.yaml* replace the image tag name with *config*
- In *pages-deployment.yaml* make changes to set the PAGE_CONTENT value reading from the ConfigMap. Add the below content inside container section
```yaml
env:
  - name: PAGE_CONTENT
    valueFrom:
      configMapKeyRef:
        name: pages-config-map
        key: PAGE_CONTENT
```
- Run the following commands in kubernetes to run the application 
```shell script
kubectl -f deployment/pages-config.yaml
kubectl -f deployment/pages-service.yaml
kubectl -f deployment/pages-deployment.yaml
```
- Verify the deployment, configMap and service are created by using the following command
```shell script
kubectl get deployment pages
kubectl get service pages
kubectl get cm pages-config-map
```
- Identify the external ip of the **pages** service
- Open the pages url in http://<external-ip>:\<nodePort> to test the application
-------------
Configuration Ends
------------------
Pipleline Starts
-----------------
# Follow Instructions to create pipeline using github actions

## Step 1
- Create the following *secrets* in your github repository. To create secrets access  the settings section of your repository and select secrets.
    * DOCKER_USERNAME
    * DOCKER_PASSWORD
    * PKS_USERNAME
    * PKS_PASSWORD
    * PKS_API
    * PKS_CLUSTER
- Create the following directory .github/workflows and create a file called pipeline.yaml under it with below content
```yaml
name: Pages Pipeline

on:
  push:
    branches: [master]

jobs:
  build-artifact:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v1
      - name: Set up JDK 11
        uses: actions/setup-java@v1
        with:
          java-version: 11
      - name: Build with Gradle
        run: |
          chmod +x ./gradlew
          ./gradlew clean build
      - name: Upload Artifact
        uses: actions/upload-artifact@v2
        with:
          name: artifact
          path: build/libs/pages.jar

      - name: build-docker-image
        uses: docker/build-push-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          repository: <docker-user-name>/pages
          tags: <tag-name>
  deploy-image-to-pks:
    needs: build-artifact
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v1
      - name: Install Pivnet & PKS
        run: |
          sudo apt-get update
          wget -O pivnet github.com/pivotal-cf/pivnet-cli/releases/download/v0.0.55/pivnet-linux-amd64-0.0.55 && chmod +x pivnet && sudo mv pivnet /usr/local/bin
          pivnet login --api-token=cd8d86d24afa43b9bd989012adcbf3f5-r
          pivnet download-product-files --product-slug='pivotal-container-service' --release-version='1.7.0' --product-file-id=646536
          sudo mv pks-linux-amd64-1.7.0-build.483 pks
          chmod +x pks
          sudo mv pks /usr/local/bin/
      - name: Install Kubectl
        run: |
          pivnet download-product-files --product-slug='pivotal-container-service' --release-version='1.7.0' --product-file-id=633728
          sudo mv  kubectl-linux-amd64-1.16.7 kubectl
          sudo mv kubectl /usr/local/bin/
      - name: PKS Login
        run: |
          pks login -a ${{ secrets.PKS_API }}  ${{ secrets.PKS_CLUSTER }} -u ${{ secrets.PKS_USERNAME }} -k -p ${{ secrets.PKS_PASSWORD }}
          pks get-credentials pks-workshop
          kubectl apply -f deployment/pages-namespace.yaml
          kubectl apply -f deployment/pages-config.yaml
          kubectl apply -f deployment/pages-service.yaml
          kubectl apply -f deployment/pages-deployment.yaml





    #1 - Install Pivnet
    #2 - Install PKS CLI
    #3 - Install kubectl
    #4 - Login to pks
    #5 - Set the kubectl context to target pks cluster
    #6 - Create ConfigMap/Secrets
    #7 - Create Service
    #8-  Create Deployment
```
- Replace the **tag-name** and **docker-user-name** with proper *tag-name* and your own docker *user name*.
- Steps to create pivnet token as per your own account and put it in pipeline.yaml
```text
1. Create an account using the link https://account.run.pivotal.io/z/uaa/sign-up
2. Check your inbox and verify email, so that you can sign in successfully.
3. Access https://network.pivotal.io/users/dashboard/edit-profile
4. Create an API token and copy it.
5. Update the token in the pages/.github/workflows/pipeline.yaml file under the workflow deploy-image-to-pks section for the below line
    pivnet login --api-token=<paste-your-token-here>
```
- Push your code to git repository and wait till git actions starts the build and deploys to PKS cluster
- Verify your objects in pks cluster and access the service in browser as mentioned in the previous lab
-------------
Pipeline Ends
-------------