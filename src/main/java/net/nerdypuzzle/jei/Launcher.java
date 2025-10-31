package net.nerdypuzzle.jei;

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.nerdypuzzle.jei.parts.PluginElementTypes;
import net.nerdypuzzle.jei.parts.gui.components.JeiSprite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher extends JavaPlugin {

	private static final Logger LOG = LogManager.getLogger("Just enough recipes");

	public Launcher(Plugin plugin) {
		super(plugin);

        addListener(PreGeneratorsLoadingEvent.class, event -> {
            PluginElementTypes.load();
            GUIComponent.registerCustomComponent(JeiSprite.class);
        });

		LOG.info("JEI plugin was loaded");
	}

}