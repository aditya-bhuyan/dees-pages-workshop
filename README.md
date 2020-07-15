# Follow Instructions to  Kubernetise the Pages Application

### Running the image in Kubernetes
- Fill the pages-namespace.yaml
  * Assign *<your-first-name>* to **name**
- Fill the pages-services.yaml
  * Assign **8080** to *targetPort* and *port*
  * Assign **TCP** to *protocol*
  * Assign the value **pages** to *app*, *servicefor* and *name*
  * In metadata section add *namespace: \<your-name>*
- Fill the pages-deployment.yaml
  * Assign the value **pages** to *app*, *servicefor* and *name* 
  * Assign proper values for \<docker_username>/pages:kube for value of image.
  * In metadata section add *namespace: \<your-name>* 
- In all the above section replace \<your-name> with your own **first name**.
#### Instructions for the  application to be deployed in minikube for local testing
*minikube* and *kubectl* must be installed in the local machine vm. To start *minikube* "minikube start" or "minikube start --driver=virtualbox/docker" could be executed in the terminal.
- Run the following commands in terminal to  run the application in minikube
```shell script
kubectl -f deployment/pages-namespace.yaml
kubectl -f deployment/pages-service.yaml
kubectl -f deployment/pages-deployment.yaml
```
- The above commands will create a namespace in the name \<your-name> and a deployment, service called pages under the same namespace.
Verify the namespace, deployment and service are created by using the following command
```shell script
kubectl get deployment pages --namespace <your-name>
kubectl get service pages --namespace <your-name>
```
- Below command would set your default namespace to the new namespace and we don't need to suffix --namespace in all commands
```shell script
kubectl config set-context --current --namespace=<your-name>
```
- Execute the command "minikube service pages --namespace \<your-name>" and it would open the application in the browser.


#### Instructions for the  application to be deployed in Enterprise PKS
- **pks cli** must have been installed already 
- Receive the **pks-username**, **pks-password**, **pks-cluster-name** and **pks-api-name**.
- Execute the below command to login to the PKS 
```shell script
pks login -a pks-api-name  pks-cluster-name -u pks-username -k -p pks-password
```
- Execute the below command to enter the pks-cluster
```shell script
pks get-credentials pks-cluster-name
```
Now we are good to execute our commands in the PKS cluster
- Execute the below commands to create namespace, deployment and service in pks cluster
```shell script
kubectl -f deployment/pages-namespace.yaml
kubectl -f deployment/pages-service.yaml
kubectl -f deployment/pages-deployment.yaml
```
- Verify the namespace, deployment and service are created by using the following command in the pks cluster
```shell script
kubectl get deployment pages --namespace <your-name>
kubectl get service pages --namespace <your-name>
```
- Below command would set your default namespace to the new namespace and we don't need to suffix --namespace in all commands
```shell script
kubectl config set-context --current --namespace=<your-name>
```
- Identify the external ip of the **pages** service from the output of "kubectl get service pages" command.
- Open the pages url in http://\<external-ip>:8080 to test the application

- Push your code to github