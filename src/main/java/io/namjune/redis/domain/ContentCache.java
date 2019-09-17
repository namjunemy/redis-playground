package io.namjune.redis.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ContentCache implements Serializable {

    private Map<Long, List<Long>> contentsByCpIdxMap = new HashMap<>();

    @Builder
    public ContentCache(Map<Long, List<Long>> contentsByCpIdxMap) {
        this.contentsByCpIdxMap = contentsByCpIdxMap;
    }
}
