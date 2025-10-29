package net.nerdypuzzle.jei.parts.gui.components;

import net.mcreator.element.parts.gui.Sprite;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;

public class JeiSprite extends Sprite {
    public int updateInterval;
    public JeiSprite(int x, int y, String sprite, int spritesCount, Procedure displayCondition, int updateInterval) {
        super(x, y, sprite, spritesCount, displayCondition, new NumberProcedure("", 0));
        this.updateInterval = updateInterval;
    }
}
