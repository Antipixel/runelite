package net.runelite.client.plugins.nexusmap;

public class Teleport
{
	private String name;
	private int childIndex;
	private String keyShortcut;
	private boolean alternate;

	public Teleport(String name, int childIndex, String key, boolean alt)
	{
		this.name = name;
		this.childIndex = childIndex;
		this.keyShortcut = key;
		this.alternate = alt;
	}

	/**
	 *
	 * @return
	 */
	public int getChildIndex()
	{
		return this.childIndex;
	}

	/**
	 * Checks whether or not the teleport is an
	 * alternative teleport location, such as the
	 * Grand Exchange on the Varrock teleport spell
	 * @return true if is alternate location, otherwise false
	 */
	public boolean isAlt()
	{
		return this.alternate;
	}


	/**
	 * The keyboard shortcut key used to activate this teleport
	 * @return the shortcut key
	 */
	public String getKeyShortcut()
	{
		return this.keyShortcut;
	}
}
