package org.codeman;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hdgaadd
 * Created on 2022/09/14
 *
 * @description: 高稳定性、高可用、高性能定时系统
 */
@RestController
public class Client {

    final static String response = "halo, baby!";

    @GetMapping()
    public String robot() {
        return response;
    }

}
