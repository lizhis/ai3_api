package com.ai.baseeureka;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * springboot3的bug
 * https://github.com/Netflix/eureka/issues/1486
 * https://github.com/spring-cloud/spring-cloud-netflix/issues/4145
 * @Description
 * @Author
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Void> error() {
        return ResponseEntity.notFound().build();
    }

}
