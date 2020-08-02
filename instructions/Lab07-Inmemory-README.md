## Instructions to Reach Inmemory Solution
As part of the *inmemory-start* checkout 2 Test classes would be added to the codebase. On build the application would fail as there is no code to support the test cases. Follow the below instructions to reach solution so that the test cases will pass.
- Create new File Page.java in src/main under *org.dell.kube.pages* package
```java
package org.dell.kube.pages;

public class Page {

    private Long id;
    private String businessName;
    private Long categoryId;
    private String address;
    private String contactNumber;

    public Page(){}

    public Page(String businessName, String address, long categoryId, String contactNumber) {
        this.businessName = businessName;
        this.address = address;
        this.categoryId = categoryId;
        this.contactNumber = contactNumber;
    }

    public Page(long id, String businessName, String address, long categoryId, String contactNumber) {
        this.id = id;
        this.businessName = businessName;
        this.address = address;
        this.categoryId = categoryId;
        this.contactNumber = contactNumber;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
```
- Create new IPageRepository.java in src/main under *org.dell.kube.pages* package
```java
package org.dell.kube.pages;

import java.util.List;

public interface IPageRepository {
    public Page create(Page page);
    public Page read(long id);
    public List<Page> list();
    public Page update(Page page, long id);
    public void delete(long id);
}
```
- Create new InMemoryPageRepository which implements IPageRepository
```java
package org.dell.kube.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPageRepository implements IPageRepository{
    Map<Long,Page> repo = new HashMap<Long,Page>();
    long counter;

    @Override
    public Page create(Page page) {
        page.setId(++counter);
        repo.put(page.getId(),page);
        return repo.get(page.getId());
    }

    @Override
    public Page read(long id) {
        return repo.get(id);
    }

    @Override
    public List<Page> list() {
        return new ArrayList<Page>(repo.values());
    }

    @Override
    public Page update(Page page, long id) {
        Page data = repo.get(id);
        if(data != null){
            page.setId(id);
            repo.put(page.getId(),page);
            data = page;
        }
        return data;
    }

    @Override
    public void delete(long id) {
       repo.remove(id);
    }
}
```
- Create a bean called  **pageRepository** in PageApplication.java which returns an implementation of *IPageRepository*
- Create a PageController.java in src folder. Create an Instance of IPageRepository and intialiase it with a constructor injection
```java
package org.dell.kube.pages;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pages")
public class PageController {

    private IPageRepository pageRepository;
    public PageController(IPageRepository pageRepository)
    {
        this.pageRepository = pageRepository;
    }
    @PostMapping
    public ResponseEntity<Page> create(@RequestBody Page page) {
        Page newPage= pageRepository.create(page);
        return new ResponseEntity<Page>(newPage, HttpStatus.CREATED);
    }
    @GetMapping("{id}")
    public ResponseEntity<Page> read(@PathVariable long id) {
        Page page = pageRepository.read(id);
        if(page!=null)
            return new ResponseEntity<Page>(page,HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    @GetMapping
    public ResponseEntity<List<Page>> list() {
        List<Page> pages= pageRepository.list();
        return new ResponseEntity<List<Page>>(pages,HttpStatus.OK);
    }
    @PutMapping("{id}")
    public ResponseEntity<Page> update(@RequestBody Page page, @PathVariable long id) {
        Page updatedPage= pageRepository.update(page,id);
        if(updatedPage!=null)
            return new ResponseEntity<Page>(updatedPage,HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable long id) {
        pageRepository.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
```
- Run the application and test by making CRUD operations using any CRUD tool like ARC, POSTMAN or CURL.
- Build and Publish the docker image tag as **repo** and change the tag value both in pages-deployment.yaml and pipeline.yaml also
- Use the below commands to Check in the code to start github actions to deploy in PKS Cluster
```shell script
git add .
git commit -m "MESSAGE"
git push origin inmemory-work:master
```
- Verify the deployments in PKS Cluster and test the application by opening it in browser as per instructions given in previous labs
- Use the below command to checkout required tag for next lab
```shell script
git checkout persistence-start -b persistence-work
```