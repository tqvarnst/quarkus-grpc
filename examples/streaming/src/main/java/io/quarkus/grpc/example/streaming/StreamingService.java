package io.quarkus.grpc.example.streaming;

import io.grpc.examples.streaming.Empty;
import io.grpc.examples.streaming.Item;
import io.grpc.examples.streaming.QuarkusStreamingGrpc;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;
import java.time.Duration;

@Singleton
public class StreamingService extends QuarkusStreamingGrpc.StreamingImplBase {

    @Override
    public Multi<Item> source(Empty request) {
        return Multi.createFrom().ticks().every(Duration.ofMillis(2))
                .transform().byTakingFirstItems(10)
                .map(l -> Item.newBuilder().setValue(Long.toString(l)).build());
    }

    @Override
    public Uni<Empty> sink(Multi<Item> request) {
        return request
                .map(Item::getValue)
                .map(Long::parseLong)
                .collectItems().last()
                .map(l -> Empty.newBuilder().build());
    }

    @Override
    public Multi<Item> pipe(Multi<Item> request) {
        return request
                .map(Item::getValue)
                .map(Long::parseLong)
                .onItem().scan(() -> 0L, Long::sum)
                .onItem().apply(l -> Item.newBuilder().setValue(Long.toString(l)).build());
    }
}