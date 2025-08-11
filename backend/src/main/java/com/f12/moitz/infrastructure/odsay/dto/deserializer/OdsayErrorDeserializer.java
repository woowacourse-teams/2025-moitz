package com.f12.moitz.infrastructure.odsay.dto.deserializer;

import com.f12.moitz.infrastructure.odsay.dto.OdsayErrorResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class OdsayErrorDeserializer extends JsonDeserializer<OdsayErrorResponse> {

    @Override
    public OdsayErrorResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.START_OBJECT) {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            return mapper.readValue(p, OdsayErrorResponse.class);
        }

        if (p.currentToken() == JsonToken.START_ARRAY) {
            p.skipChildren();
            return null;
        }

        return null;
    }

}
