package net.runelite.client.plugins.nexusmap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.SpriteOverride;

@RequiredArgsConstructor
public enum NexusSprites implements SpriteOverride
{
	MISTHALIN(-18000, "misthalin.png", 478, 272),
	KARAMJA(-18001, "karamja.png", 478, 272),
	ASGARNIA(-18002, "asgarnia.png", 478, 272),
	DESERT(-18003, "desert.png", 478, 272),
	MORYTANIA(-18004, "morytania.png", 478, 272),
	WILDERNESS(-18005, "wilderness.png", 478, 272),
	KANDARIN(-18006, "kandarin.png", 478, 272),
	FREMENNIK(-18007, "fremennik.png", 478, 272),
	TIRANNWN(-18008, "tirannwn.png", 478, 272);

	@Getter
	private final int spriteId;

	@Getter
	private final String fileName;

	@Getter
	private final int width;

	@Getter
	private final int height;
}
