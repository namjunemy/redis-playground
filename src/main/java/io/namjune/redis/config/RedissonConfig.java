package io.namjune.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    RedissonClient redissonClient() {
        Config configSingle = new Config();
        configSingle.useSingleServer().setAddress(generateUrl());
        return Redisson.create(configSingle);
    }

    private String generateUrl() {
        return "redis://" + redisHost + ":" + redisPort;
    }
}
