/**
 * utils for doing cool things with players
 */
package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.BIG_NUMBER
import com.johncorby.gravityguild.Config
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * initializes the player's stats and spawns them in a random location.
 * doesnt include lives handling.
 * doesnt start cooldown
 */
fun Player.initAndSpawn() {
    // todo teleport to random part on the map

    // heal
    health = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
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
                addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1)
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

}


var Player.lives
    get() = level
    set(value) {
        val value = value.coerceAtLeast(0)
        level = value
        exp = value / Config.LIVES.toFloat()
    }
inline var Player.isInvincible
    get() = isInvulnerable && isGlowing
    set(value) {
        isInvulnerable = value
        isGlowing = value
    }
inline var Player.isSpectating
    get() = gameMode == GameMode.SPECTATOR
    set(value) {
        gameMode = if (value) GameMode.SPECTATOR else GameMode.SURVIVAL
    }
