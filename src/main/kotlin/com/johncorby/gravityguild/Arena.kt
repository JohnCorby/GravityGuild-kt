package com.johncorby.gravityguild

import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.mineacademy.gameapi.*
import org.mineacademy.gameapi.Arena
import org.mineacademy.gameapi.cause.DeathCause
import org.mineacademy.gameapi.cause.JoinCause
import org.mineacademy.gameapi.cause.LeaveCause
import org.mineacademy.gameapi.cause.StopCause
import org.mineacademy.gameapi.type.ArenaState

class Arena : Arena {
    override fun kickPlayer(player: Player?, cause: LeaveCause?): Boolean {
        TODO()
    }

    override fun getSetup(): Setup {
        TODO()
    }

    override fun onEntityTarget(event: EntityTargetEvent?) {
        TODO()
    }

    override fun getPlugin(): ArenaPlugin {
        TODO()
    }

    override fun getSettings(): ArenaSettings {
        TODO()
    }

    override fun onPostLoad() {
        TODO()
    }

    override fun onPlayerBlockPlace(event: BlockPlaceEvent?) {
        TODO()
    }

    override fun stopArena(cause: StopCause?) {
        TODO()
    }

    override fun getName(): String {
        TODO()
    }

    override fun getPhase(): ArenaPhase {
        TODO()
    }

    override fun onPlayerDeath(player: Player?, killer: Player?) {
        TODO()
    }

    override fun onPlayerDeath(player: Player?, cause: DeathCause?) {
        TODO()
    }

    override fun getSnapshot(): ArenaSnapshot {
        TODO()
    }

    override fun onPlayerClick(player: Player?, clickedBlock: Block?, hand: ItemStack?) {
        TODO()
    }

    override fun isStopping(): Boolean {
        TODO()
    }

    override fun onPlayerPickupTag(event: PlayerPickupItemEvent?, expItem: ExpItem?) {
        TODO()
    }

    override fun setEnabled(enabled: Boolean) {
        TODO()
    }

    override fun onPlayerClickAir(player: Player?, hand: ItemStack?) {
        TODO()
    }

    override fun onProjectileLaunch(event: ProjectileLaunchEvent?) {
        TODO()
    }

    override fun getPlayers(): MutableCollection<Player> {
        TODO()
    }

    override fun onEntityDeath(event: EntityDeathEvent?) {
        TODO()
    }

    override fun teleportPlayerBack(player: Player?) {
        TODO()
    }

    override fun onPlayerRespawn(event: PlayerRespawnEvent?) {
        TODO()
    }

    override fun onProjectileHit(event: ProjectileHitEvent?) {
        TODO()
    }

    override fun joinPlayer(player: Player?, cause: JoinCause?): Boolean {
        TODO()
    }

    override fun getState(): ArenaState {
        TODO()
    }

    override fun onPlayerBlockDamage(event: EntityDamageByBlockEvent?, player: Player?, damage: Double) {
        TODO()
    }

    override fun onPlayerDamage(event: EntityDamageByEntityEvent?, player: Player?, source: Entity?, damage: Double) {
        TODO()
    }

    override fun onPlayerPvE(damager: Player?, victim: LivingEntity?, damage: Double) {
        TODO()
    }

    override fun isJoined(player: Player?): Boolean {
        TODO()
    }

    override fun isJoined(playerName: String?): Boolean {
        TODO()
    }

    override fun startLobby() {
        TODO()
    }

    override fun getRemainingSeconds(): Int {
        TODO()
    }

    override fun getData(): ArenaData {
        TODO()
    }

    override fun onEntitySpawn(event: EntitySpawnEvent?) {
        TODO()
    }

    override fun getAliveMonsters(): Int {
        TODO()
    }

    override fun startArena(): Boolean {
        TODO()
    }

    override fun isEnabled(): Boolean {
        TODO()
    }

    override fun onPlayerPvP(event: EntityDamageByEntityEvent?, damager: Player?, victim: Player?, damage: Double) {
        TODO()
    }

    override fun getMessenger(): ArenaMessenger {
        TODO()
    }

    override fun onPlayerBlockBreak(event: BlockBreakEvent?) {
        TODO()
    }

    override fun onSnapshotUpdate(newState: ArenaSnapshotStage?) {
        TODO()
    }

    override fun setRestoreSnapshots(restoreSnapshots: Boolean) {
        TODO()
    }
}
