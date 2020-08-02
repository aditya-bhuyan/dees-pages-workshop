# Follow Instructions to create pipeline using github actions

## Step 1
- Create the following *secrets* in your github repository. To create secrets access  the settings section of your repository and select secrets.
    * DOCKER_USERNAME [Your own docker account user name]
    * DOCKER_PASSWORD [Your own docker account login password ]
    * PKS_API [Get it from the instructor]
    * PKS_USERNAME [Get it from the instructor]
    * PKS_PASSWORD [Get it from the instructor]
    * PKS_CLUSTER [Get it from the instructor]
    * PKS_TOKEN [Follow the below steps to create it]
- Steps to create pivnet token as per your own account and put it in pipeline.yaml
```text
1. Create an account using the link https://account.run.pivotal.io/z/uaa/sign-up if pivotal account is not there already
2. Check your inbox and verify email, so that you can sign in successfully.
3. Access https://network.pivotal.io/users/dashboard/edit-profile
4. Create an API token and copy it and assign it to PKS_TOKEN
```
- Create the following directory .github/workflows and create a file called pipeline.yaml under it with below content
```yaml
name: Pages Pipeline

on:
  push:
    branches: [master]

jobs:
  build-artifact:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v1
      - name: Set up JDK 11
        uses: actions/setup-java@v1
        with:
          java-version: 11
      - name: Build with Gradle
        uses: eskatos/gradle-command-action@v1
        with:
          arguments: build
          gradle-version: 6.4.1
      - name: Upload Artifact
        uses: actions/upload-artifact@v2
        with:
          name: artifact
          path: build/libs/pages.jar

      - name: build-docker-image
        uses: docker/build-push-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          repository: <docker-user-name>/pages
          tags: <tag-name>
  deploy-image-to-pks:
    needs: build-artifact
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v1
      - name: Install Pivnet & PKS
        run: |
          sudo apt-get update
          wget -O pivnet github.com/pivotal-cf/pivnet-cli/releases/download/v0.0.55/pivnet-linux-amd64-0.0.55 && chmod +x pivnet && sudo mv pivnet /usr/local/bin
          pivnet login --api-token=${{ secrets.PKS_TOKEN }}
          pivnet download-product-files --product-slug='pivotal-container-service' --release-version='1.7.0' --product-file-id=646536
          sudo mv pks-linux-amd64-1.7.0-build.483 pks
          chmod +x pks
          sudo mv pks /usr/local/bin/
      - name: Install Kubectl
        run: |
          pivnet download-product-files --product-slug='pivotal-container-service' --release-version='1.7.0' --product-file-id=633728
          sudo mv  kubectl-linux-amd64-1.16.7 kubectl
          sudo mv kubectl /usr/local/bin/
      - name: PKS Login
        run: |
          pks login -a ${{ secrets.PKS_API }}   -u ${{ secrets.PKS_USERNAME }} -k -p ${{ secrets.PKS_PASSWORD }}
          pks get-credentials ${{ secrets.PKS_CLUSTER }}
          kubectl apply -f deployment/pages-namespace.yaml
          kubectl apply -f deployment/pages-config.yaml
          kubectl apply -f deployment/pages-service.yaml
          kubectl delete -f deployment/pages-deployment.yaml
          kubectl apply -f deployment/pages-deployment.yaml





    #1 - Install Pivnet
    #2 - Install PKS CLI
    #3 - Install kubectl
    #4 - Login to pks
    #5 - Set the kubectl context to target pks cluster
    #6 - Create ConfigMap/Secrets
    #7 - Create Service
    #8-  Create Deployment
```
- Replace the **tag-name** and **docker-user-name** with proper *tag-name* and your own docker *user name*.

- Push your code to git repository and wait till git actions starts the build and deploys to PKS cluster. Use the below commands to push your code to repository.
```shell script
git add .
git commit -m "MESSAGE"
git push origin pipeline-work:master
```
- Verify your objects created in pks cluster and access the service in browser as per the commands mentioned in the previous lab
- Use the below command to checkout required tag for next lab
```shell script
git checkout log-start -b log-work
```