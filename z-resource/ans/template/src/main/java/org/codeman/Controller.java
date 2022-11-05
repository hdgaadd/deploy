package org.codeman;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hdgaadd
 * created on
 */
@RestController
public class Controller {

    @GetMapping()
    public String robot() {
        return null;
    }

}
