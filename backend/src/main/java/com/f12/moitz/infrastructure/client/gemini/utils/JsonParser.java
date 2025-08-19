package com.f12.moitz.infrastructure.client.gemini.utils;

import org.springframework.stereotype.Component;

@Component
public class JsonParser {

    /**
     * JSON 응답에서 마크다운 코드 블록과 불필요한 문자 제거
     */
    public String cleanJsonResponse(final String text) {
        if (text == null) {
            return "";
        }

        // 1. 마크다운 코드 블록 제거 (```json, ```, ``` 등)
        String cleaned = text.replaceAll("```json\\s*", "")
                .replaceAll("```\\s*$", "")
                .replaceAll("^```\\s*", "")
                .trim();

        // 2. 앞뒤 공백 및 개행 문자 정리
        cleaned = cleaned.trim();

        // 3. JSON이 아닌 텍스트가 앞에 있는 경우 처리
        final int jsonStart = findJsonStart(cleaned);
        if (jsonStart > 0) {
            cleaned = cleaned.substring(jsonStart);
        }

        // 4. JSON이 아닌 텍스트가 뒤에 있는 경우 처리
        final int jsonEnd = findJsonEnd(cleaned);
        if (jsonEnd > 0 && jsonEnd < cleaned.length()) {
            cleaned = cleaned.substring(0, jsonEnd + 1);
        }

        return cleaned.trim();
    }

    /**
     * JSON 시작 위치 찾기 ({ 또는 [)
     */
    private int findJsonStart(final String text) {
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == '{' || c == '[') {
                return i;
            }
        }
        return 0;
    }

    /**
     * JSON 끝 위치 찾기 (} 또는 ])
     */
    private int findJsonEnd(final String text) {
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            switch (c) {
                case '{':
                    braceCount++;
                    break;
                case '}':
                    braceCount--;
                    if (braceCount == 0 && bracketCount == 0) {
                        return i;
                    }
                    break;
                case '[':
                    bracketCount++;
                    break;
                case ']':
                    bracketCount--;
                    if (braceCount == 0 && bracketCount == 0) {
                        return i;
                    }
                    break;
            }
        }
        return text.length() - 1;
    }

    /**
     * 기본적인 JSON 유효성 검사
     */
    public boolean isValidJson(final String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        final String trimmed = text.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

}
