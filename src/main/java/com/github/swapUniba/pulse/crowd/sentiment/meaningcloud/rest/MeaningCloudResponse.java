package com.github.swapUniba.pulse.crowd.sentiment.meaningcloud.rest;

/**
 * Models a MeaningCloud Web Service response.
 * @author Cosimo Lovascio
 *
 */
public class MeaningCloudResponse {
    public Status status;
    public String model;
    public String score_tag;
    public String agreement;
    public String subjectivity;
    public int confidence;
    public String irony;

    //not used
    public Object sentence_list;
    public Object sentimented_entity_list;
    public Object sentimented_concept_list;

    /**
     * Converts the sentiment categorical tag into semantic number.
     * @return the sentiment score number (0 for NONE or NEUTRAL, 1 for P+, 0.5 for P, -1 for N+, -0.5 for N)
     */
    public double getSentimentScore() {
        double result = 0;
        if (score_tag != null && score_tag != "") {
            switch (score_tag) {
                case "P+":
                    result = 1;
                    break;
                case "P":
                    result = 0.5;
                    break;
                case "NEU":
                    result = 0;
                    break;
                case "N":
                    result = -0.5;
                    break;
                case "N+":
                    result = -1;
                    break;
                case "NONE":
                    result = 0;
                    break;
            }
        } else {
            throw new RuntimeException("score_tag is null or empty. " +
                    "MeaningCloud status message: " + status.msg);
        }
        return result;
    }

    public class Status {
        public int code;
        public String msg;
        public int credits;
        public int remaining_credits;
    }

}