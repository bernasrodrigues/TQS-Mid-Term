package project.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CityRepository extends JpaRepository<City, Long> {

    City findByNameContainsIgnoreCase(String name);

    void deleteCitiesByNameContainsIgnoreCase(String name);

    List<City> findAll();
    void deleteAll();

}