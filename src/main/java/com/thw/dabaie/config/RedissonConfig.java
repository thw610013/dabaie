package com.thw.dabaie.config;

import io.swagger.models.auth.In;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 告诉SpringBoot，这个类是一个配置类，用于获取配置文件中Redis的配置信息
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private  String host;

    private Integer port;

    private Integer database;

    private String password;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }
}
