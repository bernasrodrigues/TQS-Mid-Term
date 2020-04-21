package project.city;

import java.util.List;

public interface CityService {

    City getCityById(Long id);
    City getCityByName(String name);
    City getCityByGeo(List<Double> geo);
    List<City> getAllCities();

    City save(City city);

    void deleteAll();
    void deleteCityByName(String name);
}