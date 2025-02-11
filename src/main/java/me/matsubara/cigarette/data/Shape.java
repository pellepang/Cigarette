package me.matsubara.cigarette.data;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Strings;
import me.matsubara.cigarette.CigarettePlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class Shape {

    private final CigarettePlugin plugin;

    private final String name;
    private final boolean shaped;
    private final List<String> ingredients;
    private final List<String> shape;

    private Recipe recipe;

    public Shape(CigarettePlugin plugin, String name, boolean shaped, List<String> ingredients, List<String> shape, ItemStack result) {
        this.plugin = plugin;
        this.name = name;
        this.shaped = shaped;
        this.ingredients = ingredients;
        this.shape = shape;
        register(result);
    }

    @SuppressWarnings("deprecation")
    public void register(ItemStack item) {
        // Since 1.12, a namespaced key is required.
        if (ReflectionUtils.VER > 11) {
            NamespacedKey key = new NamespacedKey(plugin, "cigarette_" + name);
            recipe = shaped ? new ShapedRecipe(key, item) : new ShapelessRecipe(key, item);
        } else {
            recipe = shaped ? new ShapedRecipe(item) : new ShapelessRecipe(item);
        }

        if (shaped) {
            ((ShapedRecipe) recipe).shape(shape.toArray(new String[0]));
        }

        for (String ingredient : ingredients) {
            if (Strings.isNullOrEmpty(ingredient) || ingredient.equalsIgnoreCase("none")) continue;
            String[] split = StringUtils.split(StringUtils.deleteWhitespace(ingredient), ',');
            if (split.length == 0) split = StringUtils.split(ingredient, ' ');

            Optional<XMaterial> typeOpt = XMaterial.matchXMaterial(split[0]);
            if (!typeOpt.isPresent()) continue;
            Material type = typeOpt.get().parseMaterial();
            if (type == null) return;

            char key = ' ';

            if (split.length > 1) {
                key = split[1].charAt(0);
            }

            if (shaped) {
                // Empty space are used for AIR.
                if (key == ' ') continue;
                ((ShapedRecipe) recipe).setIngredient(key, type);
            } else {
                ((ShapelessRecipe) recipe).addIngredient(type);
            }
        }

        Bukkit.addRecipe(recipe);
    }

    public Recipe getRecipe() {
        return recipe;
    }
}