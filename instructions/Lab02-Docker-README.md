# Follow Instructions to dockerize  the Pages Application

## Dockerization
- Create an account in [docker](https://hub.docker.com/) if not there already.
- Build the application using gradle command "./gradlew clean build" if not built after latest changes.
- Add the following lines in Dockerfile
```shell script
  FROM adoptopenjdk:11-jre-openj9
  ARG JAR_FILE=build/libs/*.jar
  COPY ${JAR_FILE} app.jar
  ENTRYPOINT ["java","-jar","/app.jar"]
```
- Run the following commands to generate the Docker image
```shell script
docker build -t <docker_username>/pages:<tag> .
``` 
- run the following command to run the image
```shell script
docker run -p 8080:8080 -t <docker_username>/pages:<tag>
```
Then open the application in browser @ http://localhost:8080 to test it.

- Push the image to docker
```shell script
docker push <docker_username>/pages:<tag>
```
- Use the below commands to push code to remote repository
```shell script
git add .
git commit -m "MESSAGE"
git push origin docker-work:master -f
```
- Checkout the next tag to work on the next lab
```shell script
git checkout kubernetes-demo -b kubernetes-demo-work
```
