package project.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CityRepositoryTestMocks {

    @Mock(lenient = true)
    private CityRepository cityRepository;

    @InjectMocks
    private CityServiceImp cityService;

    @BeforeEach
    public void setUp() {
        City lisboa = new City("Lisboa", new ArrayList<>(Arrays.asList(80.0, 80.0)), 80);
        lisboa.setTimestamp(new Date());    // updated

        City aveiro = new City("Aveiro", new ArrayList<>(Arrays.asList(90.0, 90.0)), 100);
        lisboa.setTimestamp(new Date());

        City outdated = new City("Outdated", new ArrayList<>(Arrays.asList(0.0, 0.0)), 0);
        outdated.setTimestamp(new Date(System.currentTimeMillis() - 10 * 60 * 1000));  // 10 minutes outdated

        List<City> allCities = Arrays.asList(lisboa, aveiro, outdated);

        Mockito.when(cityRepository.findByNameContainsIgnoreCase(lisboa.getName())).thenReturn(lisboa);
        Mockito.when(cityRepository.findByNameContainsIgnoreCase(aveiro.getName())).thenReturn(aveiro);
        Mockito.when(cityRepository.findByNameContainsIgnoreCase(outdated.getName())).thenReturn(aveiro);
        Mockito.when(cityRepository.findByNameContainsIgnoreCase("Error")).thenReturn(null);
        Mockito.when(cityRepository.findAll()).thenReturn(allCities);
    }

    @Test
    public void whenValidName_thenCityShouldBeFound() {
        String name = "Lisboa";
        City found = cityService.getCityByName(name);

        assertThat(found.getName()).isEqualTo(name);
    }

    @Test
    public void whenInValidName_thenCityShouldNotBeFound() {
        City fromDb = cityService.getCityByName("Error");
        assertThat(fromDb).isNull();

        verifyFindByNameIsCalledOnce("Error");
    }


    private void verifyFindByNameIsCalledOnce(String name) {
        Mockito.verify(cityRepository, VerificationModeFactory.times(1)).findByNameContainsIgnoreCase(name);
        Mockito.reset(cityRepository);
    }




}