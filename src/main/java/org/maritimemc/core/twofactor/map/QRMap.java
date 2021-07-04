package org.maritimemc.core.twofactor.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

/**
 * QR Code Map Renderer
 */
public class QRMap extends MapRenderer {

    private final Image qrCode;

    /**
     * Class constructor
     *
     * @param qrCode The QR image to render onto the map.
     */
    public QRMap(Image qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0, 0, qrCode);
    }
}
