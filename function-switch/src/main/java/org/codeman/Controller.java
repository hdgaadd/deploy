package org.codeman;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hdgaadd
 * created on 2022/11/02
 */
@RestController
public class Controller {

    final static String response = "halo, baby!";

    @GetMapping()
    public String robot() {
        return response;
    }

}
