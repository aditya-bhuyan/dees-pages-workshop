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
  namespace: pages-<your-name>
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
kubectl apply -f deployment/pages-namespace.yaml
kubectl apply -f deployment/pages-config.yaml
kubectl apply -f deployment/pages-service.yaml
kubectl apply -f deployment/pages-deployment.yaml
```
- Verify the deployment, configMap and service are created by using the following command
```shell script
kubectl get deployment pages --namespace pages-<your-name>
kubectl get cm pages-config-map  --namespace pages-<your-name>
kubectl get service pages  --namespace pages-<your-name>
```
- Identify the external ip of the **pages** service from the above command
- Open the pages url in http://\<external-ip>:8080 to test the application