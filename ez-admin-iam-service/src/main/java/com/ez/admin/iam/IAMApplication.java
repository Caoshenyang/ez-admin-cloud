package com.ez.admin.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * IAM 服务
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class IAMApplication {

    public static void main(String[] args) {
        SpringApplication.run(IAMApplication.class, args);
    }
}
