package com.hashimati.rsocket.demo;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.Duration;
import java.util.Random;

@Service
public class MyRSocketClient
{
    public Flux<String> sendPayLoad()
    {
        RSocket socket =
                RSocketFactory.connect()
                        .keepAlive()
                        .transport(TcpClientTransport.create("localhost", 9090))
                        .start()
                        .block();

       return  socket
               .requestChannel(
                       Flux.interval(Duration.ofMillis(1000)).map(i -> DefaultPayload.create("")))
               .map(Payload::getDataUtf8)
               .doOnNext(System.out::println)
               .take(1000000000)
               .doFinally(signalType -> socket.dispose())
               ;
    }

}

@RestController
class MultiplyController{

    private MyRSocketClient myRSocketClient;
    public MultiplyController(MyRSocketClient myRSocketClient)
    {
        this.myRSocketClient = myRSocketClient;
    }
    @GetMapping(value = "/multiplies", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> start()
    {
        return
                myRSocketClient.sendPayLoad();
    }
}
