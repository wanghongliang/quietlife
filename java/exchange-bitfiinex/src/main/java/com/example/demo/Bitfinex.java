package com.example.demo;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCandle;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.Timeframe;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.function.BiConsumer;

public class Bitfinex {


    final String apiKey = "....";
    final String apiSecret = "....";


    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }


    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//ms
        factory.setConnectTimeout(15000);//ms
        return factory;
    }

    public Bitfinex(){
        try {
            // For public operations (subscribe ticker, candles)
            BitfinexApiBroker bitfinexApiBroker = new BitfinexApiBroker();
            bitfinexApiBroker.connect();

            // For public and private operations (executing orders, read wallets)
            //BitfinexApiBroker bitfinexApiBroker = new BitfinexApiBroker(apiKey, apiSecret);
            //bitfinexApiBroker.connect();

            final BitfinexCandlestickSymbol symbol
                    = new BitfinexCandlestickSymbol(BitfinexCurrencyPair.BTC_USD, Timeframe.MINUTES_1);


            String url = "http://localhost:8901/";

            RestTemplate  restTemplate =restTemplate( new SimpleClientHttpRequestFactory());

            // The consumer will be called on all received candles for the symbol
            final BiConsumer<BitfinexCandlestickSymbol, BitfinexCandle> callback = (sym, tick) -> {

                //System.out.format("close=%s, volume=%s \n",tick.getClose(),tick.getVolume());
                JSONObject postData = new JSONObject();
                postData.put("timestamp", tick.getTimestamp());
                postData.put("open", tick.getOpen());
                postData.put("close", tick.getClose());
                postData.put("high", tick.getHigh());
                postData.put("low", tick.getLow());
                postData.put("volume", tick.getVolume().get());

                JSONObject json = restTemplate.postForEntity(url, postData.toString(), JSONObject.class).getBody();
                System.out.format("Got BitfinexTick (%s) for symbol (%s) result=(%s)\n", tick, sym, json);

            };

            final QuoteManager quoteManager = bitfinexApiBroker.getQuoteManager();
            quoteManager.registerCandlestickCallback(symbol, callback);
            quoteManager.subscribeCandles(symbol);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
         new Bitfinex();
    }
}
