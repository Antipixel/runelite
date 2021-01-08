package net.runelite.client.plugins.nexusmap;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nexusmap.sprites.MapSprite;
import net.runelite.client.plugins.nexusmap.sprites.RegionIcons;
import net.runelite.client.plugins.nexusmap.ui.UIButton;
import net.runelite.client.plugins.nexusmap.ui.UIFadeButton;
import net.runelite.client.plugins.nexusmap.ui.UIGraphic;
import net.runelite.client.plugins.nexusmap.ui.UIPage;

@PluginDescriptor(
        name = "Nexus Menu Map",
        description = "Replaces the player owned house teleport Nexus menu",
        tags = {"poh", "portal", "teleport", "nexus"}
)
public class NexusMapPlugin extends Plugin
{
	private static final int REGION_COUNT = 10;
	private static final int REGION_MAP_MAIN = 2721;
	private static final int REGION_MAP_MISTHALIN = 2722;
	private static final int REGION_MAP_KARAMJA = 2723;
	private static final int REGION_MAP_WILDERNESS = 2724;
	private static final int REGION_MAP_ASGARNIA = 2725;
	private static final int REGION_MAP_KANDARIN = 2726;
	private static final int REGION_MAP_DESERT = 2727;
	private static final int REGION_MAP_FREMENNIK = 2728;
	private static final int REGION_MAP_TIRANNWN = 2729;
	private static final int REGION_MAP_MORYTANIA = 2730;
	private static final int REGION_MAP_ZEAH = -1;

	private static final int REGION_ID_MISTHALIN = 0;
	private static final int REGION_ID_KARAMJA = 1;
	private static final int REGION_ID_ASGARNIA = 2;
	private static final int REGION_ID_DESERT = 3;
	private static final int REGION_ID_MORYTANIA = 4;
	private static final int REGION_ID_WILDERNESS = 5;
	private static final int REGION_ID_KANDARIN = 6;
	private static final int REGION_ID_FREMENNIK = 7;
	private static final int REGION_ID_TIRANNWN = 8;
	private static final int REGION_ID_KOUREND = 9;

	private static final int TELE_ICON_SIZE = 24;

	private static final int MAP_SPRITE_POS_X = 32;
	private static final int MAP_SPRITE_POS_Y = 18;
	private static final int INDEX_MAP_SPRITE_WIDTH = 400;
	private static final int INDEX_MAP_SPRITE_HEIGHT = 214;
	private static final int REGION_MAP_SPRITE_WIDTH = 478;
	private static final int REGION_MAP_SPRITE_HEIGHT = 272;
	private static final int MAP_ICON_WIDTH = 50;
	private static final int MAP_ICON_HEIGHT = 41;

	private static final String ACTION_TEXT_TELE = "Teleport";
	private static final String ACTION_TEXT_SELECT = "Select";
	private static final String ACTION_TEXT_BACK = "Back";

	private static final int SCRIPT_TRIGGER_KEY = 1437;

	private static final String TELE_NAME_PATTERN = "<col=ffffff>(\\S)<\\/col> :  (.+)";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private NexusConfig config;

	@Inject
	private SpriteManager spriteManager;

	private Map<String, Teleport> availableTeleports;
	private Map<String, TeleportInfo> teleportInfoMap;

	private NexusRegion[] regions;

	/* Widgets */
	private List<WidgetInfo> hiddenWidgets;

	private UIGraphic mapGraphic;
	private UIGraphic[] indexRegionGraphics;
	private UIButton[] indexRegionIcons;

	private UIPage indexPage;
	private List<UIPage> mapPages;

	@Override
	protected void startUp() throws Exception
	{
		this.registerCustomSprites();

		this.createHiddenWidgetList();
		this.createTeleportInfoMap();
		this.createNexusRegions();
	}

