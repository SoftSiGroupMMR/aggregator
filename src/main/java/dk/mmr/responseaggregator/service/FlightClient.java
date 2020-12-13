package dk.mmr.responseaggregator.service;

import dk.mmr.responseaggregator.entity.Offers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FlightClient {
    RestTemplate rt = new RestTemplate();

    @Async
    public CompletableFuture<Offers> getOffersFromNorwegian(String cityFrom, String cityTo, Long time) throws InterruptedException {
        String url = "http://localhost:8081/search?cityFrom=" + cityFrom + " &cityTo=" + cityTo + "&time=" + time;
        Offers response = rt.getForObject(url, Offers.class);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<Offers> getOffersEasyjet(String cityFrom, String cityTo, Long time) {
        String url = "http://localhost:8082/search?cityFrom=" + cityFrom + " &cityTo=" + cityTo + "&time=" + time;
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


}
