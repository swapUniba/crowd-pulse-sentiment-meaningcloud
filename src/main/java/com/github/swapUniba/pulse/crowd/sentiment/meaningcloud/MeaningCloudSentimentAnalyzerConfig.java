package com.github.swapUniba.pulse.crowd.sentiment.meaningcloud;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.google.gson.JsonElement;

/**
 * Plugin configuration class.
 */
public class MeaningCloudSentimentAnalyzerConfig implements IPluginConfig<MeaningCloudSentimentAnalyzerConfig> {

    /**
     * Calculate the sentiment of all messages coming from the stream.
     */
    public static final String ALL = "all";

    /**
     * Calculate the sentiment of the messages with no sentiment (property is null).
     */
    public static final String NEW = "new";

    /**
     * Calculate the sentiment of the messages with sentiment equals to 0.
     */
    public static final String NEUTER = "neuter";

    /**
     * Accepted values: ALL, NEW, NEUTER
     */
    private String calculate;

    @Override
    public MeaningCloudSentimentAnalyzerConfig buildFromJsonElement(JsonElement jsonElement) {
        return PluginConfigHelper.buildFromJson(jsonElement, MeaningCloudSentimentAnalyzerConfig.class);
    }

    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }
}
