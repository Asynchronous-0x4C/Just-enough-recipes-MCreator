package net.nerdypuzzle.jei.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import java.util.ArrayList;
import java.util.List;

public class JeiRecipe extends GeneratableElement {

    public String category;
    @ModElementReference
    public List<MItemBlock> ingredients;
    public List<Integer> integers;
    public List<String> strings;
    public String recipetype = "";
    @ModElementReference
    public List<MItemBlock> results;
    public List<Integer> resultCounts;

    public MItemBlock result; // Deprecated, only in place for backwards compatibility
    public int count; // Deprecated, only in place for backwards compatibility

    public JeiRecipe(ModElement element) {
        super(element);
        integers = new ArrayList<>();
        strings = new ArrayList<>();
        recipetype = "";
        results = new ArrayList<>();
        resultCounts = new ArrayList<>();
    }

}
