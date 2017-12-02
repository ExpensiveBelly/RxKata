package rxbasicsjava;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import rxbasicsjava.types.Country;

import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CountriesExercisesTest {

	private CountriesSolutions solutions;
	private List<Country> allCountries;

	@Rule
	public Timeout globalTimeout = Timeout.seconds(2);

	@Before
	public void setUp() {
		solutions = new CountriesSolutions();
		allCountries = CountriesTestProvider.countries();
	}

	@Test
	public void rx_CountryNameInCapitals() {
		Country testCountry = CountriesTestProvider.countries().get(0);
		String expected = testCountry.getName().toUpperCase(Locale.US);
		TestObserver<String> testObserver = solutions
				.countryNameInCapitals(testCountry)
				.test();
		testObserver.assertNoErrors();
		testObserver.assertValue(expected);
	}

	@Test
	public void rx_CountAmountOfCountries() {
		Integer expected = CountriesTestProvider.countries().size();
		TestObserver<Integer> testObserver = solutions
				.countCountries(allCountries)
				.test();
		testObserver.assertNoErrors();
		testObserver.assertValue(expected);
	}

	@Test
	public void rx_ListPopulationOfEachCountry() {
		List<Long> expectedResult = CountriesTestProvider.populationOfCountries();
		TestObserver<Long> testObserver = solutions
				.listPopulationOfEachCountry(allCountries)
				.test();
		testObserver.assertValueSet(expectedResult);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_ListNameOfEachCountry() {
		List<String> expectedResult = CountriesTestProvider.namesOfCountries();
		TestObserver<String> testObserver = solutions
				.listNameOfEachCountry(allCountries)
				.test();
		testObserver.assertValueSet(expectedResult);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_ListOnly3rdAnd4thCountry() {
		List<Country> expectedResult = new ArrayList<>();
		expectedResult.add(allCountries.get(3));
		expectedResult.add(allCountries.get(4));

		TestObserver<Country> testObserver = solutions
				.listOnly3rdAnd4thCountry(allCountries)
				.test();
		testObserver.assertValueSet(expectedResult);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_IsAllCountriesPopulationMoreThanOneMillion_Positive() {
		TestObserver<Boolean> testObserver = solutions
				.isAllCountriesPopulationMoreThanOneMillion(CountriesTestProvider.countriesPopulationMoreThanOneMillion())
				.test();
		testObserver.assertResult(true);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_IsAllCountriesPopulationMoreThanOneMillion_Negative() {
		TestObserver<Boolean> testObserver = solutions
				.isAllCountriesPopulationMoreThanOneMillion(allCountries)
				.test();
		testObserver.assertResult(false);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_ListPopulationMoreThanOneMillion() {
		List<Country> expectedResult = CountriesTestProvider.countriesPopulationMoreThanOneMillion();
		TestObserver<Country> testObserver = solutions
				.listPopulationMoreThanOneMillion(allCountries)
				.test();
		testObserver.assertValueSet(expectedResult);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_ListPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty_When_NoTimeout() {
		FutureTask<List<Country>> futureTask = new FutureTask<>(() -> {
			TimeUnit.MILLISECONDS.sleep(100);
			return allCountries;
		});
		new Thread(futureTask).start();
		TestObserver<Country> testObserver = solutions
				.listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(futureTask)
				.test();
		List<Country> expectedResult = CountriesTestProvider.countriesPopulationMoreThanOneMillion();
		testObserver.awaitTerminalEvent();
		testObserver.assertComplete();
		testObserver.assertValueSet(expectedResult);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_ListPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty_When_Timeout() {
		FutureTask<List<Country>> futureTask = new FutureTask<>(() -> {
			TimeUnit.HOURS.sleep(1);
			return allCountries;
		});
		new Thread(futureTask).start();
		TestObserver<Country> testObserver = solutions
				.listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(futureTask)
				.test();
		testObserver.awaitTerminalEvent();
		testObserver.assertComplete();
		testObserver.assertNoValues();
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_GetCurrencyUsdIfNotFound_When_CountryFound() {
		String countryRequested = "Austria";
		String expectedCurrencyValue = "EUR";
		TestObserver<String> testObserver = solutions
				.getCurrencyUsdIfNotFound(countryRequested, allCountries)
				.test();
		testObserver.assertResult(expectedCurrencyValue);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_GetCurrencyUsdIfNotFound_When_CountryNotFound() {
		String countryRequested = "Senegal";
		String expectedCurrencyValue = "USD";
		TestObserver<String> testObserver = solutions
				.getCurrencyUsdIfNotFound(countryRequested, allCountries)
				.test();
		testObserver.assertResult(expectedCurrencyValue);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_SumPopulationOfCountries() {
		// hint: use "reduce" operator
		TestObserver<Long> testObserver = solutions
				.sumPopulationOfCountries(allCountries)
				.test();
		testObserver.assertResult(CountriesTestProvider.sumPopulationOfAllCountries());
		testObserver.assertNoErrors();
	}


	@Test
	public void rx_MapCountriesToNamePopulation() {
		TestObserver<Map<String, Long>> values = solutions.mapCountriesToNamePopulation(allCountries).test();
		Map<String, Long> expected = new HashMap<>();
		for (Country country : allCountries) {
			expected.put(country.getName(), country.getPopulation());
		}
		values.assertResult(expected);
		values.assertNoErrors();
	}

	@Test
	public void rx_sumPopulationOfCountries() {
		// hint: use "map" operator
		TestObserver<Long> testObserver = solutions
				.sumPopulationOfCountries(Observable.fromIterable(allCountries), Observable.fromIterable(allCountries))
				.test();
		testObserver.assertResult(CountriesTestProvider.sumPopulationOfAllCountries()
				+ CountriesTestProvider.sumPopulationOfAllCountries());
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_areEmittingSameSequences_Positive() {
		// hint: use "sequenceEqual" operator
		TestObserver<Boolean> testObserver = solutions
				.areEmittingSameSequences(Observable.fromIterable(allCountries), Observable.fromIterable(allCountries))
				.test();
		testObserver.assertResult(true);
		testObserver.assertNoErrors();
	}

	@Test
	public void rx_areEmittingSameSequences_Negative() {
		List<Country> allCountriesDifferentSequence = new ArrayList<>(allCountries);
		Collections.swap(allCountriesDifferentSequence, 0, 1);
		TestObserver<Boolean> testObserver = solutions
				.areEmittingSameSequences(
						Observable.fromIterable(allCountries),
						Observable.fromIterable(allCountriesDifferentSequence))
				.test();
		testObserver.assertResult(false);
		testObserver.assertNoErrors();
	}

}