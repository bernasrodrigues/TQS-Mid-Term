package project.city;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import project.proprieties;


import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Basics tests to text if the City class properlly handles features like deserialization from the API
class CityTest {

    @Test
    void testCreate() {
        RestTemplate restTemplate = new RestTemplate();
        final String uri = "http://api.waqi.info/feed/Lisboa/?token=" + proprieties.token;
        City testCity = restTemplate.getForObject(uri, City.class);
        assertEquals("Entrecampos, Lisboa, Portugal", testCity.getName());
        assertEquals("String" , testCity.getData().getClass().getSimpleName());
        long timeDiference = testCity.getTimestamp().getTime() - new Date().getTime();
        assert timeDiference <= 100; // Check if the time diference from the City creation is small in comparison to the current Time (in milliseconds)
    }

    // To test when it doesn't found a city from the API it can create an "empty city"
    @Test
    void testCreateNotFound() {
        RestTemplate restTemplate = new RestTemplate();
        final String uri = "http://api.waqi.info/feed/Cairo/?token=" + proprieties.token;   // Api doesn't recognize city of Cairo, so sends an error message
        City testCity = restTemplate.getForObject(uri, City.class);
        assertEquals("error" , testCity.getName() );
        assertEquals(null , testCity.getData());
    }
}