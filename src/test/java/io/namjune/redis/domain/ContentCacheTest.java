package io.namjune.redis.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContentCacheTest {

    private static final String TEST_KEY_MAP = "test:key:map";
    private static final String TEST_KEY_LIST = "test:key:lists";

    @Autowired
    RedisTemplate redisTemplate;

    @Resource(name = "redisTemplate")
    ZSetOperations<String, String> zSetOperations;

    @Resource(name = "redisTemplate")
    ListOperations<String, Long> listOperations;

    @Resource(name = "redisTemplate")
    HashOperations<String, Long, List<Long>> hashOperations;

    @AfterEach
    public void tearDown() {
        redisTemplate.delete(TEST_KEY_MAP);
        redisTemplate.delete(TEST_KEY_LIST);
    }

    @Test
    @DisplayName("Redis Hash 테스트")
    public void redis_hashes_test() {
        //given
        int size = 3;
        putTestMapData(size);

        //when
        Map<Long, List<Long>> entries = hashOperations.entries(TEST_KEY_MAP);
        ContentHeadlineCache contentCache = ContentHeadlineCache.builder()
                                                                .contentsByCpIdxMap(entries)
                                                                .build();

        boolean isExistKey = entries.keySet().iterator().hasNext();

        //then
        assertThat(entries.size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().size()).isEqualTo(size);
        assertThat(contentCache.getContentsByCpIdxMap().get(1L).size()).isEqualTo(5);
        assertThat(contentCache.getContentsByCpIdxMap().get(3L).get(0)).isEqualTo(3L);
        assertThat(isExistKey).isTrue();
    }

    @Test
    @DisplayName("Redis List 테스트")
    public void redis_lists_test() {
        //given
        List<Long> origin = Arrays.asList(1L, 2L, 3L, 4L);
        listOperations.rightPushAll(TEST_KEY_LIST, origin);
        List<Long> list = listOperations.range(TEST_KEY_LIST, 0, -1);

        assertThat(list.size()).isEqualTo(origin.size());
        assertThat(list.get(0)).isEqualTo(1L);
    }

    private void putTestMapData(int size) {
        Map<Long, List<Long>> testMap = new HashMap<>();
        LongStream.rangeClosed(1L, size)
                  .forEach(l -> testMap
                      .put(l, Arrays.asList(l, 2L * l, 3L * l, 4L * l, 5L * l)));
        hashOperations.putAll(TEST_KEY_MAP, testMap);
    }
}