package ${package}.jei_recipes;

<#compress>

import javax.annotation.Nullable;

public record ${name}Recipe(List<ItemStack> outputs, List<Ingredient> recipeItems<#if data.enableIntList>, List<Integer> integers</#if><#if data.enableStringList>, List<String> strings</#if>) implements Recipe<RecipeInput> {
    public ${name}Recipe(List<ItemStack> outputs, List<Ingredient> recipeItems<#if data.enableIntList>, List<Integer> integers</#if><#if data.enableStringList>, List<String> strings</#if>) {
        this.outputs = outputs;
        this.recipeItems = recipeItems;
        <#if data.enableIntList>
            this.integers = integers;
        </#if>
        <#if data.enableStringList>
            this.strings = strings;
        </#if>
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.recipeItems);
    }

    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return false;
    }

    public List<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider holder) {
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getResultItems() {
        return List.copyOf(outputs);
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Type implements RecipeType<${name}Recipe> {
        private Type(){}
        public static final RecipeType<${name}Recipe> INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<${name}Recipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<${name}Recipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                        ItemStack.OPTIONAL_CODEC.listOf().fieldOf("outputs").forGetter(${name}Recipe::outputs),
                        Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(${name}Recipe::recipeItems)<#if data.enableIntList>,
                        Codec.INT.listOf().fieldOf("integers").forGetter(${name}Recipe::integers)</#if><#if data.enableStringList>,
                        Codec.STRING.listOf().fieldOf("strings").forGetter(${name}Recipe::strings)</#if>
                    )
                    .apply(builder, ${name}Recipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ${name}Recipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<${name}Recipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ${name}Recipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ${name}Recipe fromNetwork(RegistryFriendlyByteBuf buf) {
            List<Ingredient> inputs = NonNullList.withSize(buf.readVarInt(), EmptyIngredient.create());
            inputs.replaceAll(ingredients -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            List<ItemStack> outputs = NonNullList.withSize(buf.readVarInt(), ItemStack.EMPTY);
            outputs.replaceAll(results -> ItemStack.STREAM_CODEC.decode(buf));
            <#if data.enableIntList>
            List<Integer> numbers = NonNullList.withSize(buf.readVarInt(), 0);
            numbers.replaceAll(num -> buf.readVarInt());
            </#if>
            <#if data.enableStringList>
            List<String> strings = NonNullList.withSize(buf.readVarInt(), "");
            strings.replaceAll(string -> buf.readUtf());
            </#if>
            return new ${name}Recipe(outputs, inputs<#if data.enableIntList>, numbers</#if><#if data.enableStringList>, strings</#if>);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, ${name}Recipe recipe) {
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                if (ing.items().findFirst().get().value() == Items.AIR)
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, EmptyIngredient.create());
                else
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
            }
            buf.writeVarInt(recipe.getResultItems().size());
            for (ItemStack itemstack : recipe.getResultItems()) {
                ItemStack.STREAM_CODEC.encode(buf, itemstack);
            }
            <#if data.enableIntList>
            buf.writeVarInt(recipe.integers().size());
            for (Integer num : recipe.integers()) {
            	buf.writeVarInt(num);
            }
            </#if>
            <#if data.enableStringList>
            buf.writeVarInt(recipe.strings().size());
            for (String string : recipe.strings()) {
            	buf.writeUtf(string);
            }
            </#if>
        }
    }

}</#compress>