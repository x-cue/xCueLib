package com.xcue.xcuelib.utils.item;

import com.xcue.xcuelib.compatibility.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ItemUtils {

    public static Material getSmeltResult(ItemStack item) {
        Material result = null;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();

            if (item.getType().toString().toUpperCase().endsWith("_LOG")) {
                result = XMaterial.CHARCOAL.parseMaterial();
                break;
            }

            if (!(recipe instanceof FurnaceRecipe)) {
                continue;
            }

            if (((FurnaceRecipe) recipe).getInput().getType() != item.getType()) {
                continue;
            }

            result = XMaterial.matchXMaterial(recipe.getResult().getType()).parseMaterial();
            break;
        }
        return result;
    }

    public static ItemStack fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | IllegalArgumentException | ClassNotFoundException e) {
            throw new IOException("Error while converting from Base64 to ItemStack", e);
        }
    }

    public static String itemStackToBase64(ItemStack item) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IOException("Error while converting from Base64 to ItemStack", e);
        }
    }
}
