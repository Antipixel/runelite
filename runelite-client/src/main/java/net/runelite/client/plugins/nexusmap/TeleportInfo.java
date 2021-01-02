package net.runelite.client.plugins.nexusmap;

import lombok.Getter;
import net.runelite.api.SpriteID;

public enum TeleportInfo
{
	/* Misthalin Teleports */
	LUMBRIDGE_GRAVEYARD("Lumbridge Grave'", SpriteID.SPELL_LUMBRIDGE_GRAVEYARD_TELEPORT, SpriteID.SPELL_LUMBRIDGE_GRAVEYARD_TELEPORT_DISABLED),
	DRAYNOR_MANOR("Draynor Manor", SpriteID.SPELL_DRAYNOR_MANOR_TELEPORT, SpriteID.SPELL_DRAYNOR_MANOR_TELEPORT_DISABLED),
	VARROCK("Varrock", SpriteID.SPELL_VARROCK_TELEPORT, SpriteID.SPELL_VARROCK_TELEPORT_DISABLED),
	GRAND_EXCHANGE("Grand Exchange", SpriteID.SPELL_VARROCK_TELEPORT, SpriteID.SPELL_VARROCK_TELEPORT_DISABLED),
	LUMBRIDGE("Lumbridge", SpriteID.SPELL_LUMBRIDGE_TELEPORT, SpriteID.SPELL_VARROCK_TELEPORT_DISABLED),
	SENNTISTEN("Senntisten", "Digsite", SpriteID.SPELL_SENNTISTEN_TELEPORT, SpriteID.SPELL_SENNTISTEN_TELEPORT_DISABLED),

	/* Asgarnia Teleports */
	MIND_ALTAR("Mind Altar", SpriteID.SPELL_MIND_ALTAR_TELEPORT, SpriteID.SPELL_MIND_ALTAR_TELEPORT_DISABLED),
	FALADOR("Falador", SpriteID.SPELL_FALADOR_TELEPORT, SpriteID.SPELL_FALADOR_TELEPORT_DISABLED),
	TROLL_STRONGHOLD("Troll Stronghold", 2139, 2139), // TODO: Sprite Const, custom sprite

	/* Morytania Teleports */
	SALVE_GRAVEYARD("Salve Graveyard", SpriteID.SPELL_SALVE_GRAVEYARD_TELEPORT, SpriteID.SPELL_SALVE_GRAVEYARD_TELEPORT_DISABLED),
	FENKEN_CASTLE("Fenken' Castle", SpriteID.SPELL_FENKENSTRAINS_CASTLE_TELEPORT, SpriteID.SPELL_FENKENSTRAINS_CASTLE_TELEPORT_DISABLED),
	HARMONY_ISLAND("Harmony Island", SpriteID.SPELL_HARMONY_ISLAND_TELEPORT, SpriteID.SPELL_HARMONY_ISLAND_TELEPORT_DISABLED),
	KHARYRLL("Kharyrll", "Canifis", SpriteID.SPELL_KHARYRLL_TELEPORT, SpriteID.SPELL_KHARYRLL_TELEPORT_DISABLED),
	BARROWS("Barrows", SpriteID.SPELL_BARROWS_TELEPORT, SpriteID.SPELL_BARROWS_TELEPORT_DISABLED),

	/* Wilderness Teleports */
	FORGOTTEN_CEMETERY("Cemetery", SpriteID.SPELL_CEMETARY_TELEPORT, SpriteID.SPELL_CEMETARY_TELEPORT_DISABLED),
	CARRALLANGAR("Carrallangar", "Graveyard of Shadows", SpriteID.SPELL_CARRALLANGAR_TELEPORT, SpriteID.SPELL_CARRALLANGAR_TELEPORT_DISABLED),
	ANNAKARL("Annakarl", "Demonic Ruins", SpriteID.SPELL_ANNAKARL_TELEPORT, SpriteID.SPELL_ANNAKARL_TELEPORT_DISABLED),
	GHORROCK("Ghorrock", "Frozen Waste Plateau", SpriteID.SPELL_GHORROCK_TELEPORT, SpriteID.SPELL_GHORROCK_TELEPORT_DISABLED),

