package rxbasicsjava;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import rxbasicsjava.types.Country;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

class CountriesSolutions {

	public Single<String> countryNameInCapitals(Country country) {
		return Single.just(country.getName().toUpperCase(Locale.US)); // solution
	}

	public Single<Integer> countCountries(List<Country> countries) {
		return Single.just(countries.size()); // solution
	}

	public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
		return Observable.fromIterable(countries) // solution
				.map(Country::getPopulation);
	}


	public Observable<String> listNameOfEachCountry(List<Country> countries) {
		return Observable.fromIterable(countries) // solution
				.map(Country::getName);
	}


	public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
		return Observable.fromIterable(countries) // solution
				.skip(3)
				.take(2);
	}


	public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
		return Observable.fromIterable(countries)  // solution
				.all(country -> country.getPopulation() > 1000000);
	}


	public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
		return Observable.fromIterable(countries)  // solution
				.filter(country -> country.getPopulation() > 1000000);
	}


	public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
		return Observable.fromFuture(countriesFromNetwork, Schedulers.io()) // solution
				.flatMap(Observable::fromIterable)
				.filter(country -> country.getPopulation() > 1000000)
				.timeout(1, TimeUnit.SECONDS, Observable.empty());
	}


	public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
		return Observable.fromIterable(countries) // solution
				.filter(country -> country.getName().equals(countryName))
				.map(Country::getCurrency)
				.defaultIfEmpty("USD");
	}


	public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
		return Observable.fromIterable(countries)  // solution
				.map(Country::getPopulation)
				.reduce((i1, i2) -> i1 + i2)
				.toObservable();
	}


	public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
		return Observable.fromIterable(countries)  // solution
				.toMap(
						Country::getName,
						Country::getPopulation);
	}


	public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
	                                                 Observable<Country> countryObservable2) {
		return Observable.merge(countryObservable1, countryObservable2)
				.map(Country::getPopulation)
				.reduce((i1, i2) -> i1 + i2)
				.toObservable();
	}


	public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
	                                                Observable<Country> countryObservable2) {
		return Observable.sequenceEqual(countryObservable1, countryObservable2);
	}
}
