# Follow Instructions to  Kubernetize the Pages Application

### Running the image in Kubernetes
- Fill the pages-services.yaml
  * Assign **8080** to *targetPort* and *port*
  * Assign **TCP** to *protocol*
  * Assign the value **pages** to *app*, *servicefor* and *name*
- Fill the pages-deployment.yaml
  * Assign the value **pages** to *app*, *servicefor* and *name* 
  * Assign <docker_username>/<docker_repo>:\<tag> for value of image. 
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
