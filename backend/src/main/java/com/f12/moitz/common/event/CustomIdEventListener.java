package com.f12.moitz.common.event;

import com.f12.moitz.application.CustomIdGenerator;
import com.f12.moitz.domain.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomIdEventListener extends AbstractMongoEventListener<Object> {

    private final CustomIdGenerator customIdGenerator;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        Object entity = event.getSource();

        if (entity instanceof final Result result) {
            if (result.getId() == null) {
                result.setId(customIdGenerator.generateCustomId(Result.SEQUENCE_NAME));
            }
        }
    }
}
