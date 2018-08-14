package com.example.demo;


import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

import com.example.demo.websocket.MyStompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class Fcoin {

    final String apiKey = "....";
    final String apiSecret = "....";
    private static String URL = "ws://localhost:9292/websocket";

    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }


    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//ms
        factory.setConnectTimeout(15000);//ms
        return factory;
    }

    public Fcoin(){
        this.init();
    }

    public void init(){
        try {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            StompSessionHandler sessionHandler = new MyStompSessionHandler();
            stompClient.connect(URL, sessionHandler);

            new Scanner(System.in).nextLine(); // Don't close immediately.

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Fcoin();
    }

}
