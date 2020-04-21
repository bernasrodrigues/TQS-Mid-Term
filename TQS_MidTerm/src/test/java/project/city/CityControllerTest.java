package project.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.AirQualityApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AirQualityApplication.class)
class CityControllerTest {

    @Autowired
    private CityController cityController;

    @Autowired
    private CityRepository repository;


    @BeforeEach
    @Transactional
    public void setUp() {
        City lisboa = new City("Lisboa" , new ArrayList<Double>(Arrays.asList(80.0, 80.0)) , 80);
        lisboa.setTimestamp(new Date());    // updated

        City outdated = new City("Outdated" , new ArrayList<Double>(Arrays.asList(0.0, 0.0)) , 0);
        outdated.setTimestamp(new Date(System.currentTimeMillis()-10*60*1000));  // 10 minutes outdated

        repository.save(lisboa);
        repository.save(outdated);
        repository.save(new City("Aveiro" , new ArrayList<>(Arrays.asList(50.0, 80.0)) , 90));
        repository.save(new City("Madrid" , new ArrayList<>(Arrays.asList(60.0, 80.0)) , 70));
    }

    @Test   // If in cache check return true
    @Transactional
    void checkCacheUpdated() {
        assertTrue(cityController.checkCache("Lisboa").getexists());
    }

    @Test   // If not in cache return false
    @Transactional
    void checkCacheNonExistant() {
        assertFalse(cityController.checkCache("Cairo").getexists());
    }

    @Test   // If in cache but outdated return false
    @Transactional
    void checkCacheOutdated(){
        assertFalse(cityController.checkCache("Outdated").getexists());
    }


    @Test
    @Transactional
    void getDateDiff() {
        Date time = new Date(System.currentTimeMillis()+5*60*1000); // (5 minutes from current time)
        assertEquals(cityController.getDateDiff(time,time) , 0);  //Test if it makes the right calculus
        long timeCached = cityController.getDateDiff(time, repository.findByNameContainsIgnoreCase("Lisboa").getTimestamp());
        assertTrue(timeCached <= 5);
    }
}