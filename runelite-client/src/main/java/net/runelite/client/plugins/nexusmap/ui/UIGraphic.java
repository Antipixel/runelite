package net.runelite.client.plugins.nexusmap.ui;

import net.runelite.api.widgets.Widget;

public class UIGraphic extends UIComponent
{
	public UIGraphic(Widget widget)
	{
		super(widget);
	}

	public void setSprite(int spriteID)
	{
		this.getWidget().setSpriteId(spriteID);
	}
}
