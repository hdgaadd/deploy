package org.codeman.open;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hdgaadd
 * Created on 2022/09/14
 *
 * @description: 高稳定性、高可用、高性能定时系统
 */
@RestController
public class Controller {

    final static String response = "successful!";

    @Resource
    private Service service;

    @PostMapping()
    public String setClock(@RequestParam Integer delaySecond) {
        service.setClock(delaySecond);
        return response;
    }

}
