package com.f12.moitz.application;

import com.f12.moitz.domain.entity.AutoIncrementSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class SequenceGenerator {

    private final MongoOperations mongoOperations;

    public long generateSequence(String seqName) {
        AutoIncrementSequence incrementSequence = mongoOperations.findAndModify(
                Query.query(Criteria.where("id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                AutoIncrementSequence.class
        );
        return incrementSequence != null ? incrementSequence.getSeq() : 1;
    }
}
