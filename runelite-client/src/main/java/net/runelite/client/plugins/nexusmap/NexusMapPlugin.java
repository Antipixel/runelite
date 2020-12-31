package net.runelite.client.plugins.nexusmap;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Nexus Menu Map",
        description = "Replaces the player owned house teleport Nexus menu",
        tags = {"poh", "portal", "teleport", "nexus"}
)
public class NexusMapPlugin extends Plugin
{
	private static final int SCRIPT_TRIGGER_KEY = 1437;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    /**
     * Hides the default nexus widgets that are
     * not required for the plugin to function
     */
    private void hideDefaultWidgets()
    {
        // 17.3 (Icon)
        // 17.4 (Icon)
        // 17.5 (Scry Select)
        // 17.9 (Border)
        // 17.11 (Content)
        // 17.14 (Scrollbar)
    }

    // 17.7 is listeners for alternate teleports
    // 17.8 is listeners for primary teleports

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded e)
    {
        if (e.getGroupId() == 17)
        {
            Widget w = client.getWidget(17, 7);

//            clientThread.invokeLater(() -> client.runScript(1437, w.getId(), 0));
        }
    }

    private void triggerTeleport(Teleport teleport)
    {
    	int widgetSubID = teleport.isAlternate() ? 7 : 8;

    	final Widget w = this.client.getWidget(17, widgetSubID);

		this.clientThread.invokeLater(() -> client.runScript(SCRIPT_TRIGGER_KEY, w.getId(), teleport.getChildIndex()));
    }
}
