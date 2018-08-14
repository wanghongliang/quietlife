package com.example.sources;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class FcoinCandles {


    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }


    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//ms
        factory.setConnectTimeout(15000);//ms
        return factory;
    }

    public FcoinCandles(){

        String url = "https://api.fcoin.com/v2/market/candles/M1/btcusdt?limit=2";
        String sinkUrl = "http://localhost:9093";

        RestTemplate  restTemplate =restTemplate( new SimpleClientHttpRequestFactory());



        Map<String,String> queryParam = new HashMap<>();
        queryParam.put("limit","720");

        //JSONObject json = restTemplate.postForEntity(url, getData.toString(), JSONObject.class).getBody();


        for(;;){
            try {

                String json = restTemplate.getForEntity(url, String.class, queryParam).getBody();


                JSONArray jsonArray = new JSONObject(json).getJSONArray("data");

                for( int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    JSONObject postData = new JSONObject();

                    postData.put("timestamp", jsonObject.get("id"));
                    postData.put("seq", jsonObject.get("id"));
                    postData.put("open", jsonObject.get("open"));
                    postData.put("close", jsonObject.get("close"));
                    postData.put("high", jsonObject.get("high"));
                    postData.put("low", jsonObject.get("low"));
                    postData.put("volume", jsonObject.get("base_vol"));

                    System.out.println(postData);
                    JSONObject json2 = restTemplate.postForEntity(sinkUrl, postData.toString(), JSONObject.class).getBody();
                    System.out.println(json2);
                }

                Thread.sleep(1000 * 3);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public static void main(String[] args) {
        new FcoinCandles();
    }



}
