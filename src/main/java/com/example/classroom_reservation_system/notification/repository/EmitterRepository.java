package com.example.classroom_reservation_system.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * Emitter 저장
     */
    public SseEmitter save(String emitterId, SseEmitter sseEmitter){
        emitters.put(emitterId,sseEmitter);
        log.info("새로운 Emitter가 추가되었습니다. ID: {}", emitterId);
        return sseEmitter;
    }

    /**
     * 이벤트를 캐시에 저장
     */
    public void saveEventCache(String eventId, Object event){
        eventCache.put(eventId, event);
        log.info("이벤트가 캐시되었습니다. ID: {}", eventId);
    }

    /**
     * 특정 회원의 모든 Emitter를 조회
     */
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 특정 회원의 모든 이벤트 캐시를 조회
     */
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Emitter를 삭제
     */
    public void deleteById(String id) {
        emitters.remove(id);
        log.info("Emitter가 삭제되었습니다. ID: {}", id);
    }
}
