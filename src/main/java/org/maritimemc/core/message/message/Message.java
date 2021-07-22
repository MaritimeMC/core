package org.maritimemc.core.message.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.maritimemc.core.db.messaging.format.MessageFormat;

@RequiredArgsConstructor
@Getter
public class Message implements MessageFormat {

    private final MessagePlayer sender;
    private final MessagePlayer recipient;

    private final String content;
    private final String senderServerName;

    private final long time;

}
