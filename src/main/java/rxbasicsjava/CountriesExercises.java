package rxbasicsjava;

import io.reactivex.Observable;
import io.reactivex.Single;
import rxbasicsjava.types.Country;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

class CountriesExercises {


	public Single<String> countryNameInCapitals(Country country) {
		throw new NotImplementedException();
	}

	public Single<Integer> countCountries(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<String> listNameOfEachCountry(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
		throw new NotImplementedException();
	}

	public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
		throw new NotImplementedException();
	}

	public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
	                                                 Observable<Country> countryObservable2) {
		throw new NotImplementedException();
	}

	public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
	                                                Observable<Country> countryObservable2) {
		throw new NotImplementedException();
	}
}
