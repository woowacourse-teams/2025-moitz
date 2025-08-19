package com.f12.moitz.application.port;

import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Route;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncRouteFinder extends RouteFinder {

    CompletableFuture<List<Route>> findRoutesAsync(List<StartEndPair> placePairs);

}
