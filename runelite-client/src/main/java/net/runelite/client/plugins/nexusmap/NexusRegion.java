package net.runelite.client.plugins.nexusmap;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Point;

public class NexusRegion
{
	private int id;
	private String name;
	private int regionMapSprite;
	private int fullMapSprite;

	private int iconX;
	private int iconY;

	private Map<Point, TeleportInfo> teleports;

	public NexusRegion(int id, String name)
	{
		this.id = id;
		this.name = name;

		this.regionMapSprite = -1;
		this.fullMapSprite = -1;

		this.iconX = 0;
		this.iconY = 0;

		this.teleports = new HashMap<>();
	}

	public int getRegionID()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public void setMapSprites(int regionSpriteID, int fullSpriteID)
	{
		this.regionMapSprite = regionSpriteID;
		this.fullMapSprite = fullSpriteID;
	}

	public int getRegionMapSprite()
	{
		return this.regionMapSprite;
	}

	public int getFullMapSprite()
	{
		return this.fullMapSprite;
	}

	public void setIconX(int iconX)
	{
		this.iconX = iconX;
	}

	public int getIconX()
	{
		return this.iconX;
	}

	public void setIconY(int iconY)
	{
		this.iconY = iconY;
	}

	public int getIconY()
	{
		return this.iconY;
	}

	public void setIconPosition(int x, int y) {
		this.iconX = x;
		this.iconY = y;
	}

	public void addTeleport(int x, int y, TeleportInfo location)
	{
		this.teleports.put(new Point(x, y), location);
	}

	public boolean hasTeleports()
	{
		return !this.teleports.isEmpty();
	}

	public Map<Point, TeleportInfo> getTeleports()
	{
		return this.teleports;
	}
}