	/* Kandarin Teleports */
	CAMELOT("Camelot", SpriteID.SPELL_CAMELOT_TELEPORT, SpriteID.SPELL_CAMELOT_TELEPORT_DISABLED),
	SEERS_VILLAGE("Seers' Village", SpriteID.SPELL_CAMELOT_TELEPORT, SpriteID.SPELL_CAMELOT_TELEPORT_DISABLED),
	EAST_ARDOUGNE("Ardougne", SpriteID.SPELL_ARDOUGNE_TELEPORT, SpriteID.SPELL_ARDOUGNE_TELEPORT_DISABLED),
	WATCHTOWER("Watchtower", SpriteID.SPELL_WATCHTOWER_TELEPORT, SpriteID.SPELL_WATCHTOWER_TELEPORT_DISABLED),
	YANILLE("Yanille", SpriteID.SPELL_WATCHTOWER_TELEPORT, SpriteID.SPELL_WATCHTOWER_TELEPORT_DISABLED),
	WEST_ARDOUGNE("West Ardougne", SpriteID.SPELL_WEST_ARDOUGNE_TELEPORT, SpriteID.SPELL_WEST_ARDOUGNE_TELEPORT_DISABLED),
	MARIM("Marim", "Ape Atoll", SpriteID.SPELL_TELEPORT_TO_APE_ATOLL, SpriteID.SPELL_TELEPORT_TO_APE_ATOLL_DISABLED),
	FISHING_GUILD("Fishing Guild", SpriteID.SPELL_FISHING_GUILD_TELEPORT, SpriteID.SPELL_FISHING_GUILD_TELEPORT_DISABLED),
	CATHERBY("Catherby", SpriteID.SPELL_CATHERBY_TELEPORT, SpriteID.SPELL_CATHERBY_TELEPORT_DISABLED),
	APE_ATOLL_DUNGEON("Ape Atoll Dungeon", SpriteID.SPELL_APE_ATOLL_TELEPORT, SpriteID.SPELL_APE_ATOLL_TELEPORT_DISABLED),

	/* Fremennik Teleports */
	LUNAR_ISLE("Lunar Isle", SpriteID.SPELL_MOONCLAN_TELEPORT, SpriteID.SPELL_MOONCLAN_TELEPORT_DISABLED),
	WATERBIRTH_ISLAND("Waterbirth Island", SpriteID.SPELL_WATERBIRTH_TELEPORT, SpriteID.SPELL_WATERBIRTH_TELEPORT_DISABLED),
	WEISS("Weiss", 2418, 2418), // TODO: Sprite Const, custom sprite

	/* Zeah Teleports */
	BATTLEFRONT("Battlefront", SpriteID.SPELL_BARROWS_TELEPORT, SpriteID.SPELL_BARROWS_TELEPORT_DISABLED),
	KOUREND_CASTLE("Kourend Castle", SpriteID.SPELL_TELEPORT_TO_KOUREND, SpriteID.SPELL_TELEPORT_TO_KOUREND_DISABLED);

	@Getter
	private String name;

	@Getter
	private String alias;

	@Getter
	private int spriteEnabled;

	@Getter
	private int spriteDisabled;

	TeleportInfo(String name, String alias, int spriteEnabled, int spriteDisabled)
	{
		this.name = name;
		this.alias = alias;
		this.spriteEnabled = spriteEnabled;
		this.spriteDisabled = spriteDisabled;
	}

	TeleportInfo(String name, int spriteEnabled, int spriteDisabled)
	{
		this(name, "", spriteEnabled, spriteDisabled);
	}

	public String getName()
	{
		return this.name;
	}

	public boolean hasAlias()
	{
		return (alias != null && !alias.isEmpty());
	}

	public String getAlias()
	{
		return this.alias;
	}
}
