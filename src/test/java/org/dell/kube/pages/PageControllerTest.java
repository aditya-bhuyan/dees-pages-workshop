package org.dell.kube.pages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PageControllerTest {
    private IPageRepository pageRepository;
    private PageController controller;

    @BeforeEach
    public void setUp() {
        pageRepository = mock(IPageRepository.class);
        controller = new PageController(pageRepository);
    }

    @Test
    public void testCreate() {
        String businessName="XYZ";
        String address="Bangalore";
        long categoryId=123L;
        String contactNumber="1234567890";
        Page pageToCreate = new Page(businessName, address,categoryId, contactNumber);

        long pageId = 1L;
        Page expectedResult = new Page(pageId, businessName, address,categoryId, contactNumber);
        doReturn(expectedResult)
                .when(pageRepository)
                .create(any(Page.class));

        ResponseEntity response = controller.create(pageToCreate);

        verify(pageRepository).create(pageToCreate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testRead() {
        long pageId = 1L;
        String businessName="XYZ";
        String address="Bangalore";
        long categoryId=123L;
        String contactNumber="1234567890";
        Page expected = new Page(pageId, businessName, address,categoryId, contactNumber);
        doReturn(expected)
                .when(pageRepository)
                .read(pageId);

        ResponseEntity<Page> response = controller.read(pageId);

        verify(pageRepository).read(pageId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testRead_NotFound() {
        long nonExistentPageId = 1L;
        doReturn(null)
                .when(pageRepository)
                .read(nonExistentPageId);

        ResponseEntity<Page> response = controller.read(nonExistentPageId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testList() {
        List<Page> expected = asList(
                new Page(1L, "XYZ", "Bangalore",123L, "1234567890"),
                new Page(2L, "ABC", "Karnataka", 234L, "987654321")
        );
        doReturn(expected).when(pageRepository).list();

        ResponseEntity<List<Page>> response = controller.list();

        verify(pageRepository).list();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate() {
        long pageId = 1L;
        String businessName="XYZ";
        String address="Bangalore";
        long categoryId=123L;
        String contactNumber="1234567890";
        Page expected = new Page(pageId, businessName, address,categoryId, contactNumber);
        doReturn(expected)
                .when(pageRepository)
                .update(any(Page.class), eq(pageId));

        ResponseEntity response = controller.update(expected, pageId);

        verify(pageRepository).update(expected, pageId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate_NotFound() {
        long nonExistentPageId = 1L;
        doReturn(null)
                .when(pageRepository)
                .update(any(Page.class),eq(nonExistentPageId));

        ResponseEntity response = controller.update(new Page(), nonExistentPageId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDelete() {
        long pageId = 1L;
        ResponseEntity response = controller.delete(pageId);
        verify(pageRepository).delete(pageId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
