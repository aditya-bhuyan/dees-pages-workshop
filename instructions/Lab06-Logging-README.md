## Instructions for the logging and monitoring

### Creating and Updating the Volume
- Add below properties in application.properties in both test and source
```properties
logging.file.name=/var/tmp/pages-app.log
debug=true
logging.level.org.springframework.web=debug
logging.level.root=debug
```
- Code change in HomeController
  * Add a Logger from slf4j api
  * Add  debug,warn,trace,info and error messages in getPage() method
```java
package org.dell.kube.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);
    private String pageContent;

    public HomeController(@Value("${page.content}") String pageContent){
        this.pageContent=pageContent;

    }
    @GetMapping
    public String getPage(){
        logger.debug("Welcome Page Accessed");
        logger.info("Welcome Page Accessed");
        logger.trace("Welcome Page Accessed");
        logger.warn("Welcome Page Accessed");
        logger.error("Welcome Page Accessed");
        return "Hello from page : "+pageContent+" ";
    }


}
```
- Few logging properties are added in application.properties files of test and source in above step. Run your application and verify the logs
- Comment the log properties from application.properties  of both test and source.
- Add logback.xml under resources folders of both test and source with basic logging configuration for FILE and STDOUT appender
```xml
  <?xml version = "1.0" encoding = "UTF-8"?>
  <configuration>
      <include resource="org/springframework/boot/logging/logback/base.xml"/>
      <logger name="org.springframework.web" level="DEBUG"/>
      <appender name = "STDOUT" class = "ch.qos.logback.core.ConsoleAppender">
          <encoder>
              <pattern>[%d{yyyy-MM-dd'T'HH:mm:ss.sss'Z'}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
          </encoder>
      </appender>
  
      <appender name = "FILE" class = "ch.qos.logback.core.FileAppender">
          <File>/var/tmp/pages-app.log</File>
          <encoder>
              <pattern>[%d{yyyy-MM-dd'T'HH:mm:ss.sss'Z'}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
          </encoder>
      </appender>
  
      <root level = "DEBUG">
          <appender-ref ref = "FILE"/>
          <appender-ref ref = "STDOUT"/>
      </root>
  </configuration>
```
- Add log volume details in pages-deployment.yaml
  
- Add liveness and readiness probe information in pages-deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: pages
    servicefor: pages
  name: pages
  namespace: <your-name>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: pages
      servicefor: pages
  strategy: {}
  template:
    metadata:
      labels:
        app: pages
        servicefor: pages
    spec:
      volumes:
      - name: log-volume
        emptyDir: {}
      containers:
      - image: <docker-user-name>/pages:logging
        imagePullPolicy: Always
        name: pages
        ports:
          - containerPort: 8080
        env:
        - name: PAGE_CONTENT
          valueFrom:
              configMapKeyRef:
                name: pages-config-map
                key: PAGE_CONTENT
        volumeMounts:
        - name: log-volume
          mountPath: "/var/tmp/"
        readinessProbe:
          tcpSocket:
           port: 8080
          initialDelaySeconds: 150
          periodSeconds: 5
          successThreshold: 2
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 150
          periodSeconds: 5
          successThreshold: 1
        resources: {}
status: {}
```
- Replace the docker-user-name with your own docker-user-name
- Build the application with 
```sh
./gradlew clean build
```
- Docker build and push the application with tag **logging**
- Change the tag to *logging* in pages-deployment.yaml
- Use the following commands to deploy the application in Minikube kubernetes
```shell script
kubectl apply -f deployment/pages-namespace.yaml
kubectl apply -f deployment/pages-config.yaml
kubectl apply -f deployment/pages-service.yaml
kubectl delete -f deployment/pages-deployment.yaml
kubectl apply -f deployment/pages-deployment.yaml
```
- Change the value of **tags** in *pipeline.yaml* to *logging* 
- Use the below commands push the code to github repository to start the pipeline
```shell script
git add .
git commit -m "MESSAGE"
git push origin log-work:master
```
- In PKS cluster the application ready time would be delayed. The application would be ready after 150 seconds as the readiness probe would start after 150 seconds.
- Keep on checking the status of the pod which is part of the pages deployment
- After sometime though the status of the pod might be **Running**, but it might be showing **Not Ready** as it needs atleast 150 seconds to be ready.
- Execute the below command to checkout the required tag for next lab
```shell script
git checkout inmemory-start -b inmemory-work
```