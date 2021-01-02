package net.runelite.client.plugins.nexusmap;

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
import net.runelite.api.Varbits;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nexusmap.ui.ButtonWidget;
import net.runelite.client.plugins.nexusmap.ui.FadeButton;

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
	private static final int REGION_ID_ZEAH = 9;

	private static final int TELE_ICON_SIZE = 24;

	private static final int MAP_SPRITE_POS_X = 32;
	private static final int MAP_SPRITE_POS_Y = 18;
	private static final int MAP_SPRITE_WIDTH = 400;
	private static final int MAP_SPRITE_HEIGHT = 214;

	private static final String ACTION_TEXT_TELE = "Teleport";

	private static final int SCRIPT_TRIGGER_KEY = 1437;

	private static final String TELE_NAME_PATTERN = "<col=ffffff>(\\S)<\\/col> :  (.+)";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SpriteManager spriteManager;

	private Map<String, Teleport> availableTeleports;
	private Map<String, TeleportInfo> teleportInfoMap;

	private NexusRegion[] regions;

	/* Widgets */
	private List<WidgetInfo> hiddenWidgets;
	private Map<String, ButtonWidget> teleportButtons;
	private Widget[] indexMaps;

	@Override
	protected void startUp() throws Exception
	{
		this.createHiddenWidgetList();
		this.createTeleportInfoMap();
		this.createNexusRegions();
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
		this.spriteManager.addSpriteOverrides(NexusSprites.values());
//		this.spriteManager.addSpriteOverrides(SpellSprite.values());
	}

	private void createNexusRegions()
	{
		this.regions = new NexusRegion[REGION_COUNT];

		this.regions[REGION_ID_MISTHALIN] = new NexusRegion(REGION_ID_MISTHALIN, "Misthalin");
		this.regions[REGION_ID_MISTHALIN].setIconPosition(252, 108);
		this.regions[REGION_ID_MISTHALIN].setMapSprites(REGION_MAP_MISTHALIN, NexusSprites.MISTHALIN.getSpriteId());
		this.regions[REGION_ID_MISTHALIN].addTeleport(235, 211, TeleportInfo.LUMBRIDGE_GRAVEYARD);
		this.regions[REGION_ID_MISTHALIN].addTeleport(165, 138, TeleportInfo.DRAYNOR_MANOR);
		this.regions[REGION_ID_MISTHALIN].addTeleport(218, 93, TeleportInfo.VARROCK);
		this.regions[REGION_ID_MISTHALIN].addTeleport(193, 72, TeleportInfo.GRAND_EXCHANGE);
		this.regions[REGION_ID_MISTHALIN].addTeleport(214, 211, TeleportInfo.LUMBRIDGE);
		this.regions[REGION_ID_MISTHALIN].addTeleport(274, 130, TeleportInfo.SENNTISTEN);

		this.regions[REGION_ID_KARAMJA] = new NexusRegion(REGION_ID_KARAMJA, "Karamja");
		this.regions[REGION_ID_KARAMJA].setIconPosition(195, 155);
		this.regions[REGION_ID_KARAMJA].setMapSprites(REGION_MAP_KARAMJA, NexusSprites.KARAMJA.getSpriteId());

		this.regions[REGION_ID_ASGARNIA] = new NexusRegion(REGION_ID_ASGARNIA, "Asgarnia");
		this.regions[REGION_ID_ASGARNIA].setIconPosition(205, 88);
		this.regions[REGION_ID_ASGARNIA].setMapSprites(REGION_MAP_ASGARNIA, NexusSprites.ASGARNIA.getSpriteId());
		this.regions[REGION_ID_ASGARNIA].addTeleport(176, 19, TeleportInfo.TROLL_STRONGHOLD);
		this.regions[REGION_ID_ASGARNIA].addTeleport(241, 120, TeleportInfo.MIND_ALTAR);
		this.regions[REGION_ID_ASGARNIA].addTeleport(240, 181, TeleportInfo.FALADOR);

		this.regions[REGION_ID_DESERT] = new NexusRegion(REGION_ID_DESERT, "Kharidian Desert");
		this.regions[REGION_ID_DESERT].setIconPosition(268, 167);
		this.regions[REGION_ID_DESERT].setMapSprites(REGION_MAP_DESERT, NexusSprites.DESERT.getSpriteId());

		this.regions[REGION_ID_MORYTANIA] = new NexusRegion(REGION_ID_MORYTANIA, "Morytania");
		this.regions[REGION_ID_MORYTANIA].setIconPosition(311, 107);
		this.regions[REGION_ID_MORYTANIA].setMapSprites(REGION_MAP_MORYTANIA, NexusSprites.MORYTANIA.getSpriteId());
		this.regions[REGION_ID_MORYTANIA].addTeleport(72, 84, TeleportInfo.SALVE_GRAVEYARD);
		this.regions[REGION_ID_MORYTANIA].addTeleport(127, 50, TeleportInfo.FENKEN_CASTLE);
		this.regions[REGION_ID_MORYTANIA].addTeleport(412, 218, TeleportInfo.HARMONY_ISLAND);
		this.regions[REGION_ID_MORYTANIA].addTeleport(92, 75, TeleportInfo.KHARYRLL);
		this.regions[REGION_ID_MORYTANIA].addTeleport(133, 168, TeleportInfo.BARROWS);

		this.regions[REGION_ID_WILDERNESS] = new NexusRegion(REGION_ID_WILDERNESS, "Wilderness");
		this.regions[REGION_ID_WILDERNESS].setIconPosition(251, 44);
		this.regions[REGION_ID_WILDERNESS].setMapSprites(REGION_MAP_WILDERNESS, NexusSprites.WILDERNESS.getSpriteId());
		this.regions[REGION_ID_WILDERNESS].addTeleport(136, 125, TeleportInfo.FORGOTTEN_CEMETERY);
		this.regions[REGION_ID_WILDERNESS].addTeleport(234, 162, TeleportInfo.CARRALLANGAR);
		this.regions[REGION_ID_WILDERNESS].addTeleport(290, 50, TeleportInfo.ANNAKARL);
		this.regions[REGION_ID_WILDERNESS].addTeleport(136, 54, TeleportInfo.GHORROCK);

		this.regions[REGION_ID_KANDARIN] = new NexusRegion(REGION_ID_KANDARIN, "Kandarin");
		this.regions[REGION_ID_KANDARIN].setIconPosition(140, 118);
		this.regions[REGION_ID_KANDARIN].setMapSprites(REGION_MAP_KANDARIN, NexusSprites.KANDARIN.getSpriteId());
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
		this.regions[REGION_ID_FREMENNIK].setMapSprites(REGION_MAP_FREMENNIK, NexusSprites.FREMENNIK.getSpriteId());
		this.regions[REGION_ID_FREMENNIK].addTeleport(35, 75, TeleportInfo.LUNAR_ISLE);
		this.regions[REGION_ID_FREMENNIK].addTeleport(248, 135, TeleportInfo.WATERBIRTH_ISLAND);
		this.regions[REGION_ID_FREMENNIK].addTeleport(400, 32, TeleportInfo.WEISS);

		this.regions[REGION_ID_TIRANNWN] = new NexusRegion(REGION_ID_TIRANNWN, "Tirannwn");
		this.regions[REGION_ID_TIRANNWN].setIconPosition(84, 128);
		this.regions[REGION_ID_TIRANNWN].setMapSprites(REGION_MAP_TIRANNWN, NexusSprites.TIRANNWN.getSpriteId());

		this.regions[REGION_ID_ZEAH] = new NexusRegion(REGION_ID_ZEAH, "Zeah");
		this.regions[REGION_ID_ZEAH].setIconPosition(-500, -500);
		this.regions[REGION_ID_ZEAH].setMapSprites(REGION_MAP_ZEAH, -1);
		this.regions[REGION_ID_ZEAH].addTeleport(1, 0, TeleportInfo.BATTLEFRONT);
		this.regions[REGION_ID_ZEAH].addTeleport(2, 0, TeleportInfo.KOUREND_CASTLE);
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
			// Hide the default widgets
			this.setDefaultWidgetVisibility(false);

			this.buildAvailableTeleportList();
			this.createTeleportWidgets();

			Widget parentPane = this.client.getWidget(WidgetInfo.NEXUS_PORTAL_PANEL);

			Widget backArrowWidget = parentPane.createChild(-1, WidgetType.GRAPHIC);

			FadeButton backArrowButton = new FadeButton(backArrowWidget);
			backArrowButton.setSprites(SpriteID.GE_BACK_ARROW_BUTTON);
			backArrowButton.setPosition(6, 6);
			backArrowButton.setSize(30, 23);
			backArrowButton.addAction("Back", this::onBackButtonPressed);
		}
	}

	/**
	 * Creates a list of widgets that the plugin
	 * does not require in order to function, for
	 * them to be hidden and shown as required
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

	/**
	 * Shows or hides the default menu widgets
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

	/**
	 * Called when the back arrow has been pressed
	 */
	private void onBackButtonPressed()
	{
		System.out.println("Back button pressed!");
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
	 */
	private void createTeleportWidgets()
	{
		// Create a map in which we can keep track of the teleport buttons, making them
		// easier to access when attempting to show/hide the teleports for the region
		this.teleportButtons = new HashMap<>();

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

				// Get the parent pane for the nexus widgets
				Widget parentPane = this.client.getWidget(WidgetInfo.NEXUS_PORTAL_PANEL);

				// Create the teleport icon widget
				Widget teleportWidget = parentPane.createChild(-1, WidgetType.GRAPHIC);

				// Create a button wrapper for the teleport widget. Set the dimensions,
				// the position and the visibility to hidden
				ButtonWidget teleportButton = new ButtonWidget(teleportWidget);
				teleportButton.setSize(TELE_ICON_SIZE, TELE_ICON_SIZE);
				teleportButton.setPosition(position.getX(), position.getY());
				teleportButton.setVisibility(false);

				// Check that the teleport is available to the player
				if (this.isTeleportAvailable(teleInfo))
				{
					// Grab the teleport from the list of available teleports
					Teleport teleport = this.getAvailableTeleport(teleInfo);

					// Set the sprite to the active icon for this spell
					teleportButton.setSprites(teleInfo.getSpriteEnabled());

					// Format and assign the teleport name
					teleportButton.setName(getFormattedLocationName(teleInfo, teleport.getKeyShortcut()));

					// Add the menu options and listener, activate listeners
					teleportButton.addAction(ACTION_TEXT_TELE, () -> triggerTeleport(teleport));
				}
				else
				{
					// If the spell isn't available to the player, display the
					// deactivated spell icon instead
					teleportButton.setSprites(teleInfo.getSpriteDisabled());
				}

				// Place the widget into the lookup table
				this.teleportButtons.put(teleInfo.getName(), teleportButton);
			}
		}
	}

	/**
	 * Toggles the visibility of the teleport widgets for a given region
	 * @param regionID the region ID
	 * @param visible true to set the widgets to visible, false for hidden
	 */
	private void toggleTeleportsForRegion(int regionID, boolean visible)
	{
		// If region ID is outside of valid range, return
		if (regionID < 0 || regionID >= REGION_COUNT)
			return;

		// Get the region for the provided ID
		NexusRegion region = this.regions[regionID];

		// Go through each of the defined teleports for the region
		for (TeleportInfo teleInfo : region.getTeleports().values())
		{
			// Get the teleport button for the current teleport location
			ButtonWidget teleportWidget = this.teleportButtons.get(teleInfo.getName());

			// Update the visibility for the widget
			teleportWidget.setVisibility(visible);
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
	 * Creates a formatted string which is to be used as the name
	 * for the teleport icons. The string contains the base name of
	 * the teleport, the shortcut key and the alias if is applicable.
	 * @param teleInfo the teleport definition
	 * @param key the shortcut key
	 * @return the formatted string
	 */
	private String getFormattedLocationName(TeleportInfo teleInfo, String key)
	{
		// Create the base name, with the shortcut key prepended
		String name = String.format("[%s] %s", key, teleInfo.getName());

		// If this location has an alias, append it to the
		// end of the name string, enclosed in parenthesis
		if (teleInfo.hasAlias())
			name += String.format(" (%s)", teleInfo.getAlias());

		return name;
	}
}
