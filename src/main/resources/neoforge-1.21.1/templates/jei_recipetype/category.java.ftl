<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

package ${package}.jei_recipes;

public class ${name}RecipeCategory implements IRecipeCategory<${name}Recipe> {
    public final static ResourceLocation UID = ResourceLocation.parse("${modid}:${data.getModElement().getRegistryName()}");
    public final static ResourceLocation TEXTURE = ResourceLocation.parse("${modid}:textures/screens/${data.textureSelector}");

    private final IDrawable background;
    private final IDrawable icon;

    private final Minecraft mc = Minecraft.getInstance();

    public ${name}RecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, ${data.width}, ${data.height});
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(${mappedMCItemToItem(data.icon)}));
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<${name}Recipe> getRecipeType() {
        return ${JavaModName}JeiPlugin.${name}_Type;
    }

    @Override
    public Component getTitle() {
        return Component.literal("${data.title}");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void draw(${name}Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        
		<#list data.getComponentsOfType("Label") as component>
			<#if hasProcedure(component.displayCondition)>
				if (<@valueProvider component.displayCondition/>)
			</#if>
			guiGraphics.drawString(mc.font,
				<#if hasProcedure(component.text)><@valueProvider component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
				${component.gx(data.width)}, ${component.gy(data.height) - 28}, ${component.color.getRGB()}, false);
		</#list>
    }

    <#assign tooltips = data.getComponentsOfType("Tooltip")>
    <#if tooltips?size != 0>
    public void getTooltip(ITooltipBuilder tooltip, ${name}Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		<#list tooltips as component>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height) - 28>
			<#if hasProcedure(component.displayCondition)>
				if (<@valueProvider component.displayCondition/>)
			</#if>
				if (mouseX > ${x} && mouseX < ${x + component.width} && mouseY > ${y} && mouseY < ${y + component.height}) {
					<#if hasProcedure(component.text)>
					String hoverText = <@valueProvider component.text/>;
					if (hoverText != null) {
						tooltip.addAll(Arrays.stream(hoverText.split("\n")).map(Component::literal).collect(Collectors.toList()));
					}
					<#else>
						tooltip.add(Component.translatable("gui.${modid}.${registryname}.${component.getName()}"));
					</#if>
				}
		</#list>
	}
	</#if>

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ${name}Recipe recipe, IFocusGroup focuses) {
        <#if data.enableIntList>
            List<ItemStack> stacks = new ArrayList<>();
        </#if>
        List<ItemStack> recipeOutputs = recipe.getResultItems();
        List<ItemStack> actualOutputs = NonNullList.withSize(${data.getResultCount()}, ItemStack.EMPTY);
        for (int i = 0; i < recipeOutputs.size(); i++) {
            actualOutputs.set(i, recipeOutputs.get(i));
        }
        <#list data.slotList as slot>
            <#if slot.type == "INPUT">
                <#if data.enableIntList>
                    stacks.clear();
                    for (ItemStack item : (List<ItemStack>) List.of(recipe.getIngredients().get(${slot.slotid}).getItems()))
                        stacks.add(new ItemStack(item.getItem(), recipe.integers().get(${slot.slotid})));
                    builder.addSlot(RecipeIngredientRole.INPUT, ${slot.x}, ${slot.y}).addItemStacks(stacks);
                <#else>
                    builder.addSlot(RecipeIngredientRole.INPUT, ${slot.x}, ${slot.y}).addIngredients(recipe.getIngredients().get(${slot.slotid}));
                </#if>
            <#else>
                builder.addSlot(RecipeIngredientRole.OUTPUT, ${slot.x}, ${slot.y}).addItemStack(actualOutputs.get(${slot.slotid}));
            </#if>
        </#list>
    }

}

<#macro valueProvider procedure="">
    <@procedureCode procedure, {
		"x": "mc.player.getX()",
		"y": "mc.player.getY()",
		"z": "mc.player.getZ()",
		"world": "mc.level",
		"entity": "mc.player",
		"strings": "${data.enableStringList?then('recipe.strings()', 'null')}"
	}, false/>
</#macro>