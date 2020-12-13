package dk.mmr.responseaggregator;

import dk.mmr.responseaggregator.entity.Offers;
import dk.mmr.responseaggregator.service.FlightClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestFunc {
    public static void main(String[] args) {
        FlightClient fc = new FlightClient();

        List<Offers> list = fc.getAllOffers("bar", "cph",1607890533L);
        list.forEach(offers -> System.out.println(offers));
    }
}
