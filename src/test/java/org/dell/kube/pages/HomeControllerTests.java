package org.dell.kube.pages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTest {
    private final String message = "YellowPages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController();

        assertThat(controller.getPage()).contains(message);
    }


}
