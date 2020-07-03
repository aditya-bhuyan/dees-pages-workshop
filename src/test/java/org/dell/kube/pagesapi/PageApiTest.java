package org.dell.kube.pagesapi;

import com.jayway.jsonpath.DocumentContext;
import org.dell.kube.pages.Page;
import org.dell.kube.pages.PageApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = PageApplication.class, webEnvironment = RANDOM_PORT)
public class PageApiTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String businessName="XYZ";
    private final String address="Bangalore";
    private final long categoryId=123L;
    private final String contactNumber="1234567890";
    private Page page = new Page(businessName, address,categoryId, contactNumber);



    @Test
    public void testCreate() throws Exception {
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/pages", page, String.class);


        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentContext createJson = parse(createResponse.getBody());
        assertThat(createJson.read("$.id", Long.class)).isGreaterThan(0);
        assertThat(createJson.read("$.businessName", String.class)).isEqualTo(businessName);
        assertThat(createJson.read("$.address", String.class)).isEqualTo(address);
        assertThat(createJson.read("$.categoryId", Long.class)).isEqualTo(categoryId);
        assertThat(createJson.read("$.contactNumber", String.class)).isEqualTo(contactNumber);
    }

    @Test
    public void testList() throws Exception {
        Long id = createPage();


        ResponseEntity<String> listResponse = restTemplate.getForEntity("/pages", String.class);


        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext listJson = parse(listResponse.getBody());

        Collection timeEntries = listJson.read("$[*]", Collection.class);
        assertThat(timeEntries.size()).isEqualTo(1);

        Long readId = listJson.read("$[0].id", Long.class);
        assertThat(readId).isEqualTo(id);
    }

    @Test
    public void testRead() throws Exception {
        Long id = createPage();


        ResponseEntity<String> readResponse = this.restTemplate.getForEntity("/pages/" + id, String.class);

        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext readJson = parse(readResponse.getBody());
        assertThat(readJson.read("$.id", Long.class)).isEqualTo(id);
        assertThat(readJson.read("$.businessName", String.class)).isEqualTo(businessName);
        assertThat(readJson.read("$.address", String.class)).isEqualTo(address);
        assertThat(readJson.read("$.categoryId", Long.class)).isEqualTo(categoryId);
        assertThat(readJson.read("$.contactNumber", String.class)).isEqualTo(contactNumber);
    }

    @Test
    public void testUpdate() throws Exception {
        Long id = createPage();
        String businessName="XYZ";
        String address="Bangalore";
        long categoryId=123L;
        String contactNumber="1234567890";
        Page updatedPage = new Page(businessName, address, categoryId, contactNumber);


        ResponseEntity<String> updateResponse = restTemplate.exchange("/pages/" + id, HttpMethod.PUT, new HttpEntity<>(updatedPage, null), String.class);


        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext updateJson = parse(updateResponse.getBody());
        assertThat(updateJson.read("$.id", Long.class)).isEqualTo(id);
        assertThat(updateJson.read("$.businessName", String.class)).isEqualTo(businessName);
        assertThat(updateJson.read("$.address", String.class)).isEqualTo(address);
        assertThat(updateJson.read("$.categoryId", Long.class)).isEqualTo(categoryId);
        assertThat(updateJson.read("$.contactNumber", String.class)).isEqualTo(contactNumber);
    }

    @Test
    public void testDelete() throws Exception {
        Long id = createPage();


        ResponseEntity<String> deleteResponse = restTemplate.exchange("/pages/" + id, HttpMethod.DELETE, null, String.class);


        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> deletedReadResponse = this.restTemplate.getForEntity("/pages/" + id, String.class);
        assertThat(deletedReadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long createPage() {
        HttpEntity<Page> entity = new HttpEntity<>(page);

        ResponseEntity<Page> response = restTemplate.exchange("/pages", HttpMethod.POST, entity, Page.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return response.getBody().getId();
    }
}
