package net.runelite.client.plugins.nexusmap.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;

public class UIFadeButton extends UIButton
{
	private static final int FADE_OPACITY = 100;
	private static final int DEFAULT_OPACITY = 0;

	public UIFadeButton(Widget widget)
	{
		super(widget);
	}

	@Override
	protected void onMouseHover(ScriptEvent e)
	{
		super.onMouseHover(e);

		// Fade the widget
		this.getWidget().setOpacity(FADE_OPACITY);
	}

	@Override
	protected void onMouseLeave(ScriptEvent e)
	{
		super.onMouseLeave(e);

		// Set the widget back to full opacity
		this.getWidget().setOpacity(DEFAULT_OPACITY);
	}
}
