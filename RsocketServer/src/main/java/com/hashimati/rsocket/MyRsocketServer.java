package com.hashimati.rsocket;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;

public class MyRsocketServer
{

    public static void main(String... a)
    {


        Disposable x = RSocketFactory.receive()
                .acceptor(new SocketAcceptorImpl())
                .transport(TcpServerTransport.create("localhost", 9090))
                .start()
                .subscribeOn(Schedulers.newParallel(""))
                .subscribe();

    }
    private static class SocketAcceptorImpl implements SocketAcceptor {
        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket reactiveSocket) {
            return Mono.just(
                    new AbstractRSocket() {
                        @Override
                        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                            return Flux.from(payloads)
                                    .map(Payload::getDataUtf8)
                                    .map(s -> ""+new Numbers())
                                    .map(DefaultPayload::create);
                        }
                    });
        }
    }
}

class Numbers{
    
    private int n1, n2;

    public Numbers() {
        Random r = new Random();
        n1 = r.nextInt(10);
        n2 = r.nextInt(10); 
    }

    public Numbers(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    @Override
    public String toString() {
        return n1 + " X "+ n2 + " = "+ (n1 *n2); 
    }
}