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
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentCacheTest {

    private static final String TEST_KEY_MAP = "test:key:map";
    private static final String TEST_KEY_LIST = "test:key:lists";

    @Autowired
    RedissonClient redissonClient;

    @Before
    public void setUp() {
        this.redissonClient.getKeys().deleteByPattern("*");
    }

    @After
    public void tearDown() {
        this.redissonClient.getMap(TEST_KEY_MAP).delete();
    }

    @Test
    public void redis_hashes_test() {
        //given
        int size = 3;
        putTestMapData(size);

        //when
        RMap<Long, List<Long>> map = this.redissonClient.getMap(TEST_KEY_MAP, new JsonJacksonCodec());
        ContentCache contentCache = ContentCache.builder()
            .contentsByCpIdxMap(map.readAllMap())
            .build();

        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(TEST_KEY_MAP);
        boolean isExistKey = keys.iterator().hasNext();

        //then
        assertThat(map.size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().get(1L).size()).isEqualTo(5);
        assertThat(contentCache.getContentsByCpIdxMap().get(3L).get(0)).isEqualTo(3L);
        assertThat(isExistKey).isTrue();
    }

    @Test
    public void redis_lists_test() {
        //given
        List<Long> origin = Arrays.asList(1L, 2L, 3L, 4L);
        RList<Long> bucket = redissonClient.getList(TEST_KEY_LIST, new JsonJacksonCodec());
        bucket.addAllAsync(origin);

        RList<Long> list = redissonClient.getList(TEST_KEY_LIST, new JsonJacksonCodec());
        list.readAll();

        assertThat(list.size()).isEqualTo(origin.size());
        assertThat(list.get(0)).isEqualTo(1L);
    }

    private void putTestMapData(int size) {
        Map<Long, List<Long>> testMap = new HashMap<>();
        LongStream.rangeClosed(1L, size)
            .forEach(l -> testMap
                .put(l, Arrays.asList(l, 2L * l, 3L * l, 4L * l, 5L * l)));
        this.redissonClient.getMap(TEST_KEY_MAP, new JsonJacksonCodec())
            .putAllAsync(testMap);
    }


}