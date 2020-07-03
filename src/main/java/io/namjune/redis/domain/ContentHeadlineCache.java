package io.namjune.redis.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ContentHeadlineCache implements Serializable {

    private Map<Long, List<Long>> contentsByCpIdxMap = new HashMap<>();

    @Builder
    public ContentHeadlineCache(Map<Long, List<Long>> contentsByCpIdxMap) {
        this.contentsByCpIdxMap = contentsByCpIdxMap;
    }
}
