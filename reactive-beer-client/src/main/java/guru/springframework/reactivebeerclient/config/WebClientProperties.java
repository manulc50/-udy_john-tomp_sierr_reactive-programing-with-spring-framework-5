package guru.springframework.reactivebeerclient.config;

public class WebClientProperties {

	public static final String BASE_URL = "http://api.springframework.guru";
	public static final String BEER_V1_PATH = "/api/v1/beer";
	public static final String BEER_V1_GET_BY_ID_PATH = "/api/v1/beer/{beerId}";
	public static final String BEER_V1_GET_BY_UPC_PATH= "/api/v1/beerUpc/{beerUpc}";
	public static final String BEER_V1_GET_ALL_PATH = "/api/v1/beer";
}
