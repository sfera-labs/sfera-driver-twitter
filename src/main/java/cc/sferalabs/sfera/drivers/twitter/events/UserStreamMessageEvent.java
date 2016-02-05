/**
 * 
 */
package cc.sferalabs.sfera.drivers.twitter.events;

import org.json.JSONObject;

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

	private final JSONObject value;

	/**
	 * 
	 * @param source
	 * @param data
	 */
	public UserStreamMessageEvent(Node source, String data) {
		super(source, "stream.user");
		value = new JSONObject(data);
	}

	@Override
	public JSONObject getValue() {
		return value;
	}

}
