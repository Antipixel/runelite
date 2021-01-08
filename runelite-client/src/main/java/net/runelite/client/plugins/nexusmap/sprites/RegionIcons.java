package net.runelite.client.plugins.nexusmap.sprites;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.SpriteOverride;

@RequiredArgsConstructor
public enum RegionIcons implements SpriteOverride
{
	MISTHALIN_STD(-18100, "Icon_Misthalin_Std.png"),
	MISTHALIN_HOV(-18101, "Icon_Misthalin_Hover.png"),
	KARAMJA_STD(-18102, "Icon_Karamja_Std.png"),
	KARAMJA_HOV(-18103, "Icon_Karamja_Hover.png"),
	ASGARNIA_STD(-18104, "Icon_Asgarnia_Std.png"),
	ASGARNIA_HOV(-18105, "Icon_Asgarnia_Hover.png"),
	DESERT_STD(-18106, "Icon_Desert_Std.png"),
	DESERT_HOV(-18107, "Icon_Desert_Hover.png"),
	MORYTANIA_STD(-18108, "Icon_Morytania_Std.png"),
	MORYTANIA_HOV(-18109, "Icon_Morytania_Hover.png"),
	WILDERNESS_STD(-18110, "Icon_Wilderness_Std.png"),
	WILDERNESS_HOV(-18111, "Icon_Wilderness_Hover.png"),
	KANDARIN_STD(-18112, "Icon_Kandarin_Std.png"),
	KANDARIN_HOV(-18113, "Icon_Kandarin_Hover.png"),
	FREMENNIK_STD(-18114, "Icon_Fremennik_Std.png"),
	FREMENNIK_HOV(-18115, "Icon_Fremennik_Hover.png"),
	TIRANNWN_STD(-18116, "Icon_Tirannwn_Std.png"),
	TIRANNWN_HOV(-18117, "Icon_Tirannwn_Hover.png"),
	KOUREND_STD(-18118, "Icon_Kourend_Std.png"),
	KOUREND_HOV(-18119, "Icon_Kourend_Hover.png");

	@Getter
	private final int spriteId;

	@Getter
	private final String fileName;
}
