package com.orehighlighter;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Ore Highlighter",
        description = "Highlights paydirt and amethyst with a sleeker icon"
)
public class OreHighlighterPlugin extends Plugin
{
    static final int[] AMETHYST_IDS = {11388, 11389};
    static final int[] PAYDIRT_IDS = {26661, 26662, 26663, 26664};

    @Inject private Client client;
    @Inject private OreHighlighterOverlay overlay;
    @Inject private OverlayManager overlayManager;

    @Getter private final List<GameObject> trackedGameObjects = new ArrayList<>();
    @Getter private final List<WallObject> trackedWallObjects = new ArrayList<>();
    @Getter private final List<GroundObject> trackedGroundObjects = new ArrayList<>();

    private int currentRegionId = -1;

    @Override
    protected void startUp()
    {
        trackedGameObjects.clear();
        trackedWallObjects.clear();
        trackedGroundObjects.clear();
        currentRegionId = -1;
        overlayManager.add(overlay);

        refreshTrackedOres();
    }

    @Override
    protected void shutDown()
    {
        trackedGameObjects.clear();
        trackedWallObjects.clear();
        trackedGroundObjects.clear();
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (client.getLocalPlayer() == null)
        {
            return;
        }

        WorldPoint wp = client.getLocalPlayer().getWorldLocation();
        if (wp == null)
        {
            return;
        }

        int regionId = wp.getRegionID();

        if (regionId != currentRegionId)
        {
            currentRegionId = regionId;
            refreshTrackedOres();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject obj = event.getGameObject();
        if (isOreId(obj.getId()))
        {
            trackedGameObjects.add(obj);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        trackedGameObjects.remove(event.getGameObject());
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        WallObject wall = event.getWallObject();
        if (isOreId(wall.getId()))
        {
            trackedWallObjects.add(wall);
        }
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event)
    {
        trackedWallObjects.remove(event.getWallObject());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject ground = event.getGroundObject();
        if (isOreId(ground.getId()))
        {
            trackedGroundObjects.add(ground);
        }
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        trackedGroundObjects.remove(event.getGroundObject());
    }

    @SuppressWarnings("deprecation")
    private void refreshTrackedOres()
    {
        trackedGameObjects.clear();
        trackedWallObjects.clear();
        trackedGroundObjects.clear();

        Scene scene = client.getScene();
        if (scene == null)
        {
            return;
        }

        int plane = client.getLocalPlayer().getWorldLocation().getPlane();

        for (int x = 0; x < 104; x++)
        {
            for (int y = 0; y < 104; y++)
            {
                Tile tile = scene.getTiles()[plane][x][y];
                if (tile == null)
                {
                    continue;
                }

                GameObject[] gameObjects = tile.getGameObjects();
                if (gameObjects != null)
                {
                    for (GameObject obj : gameObjects)
                    {
                        if (obj != null && isOreId(obj.getId()))
                        {
                            trackedGameObjects.add(obj);
                        }
                    }
                }

                WallObject wall = tile.getWallObject();
                if (wall != null && isOreId(wall.getId()))
                {
                    trackedWallObjects.add(wall);
                }

                GroundObject ground = tile.getGroundObject();
                if (ground != null && isOreId(ground.getId()))
                {
                    trackedGroundObjects.add(ground);
                }
            }
        }
    }

    private boolean isOreId(int id)
    {
        for (int i : AMETHYST_IDS)
        {
            if (i == id)
                return true;
        }
        for (int i : PAYDIRT_IDS)
        {
            if (i == id)
                return true;
        }
        return false;
    }
}
