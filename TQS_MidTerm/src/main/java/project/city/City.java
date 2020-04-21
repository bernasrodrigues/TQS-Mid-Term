package project.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.proprieties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter @Setter @NoArgsConstructor @Data
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer aqi;
    @ElementCollection
    private List<Double> geo;
    @JsonIgnore     // the data variable is the collection of all the information from the api
    @Lob            // data is usually a large string, the lob annotation is used to handle large texts for SQL
    private String data;
    private Date timestamp; // time when its inserted in cache

    public City(String name,List<Double> geo, Integer aqi) {
        this.name = name;
        this.geo = geo;
        this.aqi = aqi;
        this.data = "sem dados";
    }

    // Json deserializer ( the more important metrics are proprly handled, the rest of the data is stored in bulk ( to simplify )
    @JsonProperty("data")
    private void unpackNested(Object data) {
        try {
            Map<String,Object> data2 = (Map<String,Object>) data;
            @SuppressWarnings("unchecked")
            Map<String, Object> city = (Map<String, Object>) data2.get("city");
            this.name = (String) city.get("name");
            this.geo = (ArrayList<Double>) city.get("geo");
            this.aqi = (Integer) data2.get("aqi");
            this.data = data.toString();
            this.timestamp = new Date();
        }
        catch (ClassCastException e){
            this.name = "error";
            if (proprieties.debug) {System.out.println("Erro ao tentar criar cidade"); };
        }
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", aqi=" + aqi +
                ", geo=" + geo +
                ", timestamp=" + timestamp;
    }

}
