package net.runelite.client.plugins.nexusmap.ui;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class ButtonWidget
{
	private static final String BTN_NAME_FORMAT = "<col=ff9040>%s</col>";

	protected Widget widget;

	private int spriteStandard;
	private int spriteHover;

	private ButtonEventListener buttonHover;
	private ButtonEventListener buttonLeave;

	private List<ButtonMenuAction> actions;

	/**
	 * Constructs a new button widget
	 * @param widget the underlying widget
	 */
	public ButtonWidget(Widget widget)
	{
		this.widget = widget;

		// Assign the event listeners to the widget
		this.widget.setOnOpListener((JavaScriptCallback) this::onActionSelected);
		this.widget.setOnMouseOverListener((JavaScriptCallback) this::onMouseHover);
		this.widget.setOnMouseLeaveListener((JavaScriptCallback) this::onMouseLeave);
		this.widget.setHasListener(true);

		this.spriteStandard = -1;
		this.spriteHover = -1;

		this.actions = new ArrayList<>();
	}

	public void addAction(String action, ButtonMenuAction callback)
	{
		this.widget.setAction(actions.size(), action);
		this.actions.add(callback);
	}

	private void onActionSelected(ScriptEvent e)
	{
		// If there's no actions specified, ignore
		if (this.actions.isEmpty())
			return;

		// Get the action action event object for this menu option
		ButtonMenuAction actionEvent = this.actions.get(e.getOp() - 1);

		actionEvent.onMenuAction();
	}

	private void onMouseHover(ScriptEvent e)
	{
		// If a hover event is specified, trigger it
		if (this.buttonHover != null)
			this.buttonHover.onButtonEvent(this);

		// Update the sprite
		this.widget.setSpriteId(this.spriteHover);
	}

	private void onMouseLeave(ScriptEvent e)
	{
		// If a leave event is specified, trigger it
		if (this.buttonLeave != null)
			this.buttonLeave.onButtonEvent(this);

		// Update the sprite
		this.widget.setSpriteId(this.spriteStandard);
	}

	/**
	 * Sets a listener which will be called upon the mouse
	 * hovering over the widget
	 * @param listener the listener
	 */
	public void setButtonHoverListener(ButtonEventListener listener)
	{
		this.buttonHover = listener;
	}

	/**
	 * Sets a listener which will be called upon the mouse
	 * exiting from over the widget
	 * @param listener the listener
	 */
	public void setButtonLeaveListener(ButtonEventListener listener)
	{
		this.buttonLeave = listener;
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
		this.widget.setSpriteId(this.spriteStandard);
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

	/**
	 * Sets the name of the button widget
	 * @param name the button name
	 */
	public void setName(String name)
	{
		this.widget.setName(String.format(BTN_NAME_FORMAT, name));
	}

	/**
	 * Sets the button size
	 * @param width the button width
	 * @param height the button height
	 */
	public void setSize(int width, int height)
	{
		this.widget.setOriginalWidth(width);
		this.widget.setOriginalHeight(height);
	}

	/**
	 * Sets the position of the button, relative
	 * to the parent layer widget
	 * @param x the x position
	 * @param y the y position
	 */
	public void setPosition(int x, int y)
	{
		this.widget.setOriginalX(x);
		this.widget.setOriginalY(y);
	}

	/**
	 * Sets the visibility of the button
	 * @param visible true for visible, false for hidden
	 */
	public void setVisibility(boolean visible)
	{
		this.widget.setHidden(!visible);
	}
}
