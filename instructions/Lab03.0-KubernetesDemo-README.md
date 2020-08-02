# Follow Instructions to demonstrate the use of Deployment, ReplicaSet and Pod

## Demonstration
- Create a folder called *logs* under */usr* using below command
```shell script
sudo mkdir -p /usr/logs
sudo chmod -R 777 /usr/logs
sudo chown -R -$USER /usr/logs
```
- Start minikube in VM using the below command. Use the argument **driver** only if minikube fails to start without it.The value for argument **driver** could be docker/virtualbox. 
```shell script
minikube start --mount=true --mount-string=/usr/logs:/usr/logs --driver=docker/virtualbox
```
- We need to create a namespace and create all the objects in the namespace. First open the deployment-pages-namespace.yaml and change the value of name attribute to your firstname. Then execute the below command.
```shell script
kubectl -f apply deployment/demo-pages-namespace.yaml
kubectl config set-context --current --namespace=<your-first-name>
```
- Change the value of namespace field in all the files of deployment folder to your first name
- Execute the below command
```shell script
kubectl apply -f deployment/demo-pages-deploy-vol.yaml
```
- This would create a PersistentVolume, PersistentVolumeClaim and a deployment. The deployment would create two pods with name pages-\<some-name>-\<some-name>. Verify the objects by using below commands one by one.
```shell script
kubectl get pv
kubectl get pvc
kubectl get deploy
kubectl get pod
```
The Pods may take some time to be running stage.
- Create an independent Pod called **pages** by using the below command and verify its creation also. This time th epod would be ready soon.
```shell script
kubectl apply -f deployment/demo-pages-pod.yaml
kubectl get po pages
``` 
- Delete a pod created by pages deployment using following command and verify the status
```shell script
kubectl delete po pages-<some-name>-<some-name>
kubectl get po
```
You will find that the old pod is terminated or terminating stage and a new pod is getting created/already created. This is because the deployment is always maintaining two pod.
Now delete the orphan pod called pages using the below command and check the status.
```shell script
kubectl delete po pages
kubectl get po
```
You would find that the standalone pod **pages** is deleted but no new pod is coming up. It is beacuse it is an orphan pod is not maintained by the deployment.
- Check the log files of the application created in "/usr/logs" using below command
```shell script
ls -l /usr/logs
```
Verify from the output that 2 log files had been created.
- Create a NodePort service using the below command.
```shell script
kubectl apply -f demo-pages-service.yaml
```
This would create a NodePort service called **pages**. Verify the service creation using below command.
```shell script
kubectl get service pages
```
- Execute the below command to open the application url in browser
```shell script
minikube service pages -n <your-name>
```
This command will open the application url in browser. Through the swagger documentation execute the REST APIs.
- Delete everything by using the below commands.
```shell script
kubectl delete pvc log-persistent-claim
kubectl delete -f deployment/demo-pages-deploy-vol.yaml
kubectl delete service pages
kubectl delete ns <your-name>
```
- Execute the below commands to push your code to remote github repository.
```shell script
git add .
git commit -m "commit_message"
git push origin kubernetes-demo-work:master -f
```
- The below command will checkout the required tag for next lab
```shell script
git checkout kubernetes-start -b kubernetes-work
```