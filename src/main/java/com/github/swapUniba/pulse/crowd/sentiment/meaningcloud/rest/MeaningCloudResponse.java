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
     * @return the sentiment score number (0 for NONE or NEUTRAL, 1 for P+ and P, -1 for N+ and N)
     */
    public Double getSentimentScore() {
        Double result = null;
        if (score_tag != null && !score_tag.equals("")) {
            switch (score_tag) {
                case "P+":
                    result = (double) 1;
                    break;
                case "P":
                    result = (double) 1;
                    break;
                case "NEU":
                    result = (double) 0;
                    break;
                case "N":
                    result = (double) -1;
                    break;
                case "N+":
                    result = (double) -1;
                    break;
                case "NONE":
                    result = null;
                    break;
            }
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