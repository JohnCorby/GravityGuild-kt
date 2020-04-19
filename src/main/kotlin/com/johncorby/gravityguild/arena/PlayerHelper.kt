/**
 * utils for doing cool things with players
 */
package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.BIG_NUMBER
import com.johncorby.gravityguild.Options
import com.johncorby.gravityguild.arena.CooldownTracker.startCooldown
import com.johncorby.gravityguild.arena.CooldownTracker.stopCooldown
import com.johncorby.gravityguild.ifNull
import com.johncorby.gravityguild.orError
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


/**
 * find an [ArenaGame] to join
 */
fun getGame(name: String? = null, id: Int? = null): ArenaGame =
    if (id != null) {
        // get game with name and id
        arenaGames
            .find { it.name == name && it.id == id }
            .orError("game with map $name and id $id doesnt exist")
    } else {
        arenaGames
            .run {
                // filter by name if necessary
                if (name != null) filter { it.name == name }
                    .ifEmpty { error("arena $name doesnt exist") }
                else this
            }.run {
                // find non-full game with the most players
                filter { it.numPlayers != Options.maxPlayers }
                    .shuffled()
                    .maxBy { it.numPlayers }
                    .ifNull {
                        // or create a new one if theyre all full
                        if (name != null) ArenaGame(name)
                        else ArenaGame()
                    }
            }
    }


fun Player.initForArena() {
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

    // todo move to after countdown when i do that
    // stops any possibly existing cooldown thats were already going
    stopCooldown()
    startCooldown()
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
var Player.isSpectating
    get() = gameMode == GameMode.SPECTATOR
    set(value) {
        gameMode = if (value) GameMode.SPECTATOR else GameMode.SURVIVAL
    }
