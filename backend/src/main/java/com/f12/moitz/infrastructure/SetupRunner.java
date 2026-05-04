package com.f12.moitz.infrastructure;

import com.f12.moitz.application.subway.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupRunner implements ApplicationRunner {

    private final SetupService setupService;

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        setupService.setup();
    }

}
