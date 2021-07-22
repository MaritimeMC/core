package org.maritimemc.core.chatlog;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatLog {

    private int id;
    private String token;

    private final UUID creator;

}
