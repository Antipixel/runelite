package net.runelite.client.plugins.nexusmap.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;

public class UIButton extends UIComponent
{
	private int spriteStandard;
	private int spriteHover;

	/**
	 * Constructs a new button widget
	 * @param widget the underlying widget
	 */
	public UIButton(Widget widget)
	{
		super(widget);

		this.spriteStandard = -1;
		this.spriteHover = -1;
	}

	@Override
	protected void onMouseHover(ScriptEvent e)
	{
		// Call the parent method
		super.onMouseHover(e);

		// Update the sprite
		this.getWidget().setSpriteId(this.spriteHover);
	}

	@Override
	protected void onMouseLeave(ScriptEvent e)
	{
		// Call the parent method
		super.onMouseLeave(e);

		// Update the sprite
		this.getWidget().setSpriteId(this.spriteStandard);
	}

	/**
	 * Sets the button sprite for both standard and hover
	 * @param standard the standard sprite id
	 * @param hover the sprite to display on hover
	 */
	public void setSprites(int standard, int hover)
	{
		this.spriteStandard = standard;
		this.spriteHover = hover;

		// Update the widgets sprite
		this.getWidget().setSpriteId(this.spriteStandard);
	}

	/**
	 * Sets the sprite for the button, for buttons
	 * without a sprite for hovering state
	 * @param standard the button sprite
	 */
	public void setSprites(int standard)
	{
		this.setSprites(standard, standard);
	}
}
