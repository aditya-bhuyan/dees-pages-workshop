## Instructions to use JDBC Repository Solution
As part of the *persistence-start* a new Test class is added to the code base. Gradle build would fail as there no code to match the Test class. Follow the below instructions to reach solution to use JDBC Repository
- Add jpa and mysql connector dependencies in build.gradle. It will fetch the Spring JDBC library.
```groovy
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
implementation 'mysql:mysql-connector-java:8.0.12'
```
- Add the following line in test.environment closure of build.gradle
```groovy
test.environment([
		"PAGE_CONTENT": "YellowPages",
		"SPRING_DATASOURCE_URL": "jdbc:mysql://localhost:3306/pages?createDatabaseIfNotExist=true&useSSL=false&user=root",
])
```
- Add the junit dependency in  depenendency closure  in build.gradle
```groovy
testImplementation "junit:junit:4.12"
```
- Add jdbc related properties in application.properties for both **main** and **test** folders
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pages?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=
```
- Add new class MySqlPageRepository.java which implements IPageRepository
```java
package org.dell.kube.pages;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class MySqlPageRepository implements IPageRepository {
    private final JdbcTemplate jdbcTemplate;
    public MySqlPageRepository(DataSource dataSource)
    {
        this.jdbcTemplate=new JdbcTemplate(dataSource);
        this.init();
    }
    @Override
    public Page create(Page page) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO pages (business_name, address, category_id, contact_number) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );
            statement.setString(1, page.getBusinessName());
            statement.setString(2, page.getAddress());
            statement.setLong(3,page.getCategoryId());
            statement.setString(4,page.getContactNumber());

            return statement;
        }, generatedKeyHolder);

        return read(generatedKeyHolder.getKey().longValue());

    }
    @Override
    public Page read(long id) {
        return jdbcTemplate.query(
                "SELECT id, business_name, address, category_id, contact_number FROM pages WHERE id = ?",
                new Object[]{id},
                extractor);
    }

    @Override
    public List<Page> list() {
        return jdbcTemplate.query("SELECT id, business_name, address, category_id, contact_number FROM pages", mapper);
    }

    @Override
    public Page update(Page page, long id) {
        jdbcTemplate.update("UPDATE pages " +
                        "SET business_name = ?, address = ?, category_id = ?,  contact_number = ? " +
                        "WHERE id = ?",
                page.getBusinessName(),
                page.getAddress(),
                page.getCategoryId(),
                page.getContactNumber(),
                id);

        return read(id);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM pages WHERE id = ?", id);
    }

    private final RowMapper<Page> mapper = (rs, rowNum) -> new Page(
            rs.getLong("id"),
            rs.getString("business_name"),
            rs.getString("address"),
            rs.getLong("category_id"),
            rs.getString("contact_number")
    );
    private final ResultSetExtractor<Page> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    private void init(){
        jdbcTemplate.execute("create table if not exists pages(\n" +
                "  id bigint(20) not null auto_increment,\n" +
                "  business_name VARCHAR(50),\n" +
                "  address VARCHAR(50),\n" +
                "  category_id bigint(20),\n" +
                "  contact_number VARCHAR(50),\n" +
                "\n" +
                "  primary key (id)\n" +
                ")\n" +
                "engine = innodb\n" +
                "default charset = utf8;");
    }
}
```
- Make change in PageApplication class to return MySqlPageRepository instance instead of InMemoryPageRepository instance. This method would also take a DataSource instance as argument.
```java
@Bean
public IPageRepository iPageRepository(DataSource dataSource){
		return new MySqlPageRepository(dataSource);
	}
```
- Add new deployment file  mysql-pv.yaml to be used by the new mysql deployment  volume in kubernetes cluster under  deployment folder
```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-volume-<your-name>
  namespace: <your-name>
  labels:
    type: local
spec:
  storageClassName: mysql-<your-name>
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteMany
  hostPath:
    path: "/mnt/data-<your-name>"
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-volume-claim-<your-name>
  namespace: <your-name>
spec:
  storageClassName: mysql-<your-name>
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Gi
```
- Create a new file called mysql-secret.yaml in deployment folder
```yaml
apiVersion: v1
data:
  mysql-pass: cGFzc3dvcmQ=
kind: Secret
metadata:
  name: mysql-secret
  namespace: <your-name>
```
- Create a new file called mysql-deployment.yaml in deployment folder
```yaml
apiVersion: apps/v1 
kind: Deployment
metadata:
  name: mysql
  namespace: <your-name>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - image: mysql:8.0
          name: mysql
          env:
            # Instead of using value directly we could also use secrets
           - name: MYSQL_ROOT_PASSWORD
             valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: mysql-pass
          ports:
            - containerPort: 3306
              name: mysql
          volumeMounts:
            - name: mysql-storage
              mountPath: "/var/lib/mysql-<your-name>"
      volumes:
        - name: mysql-storage
          persistentVolumeClaim:
            claimName: mysql-volume-claim-<your-name>
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: <your-name>
spec:
  ports:
    - port: 3306
  selector:
    app: mysql
  clusterIP: None
#kubectl run -it --rm --image=mysql:8.0 --restart=Never mysql-client -- mysql -h mysql -ppassword
#sudo ln -s /etc/apparmor.d/usr.sbin.mysqld /etc/apparmor.d/disable/
  #sudo apparmor_parser -R /etc/apparmor.d/usr.sbin.mysqld
```
- Replace all the \<your-name> with your actual first name.
- Ensure that a MySQL instance with no password for user root is running in local machine
- Build, Test and Run the application locally by using the following commands
```shell script
./gradlew clean
./gradlew build
./gradlew bootRun
```
- Stop the application. As we have to now prepare the application to be used in kubernetes cluster replace the values of following attributes in the application.properties in src/main folder
```properties
spring.datasource.url=jdbc:mysql://mysql/pages?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&user=root
spring.datasource.password=password
```
- Add the following in test.environment closure in build.gradle
```groovy
"SPRING_DATASOURCE_USERNAME": "root",
"SPRING_DATASOURCE_PASSWORD": "root",
```
- Build the application by using the following command
```shell script
./gradlew clean
./gradlew build  
```
- Make change in the pages-deployment.yaml and pipeline.yaml to update the tag as **persist**
- Add the below content in pipeline.yaml just above the line "- name: Build with Gradle"
```yaml
- name: Start Ubuntu MySQL
  run: sudo systemctl start mysql.service
```
- Change the pipeline.yaml to use the new mysql related yaml files. The last section should look like below
```yaml
kubectl apply -f deployment/pages-namespace.yaml
kubectl apply -f deployment/mysql-pv.yaml
kubectl apply -f deployment/mysql-secret.yaml
kubectl apply -f deployment/mysql-deployment.yaml
kubectl apply -f deployment/pages-config.yaml
kubectl apply -f deployment/pages-service.yaml
kubectl delete -f deployment/pages-deployment.yaml
kubectl apply -f deployment/pages-deployment.yaml
```
- Use the below commands to push the code to the github so that github actions will start the pipeline and the application would be deployed in cluster.
```shell script
git add .
git commit -m "MESSAGE"
git push origin persistence-work:master
```
- Use the below command to set default namespace
```shell script
kubectl config set-context --current --namespace=<your-name>
```
- Use the below command to connect to the mysql server and verify the schema and table
```shell script
kubectl run -it --rm --image=mysql:8.0 --restart=Never mysql-client -- mysql -h mysql -ppassword
```
- After that verify the url of the services and open the url on browser as per the instructions in the earlier labs to test the application.