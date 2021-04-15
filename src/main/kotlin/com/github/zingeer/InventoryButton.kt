package com.github.zingeer

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class InventoryButton(
    val itemStack: ItemStack,
    val clickable: Boolean,
    val clickHandler: InventoryClickEvent.() -> Unit
)