package com.f12.moitz.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CustomIdGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final SequenceGenerator sequenceGenerator;

    public String generateCustomId(String entityType) {
        StringBuilder randomStringBuilder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            randomStringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        long sequenceNumber = sequenceGenerator.generateSequence(entityType + "_sequence");

        return randomStringBuilder.toString() + sequenceNumber;
    }
}
