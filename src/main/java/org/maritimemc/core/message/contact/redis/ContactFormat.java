package org.maritimemc.core.message.contact.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.maritimemc.core.db.messaging.format.MessageFormat;

@RequiredArgsConstructor
@Getter
public class ContactFormat implements MessageFormat {

    private final String senderName;
    private final String content;

}
