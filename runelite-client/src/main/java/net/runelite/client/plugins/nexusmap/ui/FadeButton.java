package net.runelite.client.plugins.nexusmap.ui;

import net.runelite.api.widgets.Widget;

public class FadeButton extends ButtonWidget
{
	private static final int FADE_OPACITY = 100;
	private static final int DEFAULT_OPACITY = 0;

	public FadeButton(Widget widget)
	{
		super(widget);
		this.setButtonHoverListener(this::onMouseHover);
		this.setButtonLeaveListener(this::onMouseLeave);
	}

	public void onMouseHover(ButtonWidget e)
	{
		this.widget.setOpacity(FADE_OPACITY);
	}

	public void onMouseLeave(ButtonWidget e)
	{
		this.widget.setOpacity(DEFAULT_OPACITY);
	}
}
