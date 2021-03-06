package io.quarkus.grpc.examples.hello;

import examples.GreeterGrpc;
import examples.HelloReply;
import examples.HelloRequest;
import examples.MutinyGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class HelloWorldMutualTlsServiceTest {

    private ManagedChannel channel;

    @BeforeEach
    public void init() throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(new File("src/main/resources/tls/ca.pem"));
        builder.keyManager(new File("src/main/resources/tls/client.pem"),
                new File("src/main/resources/tls/client.key"));
        SslContext context = builder.build();

        channel = NettyChannelBuilder.forAddress("localhost", 9000)
                .sslContext(context)
                .build();
    }

    @AfterEach
    public void cleanup() {
        channel.shutdownNow();
    }

    @Test
    public void testHelloWorldServiceUsingBlockingStub() {
        GreeterGrpc.GreeterBlockingStub client = GreeterGrpc.newBlockingStub(channel);
        HelloReply reply = client
                .sayHello(HelloRequest.newBuilder().setName("neo-blocking").build());
        assertThat(reply.getMessage()).isEqualTo("Hello neo-blocking");
    }

    @Test
    public void testHelloWorldServiceUsingMutinyStub() {
        HelloReply reply = MutinyGreeterGrpc.newMutinyStub(channel)
                .sayHello(HelloRequest.newBuilder().setName("neo-blocking").build()).await().indefinitely();
        assertThat(reply.getMessage()).isEqualTo("Hello neo-blocking");
    }

}