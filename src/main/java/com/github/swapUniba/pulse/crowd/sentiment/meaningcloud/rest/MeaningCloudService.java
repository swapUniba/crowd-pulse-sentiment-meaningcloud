package com.github.swapUniba.pulse.crowd.sentiment.meaningcloud.rest;

import com.github.frapontillo.pulse.util.PulseLogger;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * MeaningCloud Service class.
 * @author Cosimo Lovascio
 *
 */
public class MeaningCloudService {

    private static final Logger logger = PulseLogger.getLogger(MeaningCloudService.class);

    private static final String PROP_API_KEYS = "meaningcloud.keys";
    private static final String MEANINGCLOUD_ENDPOINT = "https://api.meaningcloud.com";
    private static final String SENTIMENT_API = "/sentiment-2.1";
    private static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    private static String[] SECRET_KEY;
    private static byte serviceNumber = 0;

    /*
     * Load the properties.
     */
    static {
        Properties prop = new Properties();

        try {
            InputStream configInput =
                MeaningCloudService.class.getClassLoader().getResourceAsStream("meaningcloud.properties");

            prop.load(configInput);
            String keys = prop.getProperty(PROP_API_KEYS);
            SECRET_KEY = keys.split(",");

        } catch (IOException noFileException) {
            logger.error("Error during MeaningCloud configuration loading", noFileException);

            SECRET_KEY = new String[1];
            SECRET_KEY[0] = "";
        }

    }

    /**
     * Make a request to MeaningCloud Web Service.
     * @param lang the specified text language ("auto" to detect automatically)
     * @param text the text to analyze
     * @throws Exception
     */
    public MeaningCloudResponse makeRequest(String lang, String text) throws Exception {
        logger.info("Using API KEY " + SECRET_KEY[serviceNumber]);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse(MEDIA_TYPE);
        String requestParams = "key=" + SECRET_KEY[serviceNumber] + "&lang=" + lang + "&txt=" + text;
        RequestBody body = RequestBody.create(mediaType, requestParams);
        Request request = new Request.Builder()
                .url(MEANINGCLOUD_ENDPOINT + SENTIMENT_API)
                .post(body)
                .addHeader("content-type", MEDIA_TYPE)
                .build();

        Response response = client.newCall(request).execute();
        Gson gsonResponse = new Gson();
        MeaningCloudResponse meaningCloudResponse =
                gsonResponse.fromJson(response.body().string(), MeaningCloudResponse.class);

        logger.info("Remaining requests for API KEY " + SECRET_KEY[serviceNumber] + " : " + meaningCloudResponse.status.remaining_credits);

        //check remaining requests
        if (meaningCloudResponse.status.remaining_credits <= 0) {
            serviceNumber += 1;

            if (serviceNumber >= SECRET_KEY.length) {
                serviceNumber = 0;
                logger.info("All API KEY used!");
                throw new Exception("MeaningCloud request limit reached");

            } else {
                logger.info("Switching API KEY to " +  SECRET_KEY[serviceNumber]);

                // recursive call to the same method
                meaningCloudResponse = makeRequest(lang, text);
            }
        }

        return meaningCloudResponse;
    }

}
