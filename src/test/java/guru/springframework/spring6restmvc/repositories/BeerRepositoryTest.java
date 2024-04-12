package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testGetBeerByStyleAndNameLike() {
        Page<Beer> allByStyleAndName = beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle.IPA, "%IPA%", null);

        assertThat(allByStyleAndName).hasSize(310);
    }

    @Test
    void testGetBeerByBeerStyle() {
        Page<Beer> allByBeerStyle = beerRepository.findAllByBeerStyle(BeerStyle.ALE, null);

        assertThat(allByBeerStyle).hasSize(400);
    }

    @Test
    void testGetBeerByName() {
        Page<Beer> listIpa = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(listIpa).hasSize(336);
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                        .beerName("My beer")
                        .beerStyle(BeerStyle.IPA)
                        .upc("2352352")
                        .price(new BigDecimal("11.99"))
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void testSaveBeerTooLongName() {

        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(Beer.builder()
                    .beerName("My_beer_nameMy_beer_nameMy_beer_nameMy_beer_nameMy_beer_name")
                    .beerStyle(BeerStyle.IPA)
                    .upc("2352352")
                    .price(new BigDecimal("11.99"))
                    .build());

            beerRepository.flush();
        });
    }
}