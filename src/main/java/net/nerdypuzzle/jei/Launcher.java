package net.nerdypuzzle.jei;

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.nerdypuzzle.jei.parts.PluginElementTypes;
import net.nerdypuzzle.jei.parts.gui.components.JeiSprite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Map;

public class Launcher extends JavaPlugin {

	private static final Logger LOG = LogManager.getLogger("Just enough recipes");

	public Launcher(Plugin plugin) {
		super(plugin);

		addListener(PreGeneratorsLoadingEvent.class, event -> {
            PluginElementTypes.load();
            try {
                Class<?> guiComponentClass = GUIComponent.class;

                Field typeMappingsField = guiComponentClass.getDeclaredField("typeMappings");
                typeMappingsField.setAccessible(true);

                @SuppressWarnings("unchecked")
                Map<String, Class<? extends GUIComponent>> typeMappings =
                        (Map<String, Class<? extends GUIComponent>>) typeMappingsField.get(null);

                typeMappings.put("jei_sprite", JeiSprite.class);

                Field reverseMapField = guiComponentClass.getDeclaredField("typeMappingsReverse");
                reverseMapField.setAccessible(true);

                @SuppressWarnings("unchecked")
                Map<Class<? extends GUIComponent>, String> typeMappingsReverse =
                        (Map<Class<? extends GUIComponent>, String>) reverseMapField.get(null);

                typeMappingsReverse.put(JeiSprite.class, "jei_sprite");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

		LOG.info("JEI plugin was loaded");
	}

}