package dk.mmr.responseaggregator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Offers  implements Serializable {
    private String CompanyName;
    private ArrayList<FlightBooking> list;

    public Offers(ArrayList<FlightBooking> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Offers{" +
                "CompanyName='" + CompanyName + '\'' +
                ", list=" + list +
                '}';
    }
}
