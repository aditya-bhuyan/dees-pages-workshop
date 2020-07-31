# Follow Instructions to demonstrate the use of Deployment, ReplicaSet and Pod

## Demonstration
- Start minikube 
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
You will find that the old pod is terminated or terminating and a new pod is getting created. This is because the deployment is always maintaining two pod.
Now delete the orphan pod called pages using the below command and check the status.
```shell script
kubectl delete po pages
kubectl get po
```
You would find that the pod pages is deleted but no new pod is coming up. It is beacuse it is an orphan pod is not maintained by the deployment.
Delete everything by using the below command.
```shell script
kubectl delete -f deployment/demo-pages-deploy-vol.yaml
```
