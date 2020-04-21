package project;

import java.util.concurrent.TimeUnit;

// Class to store global variables
public class proprieties {

    public static final String token = "0d185058c1ba213d1c422bba3d7bb4a08e7564b7"; // api token for https://aqicn.org
    public static final boolean debug = true;       // shows some messages in the terminal
    public static final boolean use_cache = true;   // whether to use cache or not
    public static final int cache_time = 60;    //  how long til a cached value is considered valid ( in seconds)
    public static final int citiesToGet = 15;    // when populating the cache, how many cities to get from the api
    public static final String citiesCSV = "worldcities.csv";

}