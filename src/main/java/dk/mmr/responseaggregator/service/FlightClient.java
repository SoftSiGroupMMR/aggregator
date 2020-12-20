package dk.mmr.responseaggregator.service;

import dk.mmr.responseaggregator.ResponseaggregatorApplication;
import dk.mmr.responseaggregator.entity.Offers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import get.dk.si.route.MetaData;
import get.dk.si.route.Root;
import get.dk.si.route.Route;
import get.dk.si.route.Util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FlightClient {
    RestTemplate rt = new RestTemplate();
    private final Gson gson = new Gson();
    protected Logger logger = LoggerFactory.getLogger(FlightClient.class.getName());


    @Async
    public CompletableFuture<Offers> getOffersFromNorwegian(String cityFrom, String cityTo, Long time) throws InterruptedException {
        String url = "http://134.209.254.220:30288/search?cityFrom=" + cityFrom + " &cityTo=" + cityTo + "&time=" + time;
        Offers response = rt.getForObject(url, Offers.class);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<Offers> getOffersEasyjet(String cityFrom, String cityTo, Long time) {
        String url = "http://134.209.254.220:30793/search?cityFrom=" + cityFrom + " &cityTo=" + cityTo + "&time=" + time;
        Offers response = rt.getForObject(url, Offers.class);

        return CompletableFuture.completedFuture(response);
    }

    public List<Offers> getAllOffers(String cityFrom, String cityTo, Long time) throws InterruptedException {
        CompletableFuture<Offers> norwegian = getOffersFromNorwegian(cityFrom, cityTo, time);
        CompletableFuture<Offers> easyjet = getOffersEasyjet(cityFrom, cityTo, time);

        List<Offers> responses = new ArrayList<>();
        try {
            responses.add(norwegian.get());
            responses.add(easyjet.get());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return responses;
    }

    public void handleMessage(String message) {
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);

        String cityFrom = jsonMessage.get("metaData").getAsJsonObject().get("travelRequest").getAsJsonObject().get("cityFrom").getAsString();
        String cityTo = jsonMessage.get("metaData").getAsJsonObject().get("travelRequest").getAsJsonObject().get("cityTo").getAsString();
        Long time = jsonMessage.get("metaData").getAsJsonObject().get("travelRequest").getAsJsonObject().get("dateFrom").getAsLong();


        try {
            logger.info("Attempting to fetch flight offers");
            List<Offers> results = getAllOffers(cityFrom, cityTo, time);

            logger.info("got resulsts: " + gson.toJson(results));

            Util util = new Util();
            Root root = util.rootFromJson(message);
            MetaData metaData = root.getMetaData();
            metaData.put("flights", results);

            root.setMetaData(metaData);

            Route route = root.nextRoute();
            String json = util.rootToJson(root);
            util.sendToRoute(route, json);
            logger.info("Successfully added flights to message");

        }catch (Exception e){
            logger.error("An error occured while fetching flights: "+ e.getLocalizedMessage());
        }


    }


}