	@Provides
	NexusConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NexusConfig.class);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.hiddenWidgets.clear();
		this.teleportInfoMap.clear();
	}

	/**
	 * Registers the custom game sprites with the Sprite Manager
	 */
	private void registerCustomSprites()
	{
		this.spriteManager.addSpriteOverrides(MapSprite.values());
		this.spriteManager.addSpriteOverrides(RegionIcons.values());
	}

	/**
	 * Creates a list of widgets that the plugin does not require
	 * in order to function, for them to be hidden and shown as required
	 */
	private void createHiddenWidgetList()
	{
		this.hiddenWidgets = new ArrayList<>();

		this.hiddenWidgets.add(WidgetInfo.NEXUS_PORTAL_MODEL);
		this.hiddenWidgets.add(WidgetInfo.NEXUS_SCRY_PORTAL_TEXT);
		this.hiddenWidgets.add(WidgetInfo.NEXUS_SCRY_SELECT);
		this.hiddenWidgets.add(WidgetInfo.NEXUS_SCROLLBOX_BORDER);
		this.hiddenWidgets.add(WidgetInfo.NEXUS_TELEPORT_LIST);
		this.hiddenWidgets.add(WidgetInfo.NEXUS_SCROLLBAR);
	}

	private void createNexusRegions()
	{
		this.regions = new NexusRegion[REGION_COUNT];

		this.regions[REGION_ID_MISTHALIN] = new NexusRegion(REGION_ID_MISTHALIN, "Misthalin");
		this.regions[REGION_ID_MISTHALIN].setIconPosition(252, 108);
		this.regions[REGION_ID_MISTHALIN].setMapSprites(REGION_MAP_MISTHALIN, MapSprite.MISTHALIN.getSpriteId());
		this.regions[REGION_ID_MISTHALIN].setIconSprites(RegionIcons.MISTHALIN_STD.getSpriteId(), RegionIcons.MISTHALIN_HOV.getSpriteId());
		this.regions[REGION_ID_MISTHALIN].addTeleport(235, 211, TeleportInfo.LUMBRIDGE_GRAVEYARD);
		this.regions[REGION_ID_MISTHALIN].addTeleport(165, 138, TeleportInfo.DRAYNOR_MANOR);
		this.regions[REGION_ID_MISTHALIN].addTeleport(218, 93, TeleportInfo.VARROCK);
		this.regions[REGION_ID_MISTHALIN].addTeleport(193, 72, TeleportInfo.GRAND_EXCHANGE);
		this.regions[REGION_ID_MISTHALIN].addTeleport(214, 211, TeleportInfo.LUMBRIDGE);
		this.regions[REGION_ID_MISTHALIN].addTeleport(274, 130, TeleportInfo.SENNTISTEN);

		this.regions[REGION_ID_KARAMJA] = new NexusRegion(REGION_ID_KARAMJA, "Karamja");
		this.regions[REGION_ID_KARAMJA].setIconPosition(195, 155);
		this.regions[REGION_ID_KARAMJA].setMapSprites(REGION_MAP_KARAMJA, MapSprite.KARAMJA.getSpriteId());
		this.regions[REGION_ID_KARAMJA].setIconSprites(RegionIcons.KARAMJA_STD.getSpriteId(), RegionIcons.KARAMJA_HOV.getSpriteId());

		this.regions[REGION_ID_ASGARNIA] = new NexusRegion(REGION_ID_ASGARNIA, "Asgarnia");
		this.regions[REGION_ID_ASGARNIA].setIconPosition(205, 88);
		this.regions[REGION_ID_ASGARNIA].setMapSprites(REGION_MAP_ASGARNIA, MapSprite.ASGARNIA.getSpriteId());
		this.regions[REGION_ID_ASGARNIA].setIconSprites(RegionIcons.ASGARNIA_STD.getSpriteId(), RegionIcons.ASGARNIA_HOV.getSpriteId());
		this.regions[REGION_ID_ASGARNIA].addTeleport(176, 19, TeleportInfo.TROLL_STRONGHOLD);
		this.regions[REGION_ID_ASGARNIA].addTeleport(241, 120, TeleportInfo.MIND_ALTAR);
		this.regions[REGION_ID_ASGARNIA].addTeleport(240, 181, TeleportInfo.FALADOR);

		this.regions[REGION_ID_DESERT] = new NexusRegion(REGION_ID_DESERT, "Kharidian Desert");
		this.regions[REGION_ID_DESERT].setIconPosition(268, 167);
		this.regions[REGION_ID_DESERT].setMapSprites(REGION_MAP_DESERT, MapSprite.DESERT.getSpriteId());
		this.regions[REGION_ID_DESERT].setIconSprites(RegionIcons.DESERT_STD.getSpriteId(), RegionIcons.DESERT_HOV.getSpriteId());

		this.regions[REGION_ID_MORYTANIA] = new NexusRegion(REGION_ID_MORYTANIA, "Morytania");
		this.regions[REGION_ID_MORYTANIA].setIconPosition(311, 107);
		this.regions[REGION_ID_MORYTANIA].setMapSprites(REGION_MAP_MORYTANIA, MapSprite.MORYTANIA.getSpriteId());
		this.regions[REGION_ID_MORYTANIA].setIconSprites(RegionIcons.MORYTANIA_STD.getSpriteId(), RegionIcons.MORYTANIA_HOV.getSpriteId());
		this.regions[REGION_ID_MORYTANIA].addTeleport(72, 84, TeleportInfo.SALVE_GRAVEYARD);
		this.regions[REGION_ID_MORYTANIA].addTeleport(127, 50, TeleportInfo.FENKEN_CASTLE);
		this.regions[REGION_ID_MORYTANIA].addTeleport(412, 218, TeleportInfo.HARMONY_ISLAND);
		this.regions[REGION_ID_MORYTANIA].addTeleport(92, 75, TeleportInfo.KHARYRLL);
		this.regions[REGION_ID_MORYTANIA].addTeleport(133, 168, TeleportInfo.BARROWS);

		this.regions[REGION_ID_WILDERNESS] = new NexusRegion(REGION_ID_WILDERNESS, "Wilderness");
		this.regions[REGION_ID_WILDERNESS].setIconPosition(251, 44);
		this.regions[REGION_ID_WILDERNESS].setMapSprites(REGION_MAP_WILDERNESS, MapSprite.WILDERNESS.getSpriteId());
		this.regions[REGION_ID_WILDERNESS].setIconSprites(RegionIcons.WILDERNESS_STD.getSpriteId(), RegionIcons.WILDERNESS_HOV.getSpriteId());
		this.regions[REGION_ID_WILDERNESS].addTeleport(136, 125, TeleportInfo.FORGOTTEN_CEMETERY);
		this.regions[REGION_ID_WILDERNESS].addTeleport(234, 162, TeleportInfo.CARRALLANGAR);
		this.regions[REGION_ID_WILDERNESS].addTeleport(290, 50, TeleportInfo.ANNAKARL);
		this.regions[REGION_ID_WILDERNESS].addTeleport(136, 54, TeleportInfo.GHORROCK);

		this.regions[REGION_ID_KANDARIN] = new NexusRegion(REGION_ID_KANDARIN, "Kandarin");
		this.regions[REGION_ID_KANDARIN].setIconPosition(140, 118);
		this.regions[REGION_ID_KANDARIN].setMapSprites(REGION_MAP_KANDARIN, MapSprite.KANDARIN.getSpriteId());
		this.regions[REGION_ID_KANDARIN].setIconSprites(RegionIcons.KANDARIN_STD.getSpriteId(), RegionIcons.KANDARIN_HOV.getSpriteId());
		this.regions[REGION_ID_KANDARIN].addTeleport(271, 37, TeleportInfo.CAMELOT);
		this.regions[REGION_ID_KANDARIN].addTeleport(249, 32, TeleportInfo.SEERS_VILLAGE);
		this.regions[REGION_ID_KANDARIN].addTeleport(222, 118, TeleportInfo.EAST_ARDOUGNE);
		this.regions[REGION_ID_KANDARIN].addTeleport(166, 206, TeleportInfo.WATCHTOWER);
		this.regions[REGION_ID_KANDARIN].addTeleport(181, 232, TeleportInfo.YANILLE);
		this.regions[REGION_ID_KANDARIN].addTeleport(156, 123, TeleportInfo.WEST_ARDOUGNE);
		this.regions[REGION_ID_KANDARIN].addTeleport(424, 196, TeleportInfo.MARIM);
		this.regions[REGION_ID_KANDARIN].addTeleport(187, 78, TeleportInfo.FISHING_GUILD);
		this.regions[REGION_ID_KANDARIN].addTeleport(297, 52, TeleportInfo.CATHERBY);
		this.regions[REGION_ID_KANDARIN].addTeleport(414, 240, TeleportInfo.APE_ATOLL_DUNGEON);

		this.regions[REGION_ID_FREMENNIK] = new NexusRegion(REGION_ID_FREMENNIK, "Fremennik");
		this.regions[REGION_ID_FREMENNIK].setIconPosition(155, 48);
		this.regions[REGION_ID_FREMENNIK].setMapSprites(REGION_MAP_FREMENNIK, MapSprite.FREMENNIK.getSpriteId());
		this.regions[REGION_ID_FREMENNIK].setIconSprites(RegionIcons.FREMENNIK_STD.getSpriteId(), RegionIcons.FREMENNIK_HOV.getSpriteId());
		this.regions[REGION_ID_FREMENNIK].addTeleport(35, 75, TeleportInfo.LUNAR_ISLE);
		this.regions[REGION_ID_FREMENNIK].addTeleport(248, 135, TeleportInfo.WATERBIRTH_ISLAND);
		this.regions[REGION_ID_FREMENNIK].addTeleport(400, 32, TeleportInfo.WEISS);

		this.regions[REGION_ID_TIRANNWN] = new NexusRegion(REGION_ID_TIRANNWN, "Tirannwn");
		this.regions[REGION_ID_TIRANNWN].setIconPosition(84, 128);
		this.regions[REGION_ID_TIRANNWN].setMapSprites(REGION_MAP_TIRANNWN, MapSprite.TIRANNWN.getSpriteId());
		this.regions[REGION_ID_TIRANNWN].setIconSprites(RegionIcons.TIRANNWN_STD.getSpriteId(), RegionIcons.TIRANNWN_HOV.getSpriteId());

		this.regions[REGION_ID_KOUREND] = new NexusRegion(REGION_ID_KOUREND, "Kourend");
		this.regions[REGION_ID_KOUREND].setIconPosition(0, 0);
		this.regions[REGION_ID_KOUREND].setMapSprites(REGION_MAP_ZEAH, -1);
		this.regions[REGION_ID_KOUREND].setIconSprites(RegionIcons.KOUREND_STD.getSpriteId(), RegionIcons.KOUREND_HOV.getSpriteId());
		this.regions[REGION_ID_KOUREND].addTeleport(250, 100, TeleportInfo.BATTLEFRONT);
		this.regions[REGION_ID_KOUREND].addTeleport(220, 100, TeleportInfo.KOUREND_CASTLE);
	}

	private void createTeleportInfoMap()
	{
		this.teleportInfoMap = new TreeMap<>();

		for (TeleportInfo definition : TeleportInfo.values())
		{
			this.teleportInfoMap.put(definition.getName(), definition);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		if (e.getGroupId() == WidgetID.NEXUS_TELEPORT_ID)
		{
			Widget parent = this.client.getWidget(WidgetInfo.NEXUS_PORTAL_PANEL);


			// Hide the default widgets
			this.setDefaultWidgetVisibility(false);
			this.expandParentWidget(parent);
			this.buildAvailableTeleportList();

			this.createMenuPages();

			this.createMapGraphic(parent);
			this.createBackButton(parent);
			this.createTeleportWidgets(parent);
			this.createIndexPage(parent);

			this.displayIndexPage();
		}
	}

	private void createIndexPage(Widget root)
	{
		// Create a graphic widget for the background image of the index page
		Widget backingWidget = root.createChild(-1, WidgetType.GRAPHIC);

		// Wrap in a UIGraphic, set dimensions, position and sprite
		UIGraphic indexBackingGraphic = new UIGraphic(backingWidget);
		indexBackingGraphic.setPosition(MAP_SPRITE_POS_X, MAP_SPRITE_POS_Y);
		indexBackingGraphic.setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
		indexBackingGraphic.setSprite(REGION_MAP_MAIN);

		// Initialise the arrays for the map graphics and icons
		this.indexRegionGraphics = new UIGraphic[REGION_COUNT];
		this.indexRegionIcons = new UIButton[REGION_COUNT];

		for (int i = 0; i < REGION_COUNT; i++)
		{
			// Create a new graphic widget for the index map image
			Widget regionGraphic = root.createChild(-1, WidgetType.GRAPHIC);
			Widget regionIcon = root.createChild(-1, WidgetType.GRAPHIC);

			// Get information for the region
			NexusRegion region = this.regions[i];

			// Wrap in UIGraphic, update the size and position to match that of
			// the backing graphic. Set the sprite to that of the current region
			this.indexRegionGraphics[i] = new UIGraphic(regionGraphic);
			this.indexRegionGraphics[i].setPosition(MAP_SPRITE_POS_X, MAP_SPRITE_POS_Y);
			this.indexRegionGraphics[i].setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
			this.indexRegionGraphics[i].setSprite(region.getRegionMapSprite());

			this.indexRegionIcons[i] = new UIButton(regionIcon);
			this.indexRegionIcons[i].setName(region.getName());
			this.indexRegionIcons[i].setPosition(region.getIconX(), region.getIconY());
			this.indexRegionIcons[i].setSize(MAP_ICON_WIDTH, MAP_ICON_HEIGHT);
			this.indexRegionIcons[i].setSprites(region.getIconSpriteStd(), region.getIconSpriteHov());
			this.indexRegionIcons[i].setOnHoverListener((c) -> onIconHover(region.getRegionID()));
			this.indexRegionIcons[i].setOnLeaveListener((c) -> onIconLeave(region.getRegionID()));
			this.indexRegionIcons[i].addAction(ACTION_TEXT_SELECT, () -> onIconClicked(region.getRegionID()));

			// Add the index map graphic and icon to the index page
			this.indexPage.add(this.indexRegionIcons[i]);
			this.indexPage.add(this.indexRegionGraphics[i]);
		}
	}

	private void createBackButton(Widget root)
	{
		Widget backArrowWidget = root.createChild(-1, WidgetType.GRAPHIC);

		UIButton backArrowButton = new UIFadeButton(backArrowWidget);
		backArrowButton.setSprites(SpriteID.GE_BACK_ARROW_BUTTON);
		backArrowButton.setPosition(6, 6);
		backArrowButton.setSize(30, 23);
		backArrowButton.addAction(ACTION_TEXT_BACK, this::onBackButtonPressed);

		// Add the back arrow to each map page
		this.mapPages.forEach(page -> page.add(backArrowButton));
	}

	private void expandParentWidget(Widget parentWidget)
	{
		parentWidget.setOriginalX(0);
		parentWidget.setOriginalY(35);
		parentWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
		parentWidget.setHeightMode(WidgetSizeMode.ABSOLUTE);
		parentWidget.setOriginalWidth(REGION_MAP_SPRITE_WIDTH);
		parentWidget.setOriginalHeight(REGION_MAP_SPRITE_HEIGHT);
	}

	private void createMapGraphic(Widget root)
	{
		// Create the widget for the map graphic
		Widget mapWidget = root.createChild(-1, WidgetType.GRAPHIC);

		// Wrap the widget in a UIGraphic
		this.mapGraphic = new UIGraphic(mapWidget);
		this.mapGraphic.setPosition(0, 0);
		this.mapGraphic.setSize(REGION_MAP_SPRITE_WIDTH, REGION_MAP_SPRITE_HEIGHT);

		// Add the map graphic to each of the map pages
		this.mapPages.forEach(page -> page.add(this.mapGraphic));
	}

	/**
	 * Shows or hides the default menu widgets
	 *
	 * @param visible the desired visibility state of the widgets,
	 *                true to set them to visible, false for hidden
	 */
	private void setDefaultWidgetVisibility(boolean visible)
	{
		// Iterate though each of the non essential widgets
		for (WidgetInfo wInfo : this.hiddenWidgets)
		{
			// Update their visibility
			this.client.getWidget(wInfo).setHidden(!visible);
		}
	}

	private void createMenuPages()
	{
		this.indexPage = new UIPage();
		this.mapPages = new ArrayList<>(REGION_COUNT);

		for (int i = 0; i < REGION_COUNT; i++)
		{
			this.mapPages.add(new UIPage());
		}
	}

	private void displayIndexPage()
	{
		this.indexPage.setVisibility(true);
		this.mapPages.forEach(page -> page.setVisibility(false));
	}

	private void displayMapPage(int regionID)
	{
		this.indexPage.setVisibility(false);
		this.mapPages.forEach(page -> page.setVisibility(false));
		this.mapPages.get(regionID).setVisibility(true);

		this.mapGraphic.setSprite(regions[regionID].getFullMapSprite());
	}

	private void onIconHover(int regionID)
	{
		// Move the map sprite for this region up by 2 pixels, and
		// set the opacity to 75% opaque
		this.indexRegionGraphics[regionID].setY(MAP_SPRITE_POS_Y - 2);
		this.indexRegionGraphics[regionID].setOpacity(.75f);
		this.indexRegionGraphics[regionID].getWidget().revalidate();
	}

	private void onIconLeave(int regionID)
	{
		// Restore the original position and set back to fully opaque
		this.indexRegionGraphics[regionID].setY(MAP_SPRITE_POS_Y);
		this.indexRegionGraphics[regionID].setOpacity(1.0f);
		this.indexRegionGraphics[regionID].getWidget().revalidate();
	}

	private void onIconClicked(int regionID)
	{
		this.displayMapPage(regionID);
	}

	/**
	 * Called when the back arrow has been pressed
	 */
	private void onBackButtonPressed()
	{
		this.displayIndexPage();
	}

	/**
	 * Constructs the list of teleports available for the player to use
	 */
	private void buildAvailableTeleportList()
	{
		// Compile the pattern that will match the teleport label
		// and place the hotkey and teleport name into groups
		Pattern labelPattern = Pattern.compile(TELE_NAME_PATTERN);

		this.availableTeleports = new HashMap<>();

		// Get the parent widgets containing the label list, for both
		// the primary type teleports and altnerate type
		Widget primaryParent = this.client.getWidget(WidgetInfo.NEXUS_LOC_LABELS_PRIMARY);
		Widget alternateParent = this.client.getWidget(WidgetInfo.NEXUS_LOC_LABELS_ALTERNATE);

		// Fetch all teleports for both the primary and alternate teleport widgets,
		// appending the results of both to the available teleports maps
		this.availableTeleports.putAll(this.getTeleportsFromLabelWidget(primaryParent, false, labelPattern));
		this.availableTeleports.putAll(this.getTeleportsFromLabelWidget(alternateParent, true, labelPattern));
	}

	/**
	 * Extracts information from a nexus portals teleport list and returns the information as a Teleport list,
	 * containing the name, index, shortcut key and type of teleport (either primary or alternate)
	 * @param labelParent the widget containing a teleport list
	 * @param alt true if this widget contains alternate teleports, false if primary
	 * @param pattern a compiled pattern for matching the text contained in the list item widgets
	 * @return a list containing all available teleports for the provided widget
	 */
	private Map<String, Teleport> getTeleportsFromLabelWidget(Widget labelParent, boolean alt, Pattern pattern)
	{
		// Grab the children of the widget, each of which have a text
		// attribute containing the teleport location name and key shortcut
		Widget[] labelWidgets = labelParent.getDynamicChildren();

		// Create a map in which to place the available teleport options
 		Map<String, Teleport> teleports = new HashMap<>();

		for (Widget child : labelWidgets)
		{
			// Create a pattern matcher with the widgets text content
 			Matcher matcher = pattern.matcher(child.getText());

 			// If the text doesn't match the pattern, skip onto the next
			if (!matcher.matches())
				continue;

			// Extract the pertinent information
			String shortcutKey = matcher.group(1);
			String teleportName =  matcher.group(2);

			// Construct a new teleport object for us to add to the map of available teleports
			teleports.put(teleportName, new Teleport(teleportName, child.getIndex(), shortcutKey, alt));
		}

		return teleports;
	}

	/**
	 * Creates the teleport icon widgets and places them
	 * in their correct position on the nexus widget pane
	 * @param root the widget layer on which to create the widget
	 */
	private void createTeleportWidgets(Widget root)
	{
		// Iterate through each of the map regions
		for (int i = 0; i < REGION_COUNT; i++)
		{
			// Current map region
			NexusRegion region = this.regions[i];

			// Get the definitions for the teleports within this map region
			Map<Point, TeleportInfo> teleports = region.getTeleports();

			for (Point position : teleports.keySet())
			{
				// The definition for this teleport
				TeleportInfo teleInfo = teleports.get(position);

				// Create the teleport icon widget
				Widget teleportWidget = root.createChild(-1, WidgetType.GRAPHIC);

				// Create a button wrapper for the teleport widget. Set the dimensions,
				// the position and the visibility to hidden
				UIButton teleportButton = new UIButton(teleportWidget);
				teleportButton.setSize(TELE_ICON_SIZE, TELE_ICON_SIZE);
				teleportButton.setPosition(position.getX(), position.getY());
				teleportButton.setVisibility(false);

				this.mapPages.get(i).add(teleportButton);

				// Check that the teleport is available to the player
				if (this.isTeleportAvailable(teleInfo))
				{
					// Grab the teleport from the list of available teleports
					Teleport teleport = this.getAvailableTeleport(teleInfo);

					// Set the sprite to the active icon for this spell
					teleportButton.setSprites(teleInfo.getSpriteEnabled());

					// Get the teleport name, formatted with alias
					String teleportName = getFormattedLocationName(teleInfo);

					// If enabled in the config, prepend the shortcut key for this
					// teleport to the beginning of the teleport name
					if (this.config.displayShortcuts())
						teleportName = this.prependShortcutKey(teleportName, teleport.getKeyShortcut());

					// Assign the teleport name
					teleportButton.setName(teleportName);

					// Add the menu options and listener, activate listeners
					teleportButton.addAction(ACTION_TEXT_TELE, () -> triggerTeleport(teleport));
				}
				else
				{
					// If the spell isn't available to the player, display the
					// deactivated spell icon instead
					teleportButton.setSprites(teleInfo.getSpriteDisabled());
				}
			}
		}
	}

	/**
	 * Teleports the player to the specified teleport location
	 * @param teleport the teleport location
	 */
    private void triggerTeleport(Teleport teleport)
    {
		// Get the appropriate widget parent for the teleport, depending
		// on whether the teleport is of primary or alternate type
		WidgetInfo widget = teleport.isAlt() ? WidgetInfo.NEXUS_KEYS_ALTERNATE : WidgetInfo.NEXUS_KEYS_PRIMARY;

		// ID of the parent and index of the target child
    	final int widgetID = widget.getId();
    	final int widgetIndex = teleport.getChildIndex();

    	// Call a CS2 script which will trigger the widget's keypress event.
		// Credit to Abex for discovering this clever trick.
		this.clientThread.invokeLater(() -> client.runScript(SCRIPT_TRIGGER_KEY, widgetID, widgetIndex));
    }

    private boolean isTeleportAvailable(TeleportInfo teleInfo)
	{
		return this.availableTeleports.containsKey(teleInfo.getName());
	}

	private Teleport getAvailableTeleport(TeleportInfo teleportInfo)
	{
		return this.availableTeleports.get(teleportInfo.getName());
	}

	/**
	 * Prepends the shortcut key to the teleport name
	 * @param name the teleport name
	 * @param key the shortcut key
	 * @return the teleport name with the shortcut key prepended
	 */
	private String prependShortcutKey(String name, String key)
	{
		return String.format("[%s] %s", key, name);
	}

	/**
	 * Creates a formatted string which is to be used as the name
	 * for the teleport icons. The string contains the base name of
	 * the teleport, and the alias name of the teleport, if is applicable.
	 * @param teleInfo the teleport definition
	 * @return the formatted string
	 */
	private String getFormattedLocationName(TeleportInfo teleInfo)
	{
		// Create the base name
		String name = teleInfo.getName();

		// If this location has an alias, append it to the
		// end of the name string, enclosed in parenthesis
		if (teleInfo.hasAlias())
			name += String.format(" (%s)", teleInfo.getAlias());

		return name;
	}
}
