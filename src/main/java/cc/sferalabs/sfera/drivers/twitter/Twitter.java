package cc.sferalabs.sfera.drivers.twitter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.parser.ParseException;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import cc.sferalabs.sfera.core.Configuration;
import cc.sferalabs.sfera.drivers.Driver;
import cc.sferalabs.sfera.drivers.twitter.events.UserStreamMessageEvent;
import cc.sferalabs.sfera.events.Bus;

public class Twitter extends Driver {

	private Client hosebirdClient;
	private BlockingQueue<String> msgQueue;

	public Twitter(String id) {
		super(id);
	}

	@Override
	protected boolean onInit(Configuration config) throws InterruptedException {
		String consumerKey = config.get("consumer_key", null);
		String consumerSecret = config.get("consumer_secret", null);
		String token = config.get("token", null);
		String tokenSecret = config.get("token_secret", null);

		if (consumerKey == null) {
			log.error("Must specify consumer_key parameter");
		}
		if (consumerSecret == null) {
			log.error("Must specify consumer_secret parameter");
		}
		if (token == null) {
			log.error("Must specify token parameter");
		}
		if (tokenSecret == null) {
			log.error("Must specify token_secret parameter");
		}

		int msgQueueCapacity = config.get("message_queue_capacity", 1000);
		msgQueue = new LinkedBlockingQueue<String>(msgQueueCapacity);

		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

		Hosts userstreamHost = new HttpHosts(Constants.USERSTREAM_HOST);
		UserstreamEndpoint userstreamEndpoint = new UserstreamEndpoint();
		ClientBuilder builder = new ClientBuilder().name(getId()).hosts(userstreamHost)
				.authentication(hosebirdAuth).endpoint(userstreamEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		hosebirdClient = builder.build();
		hosebirdClient.connect();

		return true;
	}

	@Override
	protected boolean loop() throws InterruptedException {
		if (hosebirdClient.isDone()) {
			return false;
		}

		String msg = msgQueue.take();
		try {
			log.debug("Message: {}", msg);
			Bus.post(new UserStreamMessageEvent(this, msg));
		} catch (ParseException e) {
			log.error("Message parsing error", e);
		}

		return true;
	}

	@Override
	protected void onQuit() {
		hosebirdClient.stop();
	}

}
