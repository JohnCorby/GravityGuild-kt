/**
 * utils for doing cool things with players
 */
package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.BIG_NUMBER
import com.johncorby.coreapi.info
import com.johncorby.gravityguild.Config
import com.johncorby.gravityguild.arena.CooldownTracker.startCooldown
import com.johncorby.gravityguild.arena.CooldownTracker.stopCooldown
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * make the player respawn in the [ArenaGame]
 */
fun Player.respawn() {
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

    // stops any possibly existing cooldown thats were already going
    stopCooldown()
    startCooldown()
}

var Player.lives
    get() = level
    set(value) {
        require(value >= 0) { "lives cannot be negative" }
        // death
        if (lives == 0) {
            isSpectating = true
            info("you are now spectating. leave at any time with /gg arena leave or /gg lobby")
        }
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
