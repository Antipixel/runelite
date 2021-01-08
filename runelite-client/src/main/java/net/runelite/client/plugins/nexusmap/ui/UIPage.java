package net.runelite.client.plugins.nexusmap.ui;

import java.util.ArrayList;
import java.util.List;

public class UIPage
{
	private List<UIComponent> components;

	public UIPage()
	{
		this.components = new ArrayList<>();
	}

	public void setVisibility(boolean visibility)
	{
		// Update the visibility for each of the components
		this.components.forEach(c -> c.setVisibility(visibility));
	}

	public void add(UIComponent component)
	{
		this.components.add(component);
	}

	public void remove(UIComponent component)
	{
		this.components.remove(component);
	}
}
