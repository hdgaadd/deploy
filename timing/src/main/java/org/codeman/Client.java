package org.codeman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * setting clock -> 负载均衡setting到高可用Redis的DelayQueue -> 任务到时，DelayQueue发送kafka消息，触发任务进行
 */
@SpringBootApplication
public class Client {
	public static void main(String[] args) {
		SpringApplication.run(Client.class, args);
	}
}
