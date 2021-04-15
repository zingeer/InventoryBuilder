package com.github.zingeer

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.Plugin

object InventoryBuilderManager {

    val InventoryInteractEvent.player: Player
        get() = whoClicked as Player

    @JvmOverloads
    @JvmName("PluginInventoryBuilder")
    fun Plugin.InventoryBuilder(inventoryType: InventoryType, title: String, builder: InventoryBuilder.() -> Unit = {}): InventoryBuilder =
        InventoryBuilder(this, inventoryType, title, builder)

    @JvmOverloads
    @JvmName("PluginInventoryBuilder")
    fun Plugin.InventoryBuilder(rows: Int, title: String, builder: InventoryBuilder.() -> Unit = {}): InventoryBuilder =
        InventoryBuilder(this, rows, title, builder)

    @JvmOverloads
    fun InventoryBuilder.InventoryBuilder(inventoryType: InventoryType, title: String, builder: InventoryBuilder.() -> Unit = {}): InventoryBuilder =
        InventoryBuilder(plugin, inventoryType, title, builder)

    @JvmOverloads
    fun InventoryBuilder.InventoryBuilder(rows: Int, title: String, builder: InventoryBuilder.() -> Unit = {}): InventoryBuilder =
        InventoryBuilder(plugin, rows, title, builder)
}