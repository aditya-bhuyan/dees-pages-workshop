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
