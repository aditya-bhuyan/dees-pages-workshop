# Follow Instructions to  Kubernetize the Pages Application

### Running the image in Kubernetes
- Fill the pages-namespace.yaml
  * Assign *pages-<your-first-name>* to **name**
- Fill the pages-services.yaml
  * Assign **8080** to *targetPort* and *port*
  * Assign **TCP** to *protocol*
  * Assign the value **pages** to *app*, *servicefor* and *name*
  * In metadata section add *namespace: pages-<your-name>*
- Fill the pages-deployment.yaml
  * Assign the value **pages** to *app*, *servicefor* and *name* 
  * Assign proper values for <docker_username>/<docker_repo>:\<tag> for value of image.
  * In metadata section add *namespace: pages-<your-name>* 
- In all the above section replace <your-name> with your own **first name**.
- Run the following commands in kubernetes to run the application 
```shell script
kubectl -f deployment/pages-namespace.yaml
kubectl -f deployment/pages-service.yaml
kubectl -f deployment/pages-deployment.yaml
```
- Verify the namespace, deployment and service are created by using the following command
```shell script
kubectl get deployment pages --namespace pages-<your-name>
kubectl get service pages --namespace pages-<your-name>
```
The above command will create a namespace in the name pages-<your-name> and a deployment, service called pages in the namespace.
- Identify the external ip of the **pages** service from the above command.
- Open the pages url in http://<external-ip>:8080 to test the application
