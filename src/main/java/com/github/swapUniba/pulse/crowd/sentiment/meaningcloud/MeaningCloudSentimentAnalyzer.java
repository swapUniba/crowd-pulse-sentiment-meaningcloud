package com.github.swapUniba.pulse.crowd.sentiment.meaningcloud;

import java.util.List;

import com.github.swapUniba.pulse.crowd.sentiment.meaningcloud.rest.MeaningCloudResponse;
import org.apache.logging.log4j.Logger;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.rx.RxUtil;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.spi.VoidConfig;
import com.github.frapontillo.pulse.util.PulseLogger;

import com.github.swapUniba.pulse.crowd.sentiment.meaningcloud.rest.MeaningCloudService;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * CrowdPulse's plugin that uses MeaningCloud Web Service to perform a sentiment
 * analysis.
 *
 * @author Cosimo Lovascio
 *
 */
public class MeaningCloudSentimentAnalyzer extends IPlugin<Message, Message, VoidConfig> {
    private static final String PLUGIN_NAME = "sentiment-meaningcloud";
    private static final int MAX_MESSAGES_PER_SECOND = 2;
    private static final long DELAY_MEANINGCLOUD_SERVICE = 1500;
    private final static Logger logger = PulseLogger.getLogger(MeaningCloudSentimentAnalyzer.class);

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    /**
     * This plugin doesn't give any {@link rx.Observable.Operator} as output, as it
     * will only expose a custom {@link rx.Observable.Transformer} that has to be
     * applied to a stream of {@link Message}s.
     *
     * @return Always {@code null}.
     */
    @Override
    protected Operator<Message, Message> getOperator(VoidConfig params) {
        return null;
    }


    @Override
    public Observable.Transformer<Message, Message> transform(VoidConfig params) {
        return messages -> messages
                .buffer(MAX_MESSAGES_PER_SECOND)
                .lift(new MeaningCloudOperator())
                // flatten the sequence of Observables back into one single Observable
                .compose(RxUtil.flatten());
    }

    /**
     * Custom operator.
     * @author Cosimo Lovascio
     *
     */
    private class MeaningCloudOperator implements Observable.Operator<List<Message>, List<Message>> {

        @Override
        public Subscriber<? super List<Message>> call(Subscriber<? super List<Message>> subscriber) {
            return new SafeSubscriber<>(new Subscriber<List<Message>>() {

                @Override
                public void onCompleted() {
                    reportPluginAsCompleted();
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    reportPluginAsErrored();
                    subscriber.onError(e);
                }

                @Override
                public void onNext(List<Message> messages) {
                    messages.forEach(m -> reportElementAsStarted(m.getId()));
                    MeaningCloudService service = new MeaningCloudService();
                    int remainingAttempts = 3;
                    do {
                        try {

                            // for each message, set the result
                            for (Message message : messages) {
                                MeaningCloudResponse response = service.makeRequest("auto", message.getText());
                                message.setSentiment(response.getSentimentScore());
                            }
                            remainingAttempts = 0;
                        } catch (Exception e) {
                            remainingAttempts -= 1;

                            logger.error(e.getMessage());
                            logger.error("Remaining attempts: " + remainingAttempts);

                            //set default sentiment
                            if (remainingAttempts == 0) {
                                messages.forEach(m -> m.setSentiment(0.0));
                            }
                        }
                    } while (remainingAttempts > 0);
                    messages.forEach(m -> reportElementAsEnded(m.getId()));


                    try {
                        Thread.sleep(DELAY_MEANINGCLOUD_SERVICE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(messages);
                }
            });
        }
    }

}