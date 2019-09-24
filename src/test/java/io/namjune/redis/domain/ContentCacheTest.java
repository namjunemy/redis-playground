package io.namjune.redis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        RMap<Long, List<Long>> map = this.redissonClient.getMap(TEST_KEY, new JsonJacksonCodec());
        ContentCache contentCache = ContentCache.builder()
            .contentsByCpIdxMap(map.readAllMap())
            .build();

        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(TEST_KEY);
        boolean isExistKey = keys.iterator().hasNext();

        //then
        assertThat(map.size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().get(1L).size()).isEqualTo(5);
        assertThat(contentCache.getContentsByCpIdxMap().get(3L).get(0)).isEqualTo(3L);
        assertThat(isExistKey).isTrue();
    }

    private void putTestData(int size) {
        Map<Long, List<Long>> testMap = new HashMap<>();
        LongStream.rangeClosed(1L, size)
            .forEach(l -> testMap
                .put(l, Arrays.asList(l, 2L * l, 3L * l, 4L * l, 5L * l)));
        this.redissonClient.getMap(TEST_KEY, new JsonJacksonCodec())
            .putAllAsync(testMap);
    }
}