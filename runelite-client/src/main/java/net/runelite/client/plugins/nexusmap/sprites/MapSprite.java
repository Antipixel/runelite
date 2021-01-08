package net.runelite.client.plugins.nexusmap.sprites;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.SpriteOverride;

@RequiredArgsConstructor
public enum MapSprite implements SpriteOverride
{
	MISTHALIN(-18000, "misthalin.png"),
	KARAMJA(-18001, "karamja.png"),
	ASGARNIA(-18002, "asgarnia.png"),
	DESERT(-18003, "desert.png"),
	MORYTANIA(-18004, "morytania.png"),
	WILDERNESS(-18005, "wilderness.png"),
	KANDARIN(-18006, "kandarin.png"),
	FREMENNIK(-18007, "fremennik.png"),
	TIRANNWN(-18008, "tirannwn.png");

	@Getter
	private final int spriteId;

	@Getter
	private final String fileName;
}
