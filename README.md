## Instruction to start the *Hello Application* from scratch
- Download the project zip file from [here](https://dell-edu-lab-store.s3.ap-south-1.amazonaws.com/repository/pages.zip) and extract it inside workspace folder
- Create a repository in git with the name **pages**. Keep everything default, while creating the repository, don't change anything other than default.
- Copy the *git remote add origin <repo address>* command from the landing page and execute it in the directory 
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



### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
out/

```
- Open the project in Intellij Idea, select the import gradle project option in bottom right corner and  set project SDK to JDK 11
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

public class HomeControllerTests {
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
- Create a *settings.gradle* file with below content in the root project
```groovy
rootProject.name = 'pages'
```
- Push the content to the local git repository
```shell
git add .
git commit -m "MESSAGE"
```
- For the current monolith app there are 8 labs. The lab could be started by cherry-pick a tag. All the tag names are provided in sequence. The tag name would be [lab-name]-start. For each lab there would be a file containing instructions to carry out  under *instructions* directory. The instruction files follow a naming convention as *Lab[number]-[Lab Description]-README.md*. For example for docker lab which is lab number 2 the instruction file name would be instructions/*Lab02-Docker-README.md*. The instruction file would be added to the local repository once we cherry-pick the tag.
For each lab we have to cherry-pick the tag into master branch.  The tags needs to be cherry-pick as per the sequence mentioned in the below table.

    | SL No  | Lab Name                      |Git Tag Name        | Instruction File Name           | Comment                     |
    |--------|-------------------------------|--------------------|---------------------------------|----------------------------|
    | 1      | Hello                         |*hello-start*       |Lab01-Hello-README.md            |Initial App                 |
    | 2      | Docker                        |*docker-start*      |Lab02-Docker-README.md           |Docker                      |
	| 3.0    | Kubernetes Presentation       |*kubernetes-demo*   |Lab03.0-KubernetesDemo-README.md |Demo of Kubernetes Objects  |
	| 3      | Kubernetes                    |*kubernetes-start*  |Lab03-Kubernetes-README.md       |Kubernetes Deployments      |
	| 4      | Externalizing Configuration   |*config-start*      |Lab04-Config-README.md           |Using Configuration         |
	| 5      | Pipeline                      |*pipeline-start*    |Lab05-Pipeline-README.md         |Using Github Actions        |
	| 6      | Logging and Probing           |*log-start*         |Lab06-Logging-README.md          |Logging and Probing in k8s  |
	| 7      | Inmemory Persistence          |*inmemory-start*    |Lab07-Inmemory-README.md         |Inememory Repository        |
	| 8      | MySQL Persistence             |*persistence-start* |Lab08-Persistence-README.md      |MySQL Repository            |
  
For example if we have to check out *docker-start* tag we need to use the below command.
```sh
git cherry-pick docker-start
```
This cherry-pick would pick  a new file instructions/*Lab02-Docker-README.md* which contains instructions for the lab **Hello**. We need to implement the instructions mentioned in the file. Once the lab is completed we need to commit and push the code using below command.
```sh
git add .
git commit -m "MESSAGE"
git push origin master 
```
The same needs to be repeated for each lab. 

- Now to start with, cherry-pick **hello-start** tag so that you will get instructions/Lab01-Hello-README.md file. The command to cherry-pick the tag into the branch would be as below.
```sh
git cherry-pick hello-start 
```



