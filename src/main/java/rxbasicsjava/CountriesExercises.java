package rxbasicsjava;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import rxbasicsjava.types.Country;

class CountriesExercises {


    public Single<String> countryNameInCapitals(Country country) {
        return Single.just(country.getName().toUpperCase());
    }

    public Single<Integer> countCountries(List<Country> countries) {
        return Observable.fromIterable(countries)
                .count()
                .map(Long::intValue);
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(Country::getPopulation);
    }

    public Observable<String> listNameOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(Country::getName);
    }

    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
        //other excersize - do without skip..?
        return Observable.fromIterable(countries)
                .skip(3)
                .take(2);
    }

    public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                .all(country -> country.getPopulation() > 1_000_000);
    }

    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                .filter(country -> country.getPopulation() > 1_000_000);
    }

    public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork, 100, TimeUnit.MILLISECONDS)
                .onErrorReturnItem(Collections.emptyList())
                .flatMapIterable(o -> o)
                .filter(country -> country.getPopulation() > 1_000_000);
    }

    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
        return Observable.fromIterable(countries)
                .filter(country -> country.getName().equals(countryName))
                .map(Country::getCurrency)
                .first("USD")
                .toObservable();
    }

    private ObservableTransformer<Country, Long> sumPopulationsTransformer() {
        return upstream -> upstream
                .map(Country::getPopulation)
                .reduce(0L, (accum, item) -> accum + item)
                .toObservable();
    }

    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
        return Observable.fromIterable(countries)
                .compose(sumPopulationsTransformer());
    }

    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.fromIterable(countries)
                .toMap(Country::getName, Country::getPopulation);
    }

    public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
                                                     Observable<Country> countryObservable2) {
        return Observable.merge(countryObservable1, countryObservable2)
                .compose(sumPopulationsTransformer());
    }

    public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
                                                    Observable<Country> countryObservable2) {
        final Country terminator = new Country("end", "USD", 0L);
        return Observable.zip(
                Observable.concat(countryObservable1, Observable.just(terminator)),
                Observable.concat(countryObservable2, Observable.just(terminator)),
                (first, second) -> first.getName().equals(second.getName()) &&
                        first.getPopulation() == second.getPopulation() &&
                        first.getCurrency().equals(second.getCurrency()))
                .filter(isEqual -> !isEqual)
                .first(true);
    }
}
