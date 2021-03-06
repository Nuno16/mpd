
import model.DayInfo;
import model.Location;
import org.junit.Test;
import utils.HttpRequest;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.time.LocalDate.of;
import static org.junit.Assert.assertEquals;


public class WeatherServiceTests {

    @Test
    public void retrieveLocationsNamedLisbonTest() {

        final int expectedCount = 2;

        WeatherService weather =
                new WeatherService(new WeatherWebApi(new HttpRequest()));
        CompletableFuture<Stream<Location>> locations =
                weather.search("Lisbon");

        long count= locations.join().count();

        assertEquals(expectedCount, count);
    }

    @Test
    public void getMoonPhaseOfDate() {
        WeatherService weather =
                new WeatherService(new WeatherWebApi(new HttpRequest()));

        Optional<Location> lisbon =
            weather.search("Lisbon")
            .thenApply( s -> s.filter(  l -> l.getCountry()
                        .equalsIgnoreCase("portugal"))
                        .findFirst()).join();

        assertEquals(true, lisbon.isPresent());

        LocalDate day = LocalDate.of(2019, 2, 12);

        Stream<DayInfo> days =
                lisbon.get()
                .getDays(day,day)
                .join();

        Optional<DayInfo> firstDay = days.findFirst();

        assertEquals(true, firstDay.isPresent());
        System.out.println(firstDay.get().getMoonPhase());
    }


    @Test
    public void getDays2and3Feb() {
        WeatherService weather = new WeatherService(new WeatherWebApi(new HttpRequest()));

        LocalDate day1 = of(2019, 2, 2);
        LocalDate day2 = of(2019, 2, 3);

        CompletableFuture<Stream<DayInfo>> days =
                weather.search("Lisbon")
                .thenApply(s ->
                    s.filter( l ->
                                l.getCountry()
                                .equalsIgnoreCase("portugal"))
                            .findFirst()
                            .get()
                )
                .thenCompose(loc -> loc.getDays(day1, day2));


        long observationsCount =
            days.thenCompose(s ->
                s.map(d -> d.getTemperatures()
                    .thenApply(s1 -> s1.count())
                )
                .reduce((f1,f2) -> f1.thenCombine(f1, (l1,l2) -> l1+l2))
                .get()
            )
            .join();
            assertEquals(48, observationsCount);

    }

}
