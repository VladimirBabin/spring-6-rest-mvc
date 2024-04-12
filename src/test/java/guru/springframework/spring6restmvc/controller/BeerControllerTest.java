package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerById() throws Exception {

        BeerDTO testBeerDTO = beerServiceImpl.listBeers().get(0);

        given(beerService.getBeerById(testBeerDTO.getId())).willReturn(Optional.of(testBeerDTO));

        mockMvc.perform(get(BEER_PATH_ID, testBeerDTO.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeerDTO.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeerDTO.getBeerName())));
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers()).willReturn(beerServiceImpl.listBeers());

        mockMvc.perform(get(BEER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getBeerByIdNotFound() throws Exception {
        given(beerService.getBeerById(any(UUID.class)))
                .willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateNewBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        beerDTO.setVersion(null);
        beerDTO.setId(null);

        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers().get(1));

        mockMvc.perform(post(BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testUpdateBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        given(beerService.updateBeerById(any(), any()))
                .willReturn(Optional.of(beerDTO));

        mockMvc.perform(put(BEER_PATH_ID, beerDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent());
        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        given(beerService.deleteById(any()))
                .willReturn(true);

        mockMvc.perform(delete(BEER_PATH_ID, beerDTO.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(beerService).deleteById(uuidArgumentCaptor.capture());

        assertThat(beerDTO.getId()).isEqualTo(uuidArgumentCaptor.getValue());

    }

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        given(beerService.patchBeerById(any(), any()))
                .willReturn(Optional.of(beerDTO));

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name");

        mockMvc.perform(patch(BEER_PATH_ID, beerDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beerDTO.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }

    @Test
    void testCreateBeerNullBeerName() throws Exception {
        // given
        BeerDTO dto = BeerDTO.builder()
                .beerStyle(BeerStyle.IPA)
                .upc("523523523")
                .price(new BigDecimal("11.99"))
                .build();
        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers().get(1));

        // when
        mockMvc.perform(
                        post(BEER_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void whenUpdateBeerWithBlankBeerNameThanBadRequest() throws Exception {
        // given
        BeerDTO dto = beerServiceImpl.listBeers().get(0);
        dto.setBeerName("");

        // when
        mockMvc.perform(
                        put(BEER_PATH_ID, dto.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}