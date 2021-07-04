package org.maritimemc.core.twofactor.util;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

/**
 * Utility class for QR code images.
 */
public class QRImageGenerator {

    /**
     * Reads an image from a URL.
     *
     * @param urlStr The URL string.
     * @return An image from the URL.
     */
    @SneakyThrows
    public static Image getImageFromUrl(String urlStr) {
        URL url = new URL(urlStr);

        return ImageIO.read(url);
    }

}
