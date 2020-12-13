package dk.mmr.responseaggregator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightBooking implements Serializable {
    private String cityFrom, cityTo;
    private Long time;
    private int price;

}
