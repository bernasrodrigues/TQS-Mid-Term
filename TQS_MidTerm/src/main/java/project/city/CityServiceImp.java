package project.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CityServiceImp implements CityService{

    @Autowired
    private CityRepository cityRepository;


    @Override
    public City getCityById(Long id) {
        return cityRepository.findById(id).orElse(null);
    }


    @Override
    public City getCityByName(String name) {
        return cityRepository.findByNameContainsIgnoreCase(name);
    }

    @Override
    public City getCityByGeo(List<Double> geo) {
        List<City> cities = cityRepository.findAll();
        for (City geo_city:cities){
            double distance =  Math.sqrt(Math.pow(geo.get(0) - geo_city.getGeo().get(0),2) + Math.pow(geo.get(1) - geo_city.getGeo().get(1),2)) ; // distance formula
            if (distance < 0.1) return geo_city;
        }
        return null;
    }

    @Override
    public void deleteAll() {
        cityRepository.deleteAll();
    }

    @Override
    public void deleteCityByName(String city) {
        cityRepository.deleteCitiesByNameContainsIgnoreCase(city);
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City save(City city) {
        return cityRepository.save(city);
    }
}