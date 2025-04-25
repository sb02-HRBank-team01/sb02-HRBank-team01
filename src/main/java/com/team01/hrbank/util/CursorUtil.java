package com.team01.hrbank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;

public class CursorUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 커서를 디코딩해서 idAfter 값 추출
    public static Long decodeCursor(String cursor) {
        try {
            if (cursor == null || cursor.isEmpty()) {
                return null;
            }
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            String json = new String(decodedBytes);
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            return ((Number) map.get("id")).longValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 커서 값입니다.", e);
        }
    }

    // idAfter 값을 커서로 인코딩
    public static String encodeCursor(Long idAfter) {
        try {
            if (idAfter == null) {
                return null;
            }
            String json = objectMapper.writeValueAsString(Map.of("id", idAfter));
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("커서 인코딩 실패", e);
        }
    }
}
