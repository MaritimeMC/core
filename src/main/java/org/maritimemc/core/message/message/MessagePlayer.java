package org.maritimemc.core.message.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class MessagePlayer {

    private final String name;
    private final UUID uuid;

}
