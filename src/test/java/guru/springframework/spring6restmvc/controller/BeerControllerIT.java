package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerMapper beerMapper;

    @Test
    void testListBeers() {
        List<BeerDTO> beerDTOS = beerController.listBeers();

        assertThat(beerDTOS).hasSize(3);
    }

    @Transactional
    @Rollback
    @Test
    void testEmptyList() {
        // given
        beerRepository.deleteAll();

        // when
        List<BeerDTO> beerDTOS = beerController.listBeers();

        // then
        assertThat(beerDTOS).isEmpty();
    }

    @Test
    void testGetById() {
        // given
        Beer beer = beerRepository.findAll().get(0);

        // when
        BeerDTO dto = beerController.getBeerById(beer.getId());

        // then
        assertThat(dto).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                beerController.getBeerById(UUID.randomUUID()));
    }

    @Transactional
    @Rollback
    @Test
    void saveNewBeerTest() {
        // given
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("New Beer")
                .build();

        // when
        ResponseEntity responseEntity = beerController.handlePost(beerDTO);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[3]);

        Optional<Beer> byId = beerRepository.findById(savedUUID);
        assertThat(byId).isNotEmpty().get().isNotNull();
    }

    @Transactional
    @Rollback
    @Test
    void updateExistingBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDto = beerMapper.beerToBeerDto(beer);
        beerDto.setId(null);
        beerDto.setVersion(null);
        final String beerName = "UPDATED";
        beerDto.setBeerName(beerName);

        ResponseEntity responseEntity = beerController.updateById(beer.getId(), beerDto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    }

    @Test
    void testUpdateWhenIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Transactional
    @Rollback
    @Test
    void deleteByIdWhenFound() {
        Beer beer = beerRepository.findAll().get(0);

        ResponseEntity responseEntity = beerController.deleteById(beer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void deleteByIdWhenNotFound() {
        // given
        UUID id = UUID.randomUUID();

        // then
        assertThrows(NotFoundException.class, () ->
                beerController.deleteById(UUID.randomUUID()));

    }

    @Transactional
    @Rollback
    @Test
    void patchExistingBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDto = beerMapper.beerToBeerDto(beer);
        beerDto.setId(null);
        beerDto.setVersion(null);
        final String beerName = "UPDATED";
        beerDto.setBeerName(beerName);

        ResponseEntity responseEntity = beerController.updateBeerPatchById(beer.getId(), beerDto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    }

    @Test
    void testPatchWhenIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                beerController.updateBeerPatchById(UUID.randomUUID(), BeerDTO.builder().build()));
    }
}