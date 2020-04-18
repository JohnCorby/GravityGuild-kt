/**
 * utils for doing cool things with players
 */
package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.BIG_NUMBER
import com.johncorby.gravityguild.Options
import com.johncorby.gravityguild.orNullError
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


fun Player.initForArena() {
    // heal
    health = getAttribute(Attribute.GENERIC_MAX_HEALTH).orNullError("attribute GENERIC_MAX_HEALTH").value
    foodLevel = 20
    fireTicks = 0
    activePotionEffects.forEach { removePotionEffect(it.type) }

    // init inventory
    inventory.apply {
        clear()
        addItem(
            ItemStack(Material.BOW).apply {
                addUnsafeEnchantment(Enchantment.DURABILITY, BIG_NUMBER)
                addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1)
            },
            ItemStack(Material.ARROW)
        )
        helmet = ItemStack(Material.END_ROD).apply {
            addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1)
        }
        chestplate = ItemStack(Material.ELYTRA).apply {
            addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1)
            addUnsafeEnchantment(Enchantment.DURABILITY, BIG_NUMBER)
        }
    }

//    isInvincible = true
}

var Player.lives
    get() = level
    set(value) {
        require(value >= 0) { "lives cannot be negative" }
        level = value
        exp = value / Options.lives.toFloat()
    }
var Player.isInvincible
    get() = isInvulnerable && isGlowing
    set(value) {
        isInvulnerable = value
        isGlowing = value
    }
var Player.isSpectator
    get() = gameMode == GameMode.SPECTATOR
    set(value) {
        gameMode = if (value) GameMode.SPECTATOR else GameMode.SURVIVAL
    }
