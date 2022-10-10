package org.codeman.open;

import org.codeman.component.redisson.PutInQueue;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author hdgaadd
 * Created on 2022/09/14
 *
 * description: 高稳定性、高可用、高性能定时系统
 * step: setting clock -> 负载均衡setting到高可用Redis集群的DelayQueue -> 兜底信息写入db -> 任务到时，DelayQueue发送kafka消息，触发任务进行
 */
@RestController
public class Controller {

    final static String response = "successful!";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private PutInQueue service;

    @PostMapping()
    public String setClock(@RequestParam Integer delaySecond) throws IOException {
        service.setClock(delaySecond);
        return response;
    }

}
