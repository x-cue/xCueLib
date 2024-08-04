package com.xcue.xcuelib.utils.item;

import com.google.common.base.Enums;
import com.google.common.base.Strings;
import com.xcue.xcuelib.compatibility.SkullUtils;
import com.xcue.xcuelib.compatibility.XMaterial;
import com.xcue.xcuelib.compatibility.XPotion;
import com.xcue.xcuelib.utils.ColorUtils;
import com.xcue.xcuelib.utils.NumberUtils;
import dev.lone.itemsadder.api.CustomStack;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;

public class MItemBuilder {

    public static ItemStack loadConfigItem(ConfigurationSection section, boolean runningItemAdder) {
        Objects.requireNonNull(section, "Cannot load item to a null configuration section.");
        ItemStack item = XMaterial.BARRIER.parseItem();

        //Material
        if (runningItemAdder && section.contains("itemsadder")) {
            if (CustomStack.isInRegistry(section.getString("itemsadder"))) {
                CustomStack stack = CustomStack.getInstance(section.getString("itemsadder"));
                item = stack.getItemStack();

                ItemMeta meta = item.getItemMeta();

                // Amount
                int amount = section.getInt("amount");
                if (amount > 1) item.setAmount(amount);

                //Durability/Damage
                if (XMaterial.supports(13)) {
                    if (meta instanceof Damageable) {
                        int damage = section.getInt("damage");
                        if (damage > 0) ((Damageable) meta).setDamage(damage);
                    }
                } else {
                    int damage = section.getInt("damage");
                    if (damage > 0) item.setDurability((short) damage);
                }

                // Display Name
                String name = section.getString("name");
                if (!Strings.isNullOrEmpty(name)) {
                    String translated = ChatColor.translateAlternateColorCodes('&', name);
                    meta.setDisplayName(translated);
                } else if (name != null && name.isEmpty()) {
                    meta.setDisplayName(" ");
                }

                // Lore
                List<String> lores = section.getStringList("lore");
                if (!lores.isEmpty()) {
                    List<String> translatedLore = new ArrayList<>(lores.size());
                    String lastColors = "";

                    for (String lore : lores) {
                        if (lore.isEmpty()) {
                            translatedLore.add(" ");
                            continue;
                        }

                        for (String singleLore : StringUtils.splitPreserveAllTokens(lore, '\n')) {
                            if (singleLore.isEmpty()) {
                                translatedLore.add(" ");
                                continue;
                            }
                            singleLore = lastColors + ChatColor.translateAlternateColorCodes('&', singleLore);
                            translatedLore.add(singleLore);

                            lastColors = ChatColor.getLastColors(singleLore);
                        }
                    }

                    meta.setLore(translatedLore);
                }

                List<String> flags = section.getStringList("flags");
                if (!flags.isEmpty()) {
                    ArrayList<ItemFlag> arrayList = new ArrayList();

                    for (String flag : flags) {
                        ItemFlag itemFlag = null;
                        try {
                            itemFlag = ItemFlag.valueOf(flag);
                        } catch (IllegalArgumentException illegalArgumentException) {
                            illegalArgumentException.printStackTrace();
                        }
                        arrayList.add(itemFlag);
                    }

                    meta.addItemFlags(arrayList.toArray(new ItemFlag[0]));
                }

                Boolean glow = section.getBoolean("glow");
                if (glow) {
                    Enchantment enchantment = item.getType().toString().contains("ROD") ? Enchantment.SILK_TOUCH : Enchantment.LURE;
                    meta.addEnchant(enchantment, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                // Custom Model Data
                if (XMaterial.supports(14)) {
                    int modelData = section.getInt("model-data");
                    if (modelData != 0) meta.setCustomModelData(modelData);
                }

                // Unbreakable
                if (XMaterial.supports(11)) meta.setUnbreakable(section.getBoolean("unbreakable"));

                //Player heads
                if (XMaterial.matchXMaterial(item.getType()) == XMaterial.PLAYER_HEAD) {
                    String skull = section.getString("skin");
                    if (skull != null) SkullUtils.applySkin(meta, skull);
                } else if (meta instanceof BannerMeta) {
                    BannerMeta banner = (BannerMeta) meta;
                    ConfigurationSection patterns = section.getConfigurationSection("patterns");

                    if (patterns != null) {
                        for (String pattern : patterns.getKeys(false)) {
                            PatternType type = PatternType.getByIdentifier(pattern + ".type");
                            if (type == null)
                                type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                            DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                            banner.addPattern(new Pattern(color, type));
                        }
                    }
                    //Leather Armor
                } else if (meta instanceof LeatherArmorMeta) {
                    Color color = ColorUtils.getColor(section.getString("color"));

                    if (color != null) {
                        LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                        leather.setColor(color);
                    }
                    //Potions and Tipped Arrows
                } else if (meta instanceof PotionMeta) {
                    if (XMaterial.supports(9)) {
                        PotionMeta potion = (PotionMeta) meta;

                        String baseEffect = section.getString("potion");
                        if (!baseEffect.isEmpty()) {
                            PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                            boolean extended = section.getBoolean("potion.extended", false);
                            int level = section.getInt("potion.level", 0);
                            PotionData potionData = new PotionData(type, extended, (level > 1));
                            potion.setBasePotionData(potionData);
                        }
                    } else {
                        if (item.equals(XMaterial.SPLASH_POTION.parseMaterial())) {
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                                boolean extended = section.getBoolean("potion.extended", false);
                                int level = section.getInt("potion.level", 0);
                                item = (new Potion(type, level, true, extended)).toItemStack(1);
                            }
                        } else if (item.equals(XMaterial.POTION.parseMaterial())) {
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                                boolean extended = section.getBoolean("potion.extended", false);
                                int level = section.getInt("potion.level", 0);
                                item = (new Potion(type, level, false, extended)).toItemStack(1);
                            }
                        }
                    }
                    //Spawners and Shields
                } else if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState state = bsm.getBlockState();

                    //Spawners
                    if (state instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        if (section.contains("mob")) {
                            spawner.setSpawnedType(Enums.getIfPresent(EntityType.class, section.getString("mob").toUpperCase(Locale.ENGLISH)).orNull());
                            spawner.update(true);
                            bsm.setBlockState(spawner);
                        }

                        //Shields
                    } else if (state instanceof Banner) {
                        Banner banner = (Banner) state;
                        ConfigurationSection patterns = section.getConfigurationSection("patterns");

                        if (StringUtils.isNotEmpty(section.getString("color"))) {
                            banner.setBaseColor(Enums.getIfPresent(DyeColor.class, section.getString("color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE));
                            if (patterns != null) {
                                for (String pattern : patterns.getKeys(false)) {
                                    PatternType type = PatternType.getByIdentifier(pattern + ".type");
                                    if (type == null)
                                        type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                                    DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                                    banner.addPattern(new Pattern(color, type));
                                }

                                banner.update(true);
                                bsm.setBlockState(banner);
                            }
                        }
                    }
                    //Firework Stars
                } else if (meta instanceof FireworkEffectMeta) {
                    FireworkEffectMeta firework = (FireworkEffectMeta) meta;
                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    if (StringUtils.isNotEmpty(section.getString("fireworkColor"))) {
                        builder.withColor(ColorUtils.getFireworkColor(section.getString("fireworkColor")));
                    }
                    if (StringUtils.isNotEmpty(section.getString("fireworkFadeColor"))) {
                        builder.withFade(ColorUtils.getFireworkColor(section.getString("fireworkFadeColor")));
                    }
                    try {
                        firework.setEffect(builder.build());
                    } catch (IllegalStateException illegalStateException) {
                        illegalStateException.printStackTrace();
                    }
                    //Fireworks
                } else if (meta instanceof FireworkMeta) {
                    FireworkMeta firework = (FireworkMeta) meta;
                    ConfigurationSection fireworkSection = section.getConfigurationSection("fireworkEffects");
                    if (fireworkSection != null) {
                        for (String fws : fireworkSection.getKeys(false)) {
                            FireworkEffect.Builder builder = FireworkEffect.builder();
                            builder.with(FireworkEffect.Type.valueOf(fireworkSection.getString(fws + ".type").toUpperCase()));

                            if (!fireworkSection.getStringList(fws + ".colors").isEmpty()) {
                                List<String> fwColors = fireworkSection.getStringList(fws + ".colors");
                                List<Color> colors = new ArrayList<>(fwColors.size());
                                for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                                builder.withColor(colors);
                            }

                            if (!fireworkSection.getStringList(fws + ".fadeColors").isEmpty()) {
                                List<String> fwColors = fireworkSection.getStringList(fws + ".fadeColors");
                                List<Color> colors = new ArrayList<>(fwColors.size());
                                for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                                builder.withFade(colors);
                            }

                            if (fireworkSection.getBoolean(fws + ".flicker")) {
                                builder.withFlicker();
                            }

                            if (fireworkSection.getBoolean(fws + ".trail")) {
                                builder.withTrail();
                            }

                            builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fireworkSection.getString(fws + ".type").toUpperCase(Locale.ENGLISH)).or(FireworkEffect.Type.STAR));
                            firework.addEffect(builder.build());
                        }
                    }
                    if (section.getInt("fireworkPower") >= 1 && section.getInt("fireworkPower") <= 4) {
                        firework.setPower(section.getInt("fireworkPower"));
                    }
                } else if (XMaterial.supports(14)) {

                    //Crossbow
                    if (meta instanceof CrossbowMeta) {
                        CrossbowMeta crossbow = (CrossbowMeta) meta;
                        if (section.contains("projectiles")) {
                            for (String projectiles : section.getConfigurationSection("projectiles").getKeys(false)) {
                                ItemStack projectile = loadConfigItem(section.getConfigurationSection("projectiles." + projectiles), runningItemAdder);
                                crossbow.addChargedProjectile(projectile);
                            }
                        }

                        //Tropical Fish Bucket
                    } else if (meta instanceof TropicalFishBucketMeta) {

                        if (section.contains("bucket")) {
                            TropicalFishBucketMeta tropical = (TropicalFishBucketMeta) meta;
                            DyeColor color = Enums.getIfPresent(DyeColor.class, section.getString("bucket.color")).or(DyeColor.WHITE);
                            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, section.getString("bucket.patternColor")).or(DyeColor.WHITE);
                            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, section.getString("bucket.pattern")).or(TropicalFish.Pattern.BETTY);

                            tropical.setBodyColor(color);
                            tropical.setPatternColor(patternColor);
                            tropical.setPattern(pattern);
                        }
                    }
                } else if (XMaterial.supports(15)) {

                    //Suspicious Stew
                    if (meta instanceof SuspiciousStewMeta) {
                        if (section.contains("potion")) {
                            SuspiciousStewMeta stew = (SuspiciousStewMeta) meta;
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                XPotion.Effect effect = XPotion.parseEffect(section.getString("potion.type", "INSTANT_HEAL"));
                                stew.addCustomEffect(effect.getEffect(), true);
                            }
                        }
                    }
                }
                item.setItemMeta(meta);
            } else {
                String material = section.getString("material");
                if (material == null) return null;
                Optional<XMaterial> matOpt = XMaterial.matchXMaterial(material);
                if (!matOpt.isPresent()) return null;

                //Item
                item = matOpt.get().parseItem();
                if (item == null) return null;

                ItemMeta meta = item.getItemMeta();

                // Amount
                int amount = section.getInt("amount");
                if (amount > 1) item.setAmount(amount);

                //Durability/Damage
                if (XMaterial.supports(13)) {
                    if (meta instanceof Damageable) {
                        int damage = section.getInt("damage");
                        if (damage > 0) ((Damageable) meta).setDamage(damage);
                    }
                } else {
                    int damage = section.getInt("damage");
                    if (damage > 0) item.setDurability((short) damage);
                }

                // Display Name
                String name = section.getString("name");
                if (!Strings.isNullOrEmpty(name)) {
                    String translated = ChatColor.translateAlternateColorCodes('&', name);
                    meta.setDisplayName(translated);
                } else if (name != null && name.isEmpty()) {
                    meta.setDisplayName(" ");
                }

                // Lore
                List<String> lores = section.getStringList("lore");
                if (!lores.isEmpty()) {
                    List<String> translatedLore = new ArrayList<>(lores.size());
                    String lastColors = "";

                    for (String lore : lores) {
                        if (lore.isEmpty()) {
                            translatedLore.add(" ");
                            continue;
                        }

                        for (String singleLore : StringUtils.splitPreserveAllTokens(lore, '\n')) {
                            if (singleLore.isEmpty()) {
                                translatedLore.add(" ");
                                continue;
                            }
                            singleLore = lastColors + ChatColor.translateAlternateColorCodes('&', singleLore);
                            translatedLore.add(singleLore);

                            lastColors = ChatColor.getLastColors(singleLore);
                        }
                    }

                    meta.setLore(translatedLore);
                }

                List<String> flags = section.getStringList("flags");
                if (!flags.isEmpty()) {
                    ArrayList<ItemFlag> arrayList = new ArrayList();

                    for (String flag : flags) {
                        ItemFlag itemFlag = null;
                        try {
                            itemFlag = ItemFlag.valueOf(flag);
                        } catch (IllegalArgumentException illegalArgumentException) {
                            illegalArgumentException.printStackTrace();
                        }
                        arrayList.add(itemFlag);
                    }

                    meta.addItemFlags(arrayList.toArray(new ItemFlag[0]));
                }

                Boolean glow = section.getBoolean("glow");
                if (glow) {
                    Enchantment enchantment = item.getType().toString().contains("ROD") ? Enchantment.SILK_TOUCH : Enchantment.LURE;
                    meta.addEnchant(enchantment, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                // Custom Model Data
                if (XMaterial.supports(14)) {
                    int modelData = section.getInt("model-data");
                    if (modelData != 0) meta.setCustomModelData(modelData);
                }

                // Unbreakable
                if (XMaterial.supports(11)) meta.setUnbreakable(section.getBoolean("unbreakable"));

                //Player heads
                if (matOpt.get() == XMaterial.PLAYER_HEAD) {
                    String skull = section.getString("skin");
                    if (skull != null) SkullUtils.applySkin(meta, skull);
                } else if (meta instanceof BannerMeta) {
                    BannerMeta banner = (BannerMeta) meta;
                    ConfigurationSection patterns = section.getConfigurationSection("patterns");

                    if (patterns != null) {
                        for (String pattern : patterns.getKeys(false)) {
                            PatternType type = PatternType.getByIdentifier(pattern + ".type");
                            if (type == null)
                                type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                            DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                            banner.addPattern(new Pattern(color, type));
                        }
                    }
                    //Leather Armor
                } else if (meta instanceof LeatherArmorMeta) {
                    Color color = ColorUtils.getColor(section.getString("color"));

                    if (color != null) {
                        LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                        leather.setColor(color);
                    }
                    //Potions and Tipped Arrows
                } else if (meta instanceof PotionMeta) {
                    if (XMaterial.supports(9)) {
                        PotionMeta potion = (PotionMeta) meta;

                        String baseEffect = section.getString("potion");
                        if (!baseEffect.isEmpty()) {
                            PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                            boolean extended = section.getBoolean("potion.extended", false);
                            int level = section.getInt("potion.level", 0);
                            PotionData potionData = new PotionData(type, extended, (level > 1));
                            potion.setBasePotionData(potionData);
                        }
                    } else {
                        if (item.equals(XMaterial.SPLASH_POTION.parseMaterial())) {
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                                boolean extended = section.getBoolean("potion.extended", false);
                                int level = section.getInt("potion.level", 0);
                                item = (new Potion(type, level, true, extended)).toItemStack(1);
                            }
                        } else if (item.equals(XMaterial.POTION.parseMaterial())) {
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                                boolean extended = section.getBoolean("potion.extended", false);
                                int level = section.getInt("potion.level", 0);
                                item = (new Potion(type, level, false, extended)).toItemStack(1);
                            }
                        }
                    }
                    //Spawners and Shields
                } else if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState state = bsm.getBlockState();

                    //Spawners
                    if (state instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        if (section.contains("mob")) {
                            spawner.setSpawnedType(Enums.getIfPresent(EntityType.class, section.getString("mob").toUpperCase(Locale.ENGLISH)).orNull());
                            spawner.update(true);
                            bsm.setBlockState(spawner);
                        }

                        //Shields
                    } else if (state instanceof Banner) {
                        Banner banner = (Banner) state;
                        ConfigurationSection patterns = section.getConfigurationSection("patterns");

                        if (StringUtils.isNotEmpty(section.getString("color"))) {
                            banner.setBaseColor(Enums.getIfPresent(DyeColor.class, section.getString("color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE));
                            if (patterns != null) {
                                for (String pattern : patterns.getKeys(false)) {
                                    PatternType type = PatternType.getByIdentifier(pattern + ".type");
                                    if (type == null)
                                        type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                                    DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                                    banner.addPattern(new Pattern(color, type));
                                }

                                banner.update(true);
                                bsm.setBlockState(banner);
                            }
                        }
                    }
                    //Firework Stars
                } else if (meta instanceof FireworkEffectMeta) {
                    FireworkEffectMeta firework = (FireworkEffectMeta) meta;
                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    if (StringUtils.isNotEmpty(section.getString("fireworkColor"))) {
                        builder.withColor(ColorUtils.getFireworkColor(section.getString("fireworkColor")));
                    }
                    if (StringUtils.isNotEmpty(section.getString("fireworkFadeColor"))) {
                        builder.withFade(ColorUtils.getFireworkColor(section.getString("fireworkFadeColor")));
                    }
                    try {
                        firework.setEffect(builder.build());
                    } catch (IllegalStateException illegalStateException) {
                        illegalStateException.printStackTrace();
                    }
                    //Fireworks
                } else if (meta instanceof FireworkMeta) {
                    FireworkMeta firework = (FireworkMeta) meta;
                    ConfigurationSection fireworkSection = section.getConfigurationSection("fireworkEffects");
                    if (fireworkSection != null) {
                        for (String fws : fireworkSection.getKeys(false)) {
                            FireworkEffect.Builder builder = FireworkEffect.builder();
                            builder.with(FireworkEffect.Type.valueOf(fireworkSection.getString(fws + ".type").toUpperCase()));

                            if (!fireworkSection.getStringList(fws + ".colors").isEmpty()) {
                                List<String> fwColors = fireworkSection.getStringList(fws + ".colors");
                                List<Color> colors = new ArrayList<>(fwColors.size());
                                for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                                builder.withColor(colors);
                            }

                            if (!fireworkSection.getStringList(fws + ".fadeColors").isEmpty()) {
                                List<String> fwColors = fireworkSection.getStringList(fws + ".fadeColors");
                                List<Color> colors = new ArrayList<>(fwColors.size());
                                for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                                builder.withFade(colors);
                            }

                            if (fireworkSection.getBoolean(fws + ".flicker")) {
                                builder.withFlicker();
                            }

                            if (fireworkSection.getBoolean(fws + ".trail")) {
                                builder.withTrail();
                            }

                            builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fireworkSection.getString(fws + ".type").toUpperCase(Locale.ENGLISH)).or(FireworkEffect.Type.STAR));
                            firework.addEffect(builder.build());
                        }
                    }
                    if (section.getInt("fireworkPower") >= 1 && section.getInt("fireworkPower") <= 4) {
                        firework.setPower(section.getInt("fireworkPower"));
                    }
                } else if (XMaterial.supports(14)) {

                    //Crossbow
                    if (meta instanceof CrossbowMeta) {
                        CrossbowMeta crossbow = (CrossbowMeta) meta;
                        if (section.contains("projectiles")) {
                            for (String projectiles : section.getConfigurationSection("projectiles").getKeys(false)) {
                                ItemStack projectile = loadConfigItem(section.getConfigurationSection("projectiles." + projectiles), runningItemAdder);
                                crossbow.addChargedProjectile(projectile);
                            }
                        }

                        //Tropical Fish Bucket
                    } else if (meta instanceof TropicalFishBucketMeta) {

                        if (section.contains("bucket")) {
                            TropicalFishBucketMeta tropical = (TropicalFishBucketMeta) meta;
                            DyeColor color = Enums.getIfPresent(DyeColor.class, section.getString("bucket.color")).or(DyeColor.WHITE);
                            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, section.getString("bucket.patternColor")).or(DyeColor.WHITE);
                            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, section.getString("bucket.pattern")).or(TropicalFish.Pattern.BETTY);

                            tropical.setBodyColor(color);
                            tropical.setPatternColor(patternColor);
                            tropical.setPattern(pattern);
                        }
                    }
                } else if (XMaterial.supports(15)) {

                    //Suspicious Stew
                    if (meta instanceof SuspiciousStewMeta) {
                        if (section.contains("potion")) {
                            SuspiciousStewMeta stew = (SuspiciousStewMeta) meta;
                            String baseEffect = section.getString("potion");
                            if (!baseEffect.isEmpty()) {
                                XPotion.Effect effect = XPotion.parseEffect(section.getString("potion.type", "INSTANT_HEAL"));
                                stew.addCustomEffect(effect.getEffect(), true);
                            }
                        }
                    }
                }
                item.setItemMeta(meta);
            }
        } else {
            String material = section.getString("material");
            if (material == null) return null;
            Optional<XMaterial> matOpt = XMaterial.matchXMaterial(material);
            if (!matOpt.isPresent()) return null;

            //Item
            item = matOpt.get().parseItem();
            if (item == null) return null;

            ItemMeta meta = item.getItemMeta();

            // Amount
            int amount = section.getInt("amount");
            if (amount > 1) item.setAmount(amount);

            //Durability/Damage
            if (XMaterial.supports(13)) {
                if (meta instanceof Damageable) {
                    int damage = section.getInt("damage");
                    if (damage > 0) ((Damageable) meta).setDamage(damage);
                }
            } else {
                int damage = section.getInt("damage");
                if (damage > 0) item.setDurability((short) damage);
            }

            // Display Name
            String name = section.getString("name");
            if (!Strings.isNullOrEmpty(name)) {
                String translated = ChatColor.translateAlternateColorCodes('&', name);
                meta.setDisplayName(translated);
            } else if (name != null && name.isEmpty()) {
                meta.setDisplayName(" ");
            }

            // Lore
            List<String> lores = section.getStringList("lore");
            if (!lores.isEmpty()) {
                List<String> translatedLore = new ArrayList<>(lores.size());
                String lastColors = "";

                for (String lore : lores) {
                    if (lore.isEmpty()) {
                        translatedLore.add(" ");
                        continue;
                    }

                    for (String singleLore : StringUtils.splitPreserveAllTokens(lore, '\n')) {
                        if (singleLore.isEmpty()) {
                            translatedLore.add(" ");
                            continue;
                        }
                        singleLore = lastColors + ChatColor.translateAlternateColorCodes('&', singleLore);
                        translatedLore.add(singleLore);

                        lastColors = ChatColor.getLastColors(singleLore);
                    }
                }

                meta.setLore(translatedLore);
            }

            List<String> flags = section.getStringList("flags");
            if (!flags.isEmpty()) {
                ArrayList<ItemFlag> arrayList = new ArrayList();

                for (String flag : flags) {
                    ItemFlag itemFlag = null;
                    try {
                        itemFlag = ItemFlag.valueOf(flag);
                    } catch (IllegalArgumentException illegalArgumentException) {
                        illegalArgumentException.printStackTrace();
                    }
                    arrayList.add(itemFlag);
                }

                meta.addItemFlags(arrayList.toArray(new ItemFlag[0]));
            }

            Boolean glow = section.getBoolean("glow");
            if (glow) {
                Enchantment enchantment = item.getType().toString().contains("ROD") ? Enchantment.SILK_TOUCH : Enchantment.LURE;
                meta.addEnchant(enchantment, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // Custom Model Data
            if (XMaterial.supports(14)) {
                int modelData = section.getInt("model-data");
                if (modelData != 0) meta.setCustomModelData(modelData);
            }

            // Unbreakable
            if (XMaterial.supports(11)) meta.setUnbreakable(section.getBoolean("unbreakable"));

            //Player heads
            if (matOpt.get() == XMaterial.PLAYER_HEAD) {
                String skull = section.getString("skin");
                if (skull != null) SkullUtils.applySkin(meta, skull);
            } else if (meta instanceof BannerMeta) {
                BannerMeta banner = (BannerMeta) meta;
                ConfigurationSection patterns = section.getConfigurationSection("patterns");

                if (patterns != null) {
                    for (String pattern : patterns.getKeys(false)) {
                        PatternType type = PatternType.getByIdentifier(pattern + ".type");
                        if (type == null)
                            type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                        DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                        banner.addPattern(new Pattern(color, type));
                    }
                }
                //Leather Armor
            } else if (meta instanceof LeatherArmorMeta) {
                Color color = ColorUtils.getColor(section.getString("color"));

                if (color != null) {
                    LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                    leather.setColor(color);
                }
                //Potions and Tipped Arrows
            } else if (meta instanceof PotionMeta) {
                if (XMaterial.supports(9)) {
                    PotionMeta potion = (PotionMeta) meta;

                    String baseEffect = section.getString("potion");
                    if (!baseEffect.isEmpty()) {
                        PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                        boolean extended = section.getBoolean("potion.extended", false);
                        int level = section.getInt("potion.level", 0);
                        PotionData potionData = new PotionData(type, extended, (level > 1));
                        potion.setBasePotionData(potionData);
                    }
                } else {
                    if (item.equals(XMaterial.SPLASH_POTION.parseMaterial())) {
                        String baseEffect = section.getString("potion");
                        if (!baseEffect.isEmpty()) {
                            PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                            boolean extended = section.getBoolean("potion.extended", false);
                            int level = section.getInt("potion.level", 0);
                            item = (new Potion(type, level, true, extended)).toItemStack(1);
                        }
                    } else if (item.equals(XMaterial.POTION.parseMaterial())) {
                        String baseEffect = section.getString("potion");
                        if (!baseEffect.isEmpty()) {
                            PotionType type = Enums.getIfPresent(PotionType.class, section.getString("potion.type", "INSTANT_HEAL").toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                            boolean extended = section.getBoolean("potion.extended", false);
                            int level = section.getInt("potion.level", 0);
                            item = (new Potion(type, level, false, extended)).toItemStack(1);
                        }
                    }
                }
                //Spawners and Shields
            } else if (meta instanceof BlockStateMeta) {
                BlockStateMeta bsm = (BlockStateMeta) meta;
                BlockState state = bsm.getBlockState();

                //Spawners
                if (state instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) state;
                    if (section.contains("mob")) {
                        spawner.setSpawnedType(Enums.getIfPresent(EntityType.class, section.getString("mob").toUpperCase(Locale.ENGLISH)).orNull());
                        spawner.update(true);
                        bsm.setBlockState(spawner);
                    }

                    //Shields
                } else if (state instanceof Banner) {
                    Banner banner = (Banner) state;
                    ConfigurationSection patterns = section.getConfigurationSection("patterns");

                    if (StringUtils.isNotEmpty(section.getString("color"))) {
                        banner.setBaseColor(Enums.getIfPresent(DyeColor.class, section.getString("color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE));
                        if (patterns != null) {
                            for (String pattern : patterns.getKeys(false)) {
                                PatternType type = PatternType.getByIdentifier(pattern + ".type");
                                if (type == null)
                                    type = Enums.getIfPresent(PatternType.class, patterns.getString(pattern + ".type").toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                                DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern + ".color").toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                                banner.addPattern(new Pattern(color, type));
                            }

                            banner.update(true);
                            bsm.setBlockState(banner);
                        }
                    }
                }
                //Firework Stars
            } else if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta firework = (FireworkEffectMeta) meta;
                FireworkEffect.Builder builder = FireworkEffect.builder();
                if (StringUtils.isNotEmpty(section.getString("fireworkColor"))) {
                    builder.withColor(ColorUtils.getFireworkColor(section.getString("fireworkColor")));
                }
                if (StringUtils.isNotEmpty(section.getString("fireworkFadeColor"))) {
                    builder.withFade(ColorUtils.getFireworkColor(section.getString("fireworkFadeColor")));
                }
                try {
                    firework.setEffect(builder.build());
                } catch (IllegalStateException illegalStateException) {
                    illegalStateException.printStackTrace();
                }
                //Fireworks
            } else if (meta instanceof FireworkMeta) {
                FireworkMeta firework = (FireworkMeta) meta;
                ConfigurationSection fireworkSection = section.getConfigurationSection("fireworkEffects");
                if (fireworkSection != null) {
                    for (String fws : fireworkSection.getKeys(false)) {
                        FireworkEffect.Builder builder = FireworkEffect.builder();
                        builder.with(FireworkEffect.Type.valueOf(fireworkSection.getString(fws + ".type").toUpperCase()));

                        if (!fireworkSection.getStringList(fws + ".colors").isEmpty()) {
                            List<String> fwColors = fireworkSection.getStringList(fws + ".colors");
                            List<Color> colors = new ArrayList<>(fwColors.size());
                            for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                            builder.withColor(colors);
                        }

                        if (!fireworkSection.getStringList(fws + ".fadeColors").isEmpty()) {
                            List<String> fwColors = fireworkSection.getStringList(fws + ".fadeColors");
                            List<Color> colors = new ArrayList<>(fwColors.size());
                            for (String colorStr : fwColors) colors.add(NumberUtils.parseColor(colorStr));
                            builder.withFade(colors);
                        }

                        if (fireworkSection.getBoolean(fws + ".flicker")) {
                            builder.withFlicker();
                        }

                        if (fireworkSection.getBoolean(fws + ".trail")) {
                            builder.withTrail();
                        }

                        builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fireworkSection.getString(fws + ".type").toUpperCase(Locale.ENGLISH)).or(FireworkEffect.Type.STAR));
                        firework.addEffect(builder.build());
                    }
                }
                if (section.getInt("fireworkPower") >= 1 && section.getInt("fireworkPower") <= 4) {
                    firework.setPower(section.getInt("fireworkPower"));
                }
            } else if (XMaterial.supports(14)) {

                //Crossbow
                if (meta instanceof CrossbowMeta) {
                    CrossbowMeta crossbow = (CrossbowMeta) meta;
                    if (section.contains("projectiles")) {
                        for (String projectiles : section.getConfigurationSection("projectiles").getKeys(false)) {
                            ItemStack projectile = loadConfigItem(section.getConfigurationSection("projectiles." + projectiles), runningItemAdder);
                            crossbow.addChargedProjectile(projectile);
                        }
                    }

                    //Tropical Fish Bucket
                } else if (meta instanceof TropicalFishBucketMeta) {

                    if (section.contains("bucket")) {
                        TropicalFishBucketMeta tropical = (TropicalFishBucketMeta) meta;
                        DyeColor color = Enums.getIfPresent(DyeColor.class, section.getString("bucket.color")).or(DyeColor.WHITE);
                        DyeColor patternColor = Enums.getIfPresent(DyeColor.class, section.getString("bucket.patternColor")).or(DyeColor.WHITE);
                        TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, section.getString("bucket.pattern")).or(TropicalFish.Pattern.BETTY);

                        tropical.setBodyColor(color);
                        tropical.setPatternColor(patternColor);
                        tropical.setPattern(pattern);
                    }
                }
            } else if (XMaterial.supports(15)) {

                //Suspicious Stew
                if (meta instanceof SuspiciousStewMeta) {
                    if (section.contains("potion")) {
                        SuspiciousStewMeta stew = (SuspiciousStewMeta) meta;
                        String baseEffect = section.getString("potion");
                        if (!baseEffect.isEmpty()) {
                            XPotion.Effect effect = XPotion.parseEffect(section.getString("potion.type", "INSTANT_HEAL"));
                            stew.addCustomEffect(effect.getEffect(), true);
                        }
                    }
                }
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}
