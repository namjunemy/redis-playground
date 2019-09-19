package io.namjune.redis.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentCacheTest {

    private static final String TEST_KEY = "test:key";

    @Autowired
    RedissonClient redissonClient;

    @Before
    public void setUp() {
        this.redissonClient.getKeys().deleteByPattern("*");
    }

    @After
    public void tearDown() {
        this.redissonClient.getMap(TEST_KEY).delete();
    }

    @Test
    public void redis_hashes_test() {
        //given
        int size = 3;
        putTestData(size);

        //when
        RMap<Long, List<Long>> map = this.redissonClient.getMap(TEST_KEY);
        ContentCache contentCache = ContentCache.builder()
            .contentsByCpIdxMap(map.readAllMap())
            .build();

        //then
        assertThat(map.size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().get(1L).size()).isEqualTo(5);
        assertThat(contentCache.getContentsByCpIdxMap().get(3L).get(0)).isEqualTo(3L);
    }

    private void putTestData(int size) {
        LongStream.rangeClosed(1L, size)
            .forEach(l -> this.redissonClient
                .getMap(TEST_KEY)
                .put(l, Arrays.asList(l, 2L * l, 3L * l, 4L * l, 5L * l)));
    }
}