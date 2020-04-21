package project.city;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import project.proprieties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class CityController {

    @Autowired
    private CityService cityService;

    // find data of the designated city ( default city = Lisboa )
    @GetMapping("city")
    public City city(@RequestParam(value="city", defaultValue="lisboa") String city){
        Pair rep_city = checkCache(city);
        if (rep_city.getexists()){
            return rep_city.getCity();
        }
        else {
            final String uri = "http://api.waqi.info/feed/" + city + "/?token=" + proprieties.token;
            RestTemplate restTemplate = new RestTemplate();
            City apiCity = Objects.requireNonNull(restTemplate.getForObject(uri, City.class));
            if (!apiCity.getName().equals("error")){
                if (proprieties.debug) {System.out.println("Cidade adicionada: " + apiCity.getName());};
                cityService.save(apiCity);
            }
            return apiCity;
        }
    }

    // search nearest city based on IP locations
    @GetMapping("here")
    public City Nearest_City(){
        final String uri = "http://api.waqi.info/feed/here/?token=" + proprieties.token;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, City.class);
    }


    @GetMapping("geo")
    public City Geo(@RequestParam(value="lat", defaultValue = "38.7") String lat,
                    @RequestParam(value="lng", defaultValue = "-9.1") String lng){
        ArrayList<Double> coordinates = new ArrayList<>(Arrays.asList(Double.parseDouble(lat), Double.parseDouble(lng)));

        Pair repCity = checkCache(coordinates);
        if (repCity.getexists()){
            return repCity.getCity();
        }
        else {
            final String uri = "https://api.waqi.info/feed/geo:" + lat + ";" + lng + "/?token=" + proprieties.token;
            RestTemplate restTemplate = new RestTemplate();
            City apiCity = Objects.requireNonNull(restTemplate.getForObject(uri, City.class));
            cityService.save(apiCity);
            return apiCity;
        }

    }


    @GetMapping("populate")
    public String populate(){
        RestTemplate restTemplate = new RestTemplate();
        if (proprieties.debug) {System.out.println("Adding cities to cache");}
        ArrayList<String> cities = readCSV("src/" + proprieties.citiesCSV);

        for (String city:cities){
            String uri = "http://api.waqi.info/feed/" + city + "/?token=" + proprieties.token;
            if (proprieties.debug) {System.out.println("Buscando cidade de: " + uri);}
            City api_city = Objects.requireNonNull(restTemplate.getForObject(uri, City.class));
            if (!api_city.getName().equals("error")){
                if (proprieties.debug) {System.out.println("Cidade adicionada: " + api_city.getName());};
                cityService.save(api_city);
            }else if (proprieties.debug) {System.out.println("cidade não adicionada");}
        }
        return "cache populada";
    }



    @GetMapping("all")
    public List<City> all(){
        if (proprieties.debug) {System.out.println("Showing all cities");}
        return cityService.getAllCities();
    }

    @GetMapping("clear")
    public String clear(){
        if (proprieties.debug) {System.out.println("Deleting cache");}
        cityService.deleteAll();
        return "Cache apagada";
    }





    // ##############################################################################################################
    public ArrayList<String> readCSV(String fileName){
        BufferedReader br = null;
        String line = "";
        ArrayList<String> names = new ArrayList<>();
        int totalCities = 0;
        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null && totalCities < proprieties.citiesToGet) {

                // use comma as separator
                String[] cities = line.split(",");
                names.add(cities[0].replace("\"",""));
                totalCities += 1;
            }
        } catch (IOException e) {
            System.out.println("Erro na leitura de ficheiro");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Erro no encerramento do leitor de ficheiro");
                    e.printStackTrace();
                }
            }
        }
        return names;

    }




    // Method that checks the cache and returns the cached city (if it exists)
    public Pair checkCache(Object city){

        City repCity = null;
        if (city.getClass().isInstance("String")){
            if (proprieties.debug) {System.out.println("Searching in cache for city: " + city); }
            repCity = cityService.getCityByName((String) city);
        }
        else{
            if (proprieties.debug) {System.out.println("Searching in cache for coordinates like: " + city); }
            repCity = cityService.getCityByGeo((ArrayList<Double>) city);
        }



        if (repCity == null) {
            if (proprieties.debug) {System.out.println("City in cache not found"); }
            return new Pair(false, null);
        }

        if (proprieties.debug) {System.out.println("Cached city: " + repCity); }
        long cacheTime = getDateDiff(repCity.getTimestamp() , new Date());
        if (proprieties.debug)  System.out.println("Time in cache: " + cacheTime + " seconds");

        if (cacheTime >= proprieties.cache_time){
            if (proprieties.debug)  System.out.println("Too long in cache");
            cityService.deleteCityByName(repCity.getName());
            if (proprieties.debug)  System.out.println("Cached city deleted (" + city + ")" );
            return new Pair(false , null);
        }
        return new Pair(true , repCity);
    }

    // Calculate time diference
    public long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toSeconds(diffInMillies);
    }




    // Class used to help with checkCache function return parameter
    public static final class Pair{
        private final boolean exists;
        private final City city;

        Pair(boolean exists, City city) {
            this.exists = exists;
            this.city = city;
        }
        public boolean getexists() {
            return this.exists;
        }
        public City getCity() {
            return this.city;
        }
    }

}
