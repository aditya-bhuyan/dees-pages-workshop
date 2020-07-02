package org.dell.kube.pages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTest {
    private final String message = "Hello Yellow Pages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController(message);

        assertThat(controller.getPage()).contains(message);
    }


}
