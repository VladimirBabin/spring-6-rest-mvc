package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void convertCsv() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        List<BeerCSVRecord> records = beerCsvService.convertCSV(file);
        assertThat(records.size()).isPositive().isEqualTo(2410);
    }
}