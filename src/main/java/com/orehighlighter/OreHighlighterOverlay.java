package com.orehighlighter;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OreHighlighterOverlay extends Overlay
{
    private static final int AMETHYST_Z_OFFSET = 80;
    private static final int PAYDIRT_Z_OFFSET = 160;

    private final Client client;
    private final OreHighlighterPlugin plugin;
    private final BufferedImage oreIcon;

    @Inject
    public OreHighlighterOverlay(Client client, OreHighlighterPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);

        this.oreIcon = ImageUtil.loadImageResource(getClass(), "ore_icon.png");
    }

    private boolean isAmethyst(int id)
    {
        for (int i : OreHighlighterPlugin.AMETHYST_IDS)
        {
            if (i == id)
                return true;
        }
        return false;
    }

    private boolean isPaydirt(int id)
    {
        for (int i : OreHighlighterPlugin.PAYDIRT_IDS)
        {
            if (i == id)
                return true;
        }
        return false;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderGameObjects(graphics);
        renderWallObjects(graphics);
        renderGroundObjects(graphics);

        return null;
    }

    private void renderGameObjects(Graphics2D graphics)
    {
        for (GameObject obj : plugin.getTrackedGameObjects())
        {
            if (obj == null)
                continue;

            int offset = getOffsetForOre(obj.getId());
            LocalPoint loc = obj.getLocalLocation();
            Point canvasPoint = Perspective.getCanvasImageLocation(client, loc, oreIcon, offset);

            if (canvasPoint != null)
            {
                graphics.drawImage(oreIcon, canvasPoint.getX(), canvasPoint.getY(), null);
            }
        }
    }

    private void renderWallObjects(Graphics2D graphics)
    {
        for (WallObject wall : plugin.getTrackedWallObjects())
        {
            if (wall == null)
                continue;

            int offset = getOffsetForOre(wall.getId());
            LocalPoint loc = wall.getLocalLocation();
            Point canvasPoint = Perspective.getCanvasImageLocation(client, loc, oreIcon, offset);

            if (canvasPoint != null)
            {
                graphics.drawImage(oreIcon, canvasPoint.getX(), canvasPoint.getY(), null);
            }
        }
    }

    private void renderGroundObjects(Graphics2D graphics)
    {
        for (GroundObject ground : plugin.getTrackedGroundObjects())
        {
            if (ground == null)
                continue;

            int offset = getOffsetForOre(ground.getId());
            LocalPoint loc = ground.getLocalLocation();
            Point canvasPoint = Perspective.getCanvasImageLocation(client, loc, oreIcon, offset);

            if (canvasPoint != null)
            {
                graphics.drawImage(oreIcon, canvasPoint.getX(), canvasPoint.getY(), null);
            }
        }
    }

    private int getOffsetForOre(int id)
    {
        if (isAmethyst(id))
        {
            return AMETHYST_Z_OFFSET;
        }
        else if (isPaydirt(id))
        {
            return PAYDIRT_Z_OFFSET;
        }
        else
        {
            return PAYDIRT_Z_OFFSET; // default offset fallback
        }
    }
}
