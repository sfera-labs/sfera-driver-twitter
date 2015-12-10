/**
 * 
 */
package cc.sferalabs.sfera.drivers.twitter.events;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.sferalabs.sfera.events.BaseEvent;
import cc.sferalabs.sfera.events.Node;

/**
 *
 * @author Giampiero Baggiani
 *
 * @version 1.0.0
 *
 */
public class UserStreamMessageEvent extends BaseEvent implements TwitterEvent {

	private static final JSONParser parser = new JSONParser();
	private final JSONObject value;

	/**
	 * 
	 * @param source
	 * @param data
	 * @throws ParseException
	 */
	public UserStreamMessageEvent(Node source, String data) throws ParseException {
		super(source, "stream.user");
		value = (JSONObject) parser.parse(data);
	}

	@Override
	public JSONObject getValue() {
		return value;
	}

}
