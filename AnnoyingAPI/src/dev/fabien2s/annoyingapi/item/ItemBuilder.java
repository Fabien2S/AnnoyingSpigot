package dev.fabien2s.annoyingapi.item;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    private final ItemStack itemStack;

    public ItemBuilder() {
        this(Material.AIR);
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack other) {
        this.itemStack = new ItemStack(other);
    }

    private ItemBuilder applyMeta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            ItemFactory itemFactory = Bukkit.getItemFactory();
            Material type = itemStack.getType();
            itemMeta = itemFactory.getItemMeta(type);
            if (itemMeta == null)
                return this; // this is probably air
        }

        metaConsumer.accept(itemMeta);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder withType(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder withAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder withName(String name) {
        return applyMeta(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemBuilder withLocalizedName(String name) {
        return applyMeta(itemMeta -> itemMeta.setLocalizedName(name));
    }

    public ItemBuilder withFlags(ItemFlag... flags) {
        return applyMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemBuilder withUnbreakable(boolean unbreakable) {
        return applyMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public <T> ItemBuilder withTag(NamespacedKey key, PersistentDataType<T, String> type) {
        return withTag(key, type, key.getKey());
    }

    public <T, Z> ItemBuilder withTag(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        return applyMeta(itemMeta -> {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(key, type, value);
        });
    }

    public ItemBuilder withCustomModelData(int customModelData) {
        return applyMeta(itemMeta -> itemMeta.setCustomModelData(customModelData));
    }

    public ItemBuilder withAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        return applyMeta(itemMeta -> itemMeta.addAttributeModifier(attribute, modifier));
    }

    public ItemBuilder withLore(String... lines) {
        return applyMeta(itemMeta -> {
            if (lines != null) {
                List<String> lore = itemMeta.getLore();
                if (lore == null)
                    lore = new ArrayList<>();
                Collections.addAll(lore, lines);
                itemMeta.setLore(lore);
            } else
                itemMeta.setLore(null);
        });
    }

    public ItemBuilder clearLore() {
        return withLore((String[]) null);
    }

    public ItemBuilder withEnchantment(Enchantment enchantment) {
        return withEnchantment(enchantment, enchantment.getStartLevel(), true);
    }

    public ItemBuilder withEnchantment(Enchantment enchantment, int level) {
        return withEnchantment(enchantment, level, true);
    }

    public ItemBuilder withEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        return applyMeta(itemMeta -> itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction));
    }

    public ItemBuilder withDamage(int damage) {
        return applyMeta(itemMeta -> {
            if (itemMeta instanceof Damageable)
                ((Damageable) itemMeta).setDamage(damage);
        });
    }

    public ItemBuilder withColor(Color color) {
        return applyMeta(itemMeta -> {
            if (itemMeta instanceof LeatherArmorMeta)
                ((LeatherArmorMeta) itemMeta).setColor(color);
            else if (itemMeta instanceof PotionMeta)
                ((PotionMeta) itemMeta).setColor(color);
            else if (itemMeta instanceof MapMeta)
                ((MapMeta) itemMeta).setColor(color);
        });
    }

    public ItemBuilder clearColor() {
        return withColor(null);
    }

    public ItemBuilder withSkullOwner(OfflinePlayer offlinePlayer) {
        return applyMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta)
                ((SkullMeta) itemMeta).setOwningPlayer(offlinePlayer);
        });
    }

    public ItemBuilder withPotionType(PotionType potionType) {
        return applyMeta(itemMeta -> {
            if (itemMeta instanceof PotionMeta)
                ((PotionMeta) itemMeta).setBasePotionData(new PotionData(potionType));
        });
    }

    public ItemBuilder withChargedProjectiles(List<ItemStack> itemStacks) {
        return applyMeta(itemMeta -> {
            if (itemMeta instanceof CrossbowMeta crossbowMeta) {
                crossbowMeta.setChargedProjectiles(itemStacks);
            }
        });
    }

    public ItemStack build() {
        return itemStack.clone();
    }

}
