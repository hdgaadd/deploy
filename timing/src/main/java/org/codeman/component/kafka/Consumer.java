package org.codeman.component.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author hdgaadd
 * Created on 2022/10/03
 */
@Component
public class Consumer {

    @KafkaListener(topics = "kafka-topic", groupId = "kafka-group")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        System.out.println("message: " + value);
        System.out.println("record: " + record);
        ack.acknowledge();
    }
}

