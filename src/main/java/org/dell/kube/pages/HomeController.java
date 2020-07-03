package org.dell.kube.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);
    private String pageContent;

    public HomeController(@Value("${page.content}") String pageContent){
        this.pageContent=pageContent;

    }
    @GetMapping
    public String getPage(){
        logger.debug("Welcome Page Accessed");
        logger.info("Welcome Page Accessed");
        logger.trace("Welcome Page Accessed");
        logger.warn("Welcome Page Accessed");
        logger.error("Welcome Page Accessed");
        return "Hello from page : "+pageContent+" ";
    }


}