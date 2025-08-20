package com.f12.moitz.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "auto_sequence")
public class AutoIncrementSequence {

    @Id
    private String id;

    private Long seq;

    protected AutoIncrementSequence() {}


}
