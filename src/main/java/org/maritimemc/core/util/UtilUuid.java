package org.maritimemc.core.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UtilUuid {

    public static byte[] toBytes(final UUID uuid) {
        return ByteBuffer.allocate(16).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).array();
    }

    public static UUID fromBytes(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        if (bytes.length < 2) {
            throw new IllegalArgumentException("Byte array too small.");
        }

        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
