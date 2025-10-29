package net.nerdypuzzle.jei.parts.gui.components;


import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.wysiwyg.AbstractWYSIWYGDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.nerdypuzzle.jei.parts.gui.JeiGuiEditor;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class JeiSpriteDialog extends AbstractWYSIWYGDialog<JeiSprite> {

    public JeiSpriteDialog(JeiGuiEditor editor, @Nullable JeiSprite sprite) {
        super(editor.getFakeEditor(), sprite);
        setSize(650, 200);
        setLocationRelativeTo(editor.mcreator);
        setModal(true);
        setTitle(L10N.t("dialog.gui.sprite_title"));

        setLayout(new BorderLayout(5, 5));

        JPanel options = new JPanel(new BorderLayout(2, 2));

        TextureComboBox textureSelector = new TextureComboBox(editor.mcreator, TextureType.SCREEN, false);

        SpinnerNumberModel spritesCountModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner spritesCount = new JSpinner(spritesCountModel);

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, (int) spritesCount.getValue() - 1, 1);
        JSpinner spinner = new JSpinner(model);

        AbstractProcedureSelector.ReloadContext context = AbstractProcedureSelector.ReloadContext.create(
                editor.mcreator.getWorkspace());

        SpinnerNumberModel updateIntervalModel = new SpinnerNumberModel(10, 1, 20, 1);
        JSpinner updateInterval = new JSpinner(updateIntervalModel);

        JPanel opts = new JPanel(new GridLayout(2, 2, 2, 2));

        opts.add(L10N.label("dialog.gui.image_texture"));
        opts.add(textureSelector);
        opts.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/sprite_count"),
                L10N.label("dialog.gui.sprite_count")));
        opts.add(spritesCount);

        JPanel intervalPanel = new JPanel(new GridLayout(1, 2, 2, 2));

        intervalPanel.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("jei/update_interval"),
                L10N.label("dialog.gui.sprite_update_interval")));
        intervalPanel.add(updateInterval);

        options.add("Center", PanelUtils.centerAndSouthElement(opts, intervalPanel, 2, 2));

        ProcedureSelector displayCondition = new ProcedureSelector(
                IHelpContext.NONE.withEntry("gui/sprite_display_condition"), editor.mcreator,
                L10N.t("dialog.gui.sprite_display_condition"), ProcedureSelector.Side.CLIENT, false,
                VariableTypeLoader.BuiltInTypes.LOGIC,
                Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/strings:stringlist"));
        displayCondition.refreshList(context);

        final int[] previousSpritesCount = { (int) spritesCount.getValue() };
        spritesCount.addChangeListener(e -> {
            int currentSpritesCount = (int) spritesCount.getValue();

            model.setMaximum(currentSpritesCount - 1);
            if (previousSpritesCount[0] > currentSpritesCount
                    && model.getNumber().intValue() == previousSpritesCount[0] - 1)
                model.setValue(model.getNumber().intValue() - 1);
            previousSpritesCount[0] = currentSpritesCount;
        });

        textureSelector.getComboBox().addActionListener(e -> {
            if (textureSelector.getTexture() != null) {
                ImageIcon selectedTexture = textureSelector.getTexture().getTextureIcon(editor.mcreator.getWorkspace());
                int maximum = Math.max(selectedTexture.getIconWidth(), selectedTexture.getIconHeight());

                spritesCountModel.setMaximum(maximum);
                if (maximum < spritesCountModel.getNumber().intValue())
                    spritesCountModel.setValue(maximum);

                if (model.getNumber().intValue() > maximum)
                    model.setValue(maximum - 1);
            }
        });

        final JComboBox<GUIComponent.AnchorPoint> anchor = new JComboBox<>(GUIComponent.AnchorPoint.values());
        anchor.setSelectedItem(GUIComponent.AnchorPoint.CENTER);

        add("Center", PanelUtils.totalCenterInPanel(
                PanelUtils.centerAndEastElement(options, PanelUtils.pullElementUp(displayCondition), 10, 10)));

        JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

        getRootPane().setDefaultButton(ok);

        JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
        add("South", PanelUtils.join(ok, cancel));

        if (sprite != null) {
            ok.setText(L10N.t("dialog.common.save_changes"));
            textureSelector.setTextureFromTextureName(sprite.sprite);
            spritesCount.setValue(sprite.spritesCount);
            displayCondition.setSelectedProcedure(sprite.displayCondition);
            updateInterval.setValue(sprite.updateInterval);
            anchor.setSelectedItem(sprite.anchorPoint);
        }

        cancel.addActionListener(arg01 -> dispose());
        ok.addActionListener(arg01 -> {
            dispose();
            if (textureSelector.hasTexture()) {
                if (sprite == null) {
                    JeiSprite component = new JeiSprite(0, 0, textureSelector.getTextureName(), (int) spritesCount.getValue(),
                            displayCondition.getSelectedProcedure(), (int) updateInterval.getValue());
                    setEditingComponent(component);
                    editor.editor.addComponent(component);
                    editor.list.setSelectedValue(component, true);
                    editor.editor.moveMode();
                } else {
                    int idx = editor.components.indexOf(sprite);
                    editor.components.remove(sprite);
                    JeiSprite spriteNew = new JeiSprite(sprite.getX(), sprite.getY(), textureSelector.getTextureName(),
                            (int) spritesCount.getValue(), displayCondition.getSelectedProcedure(),
                            (int) updateInterval.getValue());
                    editor.components.add(idx, spriteNew);
                    setEditingComponent(spriteNew);
                }
            }
        });

        setVisible(true);
    }

}