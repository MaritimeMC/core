package org.maritimemc.core.db.messaging.format;

import org.maritimemc.core.db.messaging.listen.Callback;

/**
 * A class to be serialised into JSON for the sending/receiving of database messages
 * across Redis channels. All messages to be sent must implement this interface and to be
 * handled by their {@link Callback}.
 *
 * @see com.google.gson.Gson#toJson(Object)
 * @see com.google.gson.Gson#fromJson(String, Class)
 */
public interface MessageFormat {
}
