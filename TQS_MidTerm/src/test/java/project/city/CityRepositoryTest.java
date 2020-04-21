package project.city;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CityRepositoryTest {


    @Autowired
    private CityRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void whenFindByNameThenReturnCity() {
        City lisboa = new City("Lisboa" , new ArrayList<Double>(Arrays.asList(80.0, 80.0)) , 80);
        entityManager.persistAndFlush(lisboa);

        City found = repository.findByNameContainsIgnoreCase(lisboa.getName());
        assertThat(found.getName()).isEqualTo(lisboa.getName());
    }


    @Test
    public void whenInvalidNameThenReturnNull() {
        City fromDb = repository.findByNameContainsIgnoreCase("doesNotExist");
        assertThat(fromDb).isNull();
    }

    @Test
    public void givenSetOfCitiesThenFindAllThenReturnAllEmployees() {
        City lisboa = new City("Lisboa" , new ArrayList<Double>(Arrays.asList(80.0, 80.0)) , 80);
        City aveiro = new City("Aveiro" , new ArrayList<Double>(Arrays.asList(80.0, 80.0)) , 80);
        City porto = new City("Porto" , new ArrayList<Double>(Arrays.asList(80.0, 80.0)) , 80);

        entityManager.persist(lisboa);
        entityManager.persist(aveiro);
        entityManager.persist(porto);
        entityManager.flush();

        List<City> allcities = repository.findAll();

        assertThat(allcities).hasSize(3).extracting(City::getName).containsOnly(lisboa.getName(), aveiro.getName(), porto.getName());
    }




}
