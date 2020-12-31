package net.runelite.client.plugins.nexusmap;

public class Teleport
{
	private boolean alternate;
	private int childIndex;

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
	public boolean isAlternate()
	{
		return this.alternate;
	}
}
