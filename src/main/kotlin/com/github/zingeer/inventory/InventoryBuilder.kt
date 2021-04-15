package com.github.zingeer.inventory

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

open class InventoryBuilder
private constructor(
    val plugin: Plugin,
    inventoryType: InventoryType,
    rows: Int,
    title: String,
    private val builder: InventoryBuilder.() -> Unit = {}
) : InventoryHolder, Listener {
    constructor(
        plugin: Plugin,
        inventoryType: InventoryType,
        title: String,
        builder: InventoryBuilder.() -> Unit = {}
    ) : this(plugin, inventoryType, 3, title, builder)

    constructor(
        plugin: Plugin,
        rows: Int,
        title: String,
        builder: InventoryBuilder.() -> Unit = {}
    ) : this(plugin, InventoryType.CHEST, rows, title, builder)

    private val _inventory = if (inventoryType == InventoryType.CHEST)
        Bukkit.createInventory(this, rows * 9, Component.translatable(title)) else
        Bukkit.createInventory(this, inventoryType, Component.translatable(title))

    override fun getInventory(): Inventory = _inventory

    private val buttons = HashMap<Int, InventoryButton>()

    private val clickHandlers = ArrayList<InventoryClickEvent.()->Unit>().apply {
        add { onClick(this) }
    }
    private val closeHandlers = ArrayList<InventoryCloseEvent.()->Unit>().apply {
        add { onClose(this) }
    }
    private val dragHandlers = ArrayList<InventoryDragEvent.()->Unit>().apply {
        add { onDrag(this) }
    }

    private val interactHandlers = ArrayList<InventoryClickEvent.()->Unit>().apply {
        add { onInteract( this) }
    }

    open fun onClose(event: InventoryCloseEvent) {}

    fun onClose(handler: InventoryCloseEvent.()->Unit) = closeHandlers.add(handler)

    open fun onClick(event: InventoryClickEvent) {}

    fun onClick(handler: InventoryClickEvent.()->Unit) = clickHandlers.add(handler)

    open fun onDrag(event: InventoryDragEvent) {}

    fun onDrag(handler: InventoryDragEvent.()->Unit) = dragHandlers.add(handler)

    open fun onInteract(event: InventoryClickEvent) {}

    fun onInteract(handler: InventoryClickEvent.()->Unit) = interactHandlers.add(handler)

    /**
     * |----------------------------|
     * | 0  1  2  3  4  5  6  7  8  |
     * | 9  10 11 12 13 14 15 16 17 |
     * | 18 19 20 21 22 23 24 25 26 |
     * | 27 28 29 30 31 32 33 34 35 |
     * | 36 37 38 39 40 41 42 43 44 |
     * | 45 46 47 48 49 50 51 52 53 |
     * |----------------------------|
     * | 54 55 56 57 58 59 60 61 62 |
     * | 63 64 65 66 67 68 69 70 71 |
     * | 72 73 74 75 76 77 78 79 80 |
     * |----------------------------|
     * | 81 82 83 84 85 86 87 88 89 |
     * |----------------------------|
     */

    fun addButton(slot: Int, button: InventoryButton): InventoryBuilder {
        inventory.setItem(slot, button.itemStack)
        buttons[slot] = button
        return this
    }

    fun addButton(button: InventoryButton): InventoryBuilder =
        addButton(firstEmpty(), button)

    fun addButton(slot: Int, itemStack: ItemStack, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder = apply {
        val button = InventoryButton(itemStack, clickable, clickHandler)
        addButton(slot, button)
    }

    fun addButton(itemStack: ItemStack, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder =
        addButton(firstEmpty(), itemStack, clickable, clickHandler)

    fun addButton(slot: Int, material: Material, text: List<String>, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder {
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        itemMeta.addItemFlags(*ItemFlag.values())
        if (text.isNotEmpty()) itemMeta.displayName(
            Component.translatable(
                ChatColor.RESET.toString() + ChatColor.translateAlternateColorCodes(
                    '&',
                    text[0]
                )
            )
        )
        if (text.size > 1) itemMeta.lore = text.subList(1, text.size)
            .map { ChatColor.RESET.toString() + ChatColor.translateAlternateColorCodes('&', it) }
        itemStack.itemMeta = itemMeta

        return addButton(slot, itemStack, clickable, clickHandler)
    }

    fun addButton(material: Material, text: List<String>, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder {
        return addButton(firstEmpty(), material, text, clickable, clickHandler)
    }

    fun addButton(material: Material, text: String, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder =
        addButton(material, text.split("\n"), clickable, clickHandler)

    fun addButton(material: Material, vararg text: String, clickable: Boolean, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryBuilder =
        addButton(material, text.toList(), clickable, clickHandler)

    @JvmOverloads
    fun addButton(slot: Int, itemStack: ItemStack, clickable: Boolean, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryBuilder =
        addButton(slot, itemStack, clickable) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(itemStack: ItemStack, clickable: Boolean, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryBuilder =
        addButton(itemStack, clickable) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(material: Material, text: List<String>, clickable: Boolean, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryBuilder =
        addButton(material, text, clickable) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(material: Material, text: String, clickable: Boolean, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryBuilder =
        addButton(material, text.split("\n"), clickable) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(material: Material, vararg text: String, clickable: Boolean, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryBuilder =
        addButton(material, text.toList(), clickable) { clickHandler.accept(this) }

    @JvmOverloads
    fun open(player: Player, clear: Boolean = false): InventoryBuilder {
        if (clear) {
            clear()
        }
        builder(this)
        player.openInventory(inventory)

        HandlerList.unregisterAll(this)
        plugin.server.pluginManager.registerEvents(this, plugin)

        return this
    }

    fun clear() {
        inventory.clear()
        buttons.clear()
    }

    @EventHandler
    fun clickEventHandler(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return

        if (clickedInventory.holder == this) {
            buttons[event.slot]?.also { button ->
                if (!button.clickable) {
                    event.isCancelled = true
                }
                button.clickHandler(event)
            }
            clickHandlers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            interactHandlers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (event.view.topInventory.holder == this) {
            clickHandlers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            interactHandlers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @EventHandler
    fun dragEventHandler(event: InventoryDragEvent) {
        event.rawSlots.forEach { slot ->
            val clickedInventory = event.view.getInventory(slot) ?: return
            if (clickedInventory.holder != this) return

            val clickEvent = InventoryClickEvent(
                event.view,
                InventoryType.SlotType.CONTAINER,
                slot,
                ClickType.UNKNOWN,
                InventoryAction.UNKNOWN
            )

            dragHandlers.forEach {
                try {
                    it(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            interactHandlers.forEach {
                try {
                    it(clickEvent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            event.isCancelled = clickEvent.isCancelled
        }
    }

    @EventHandler
    fun closeEventHandler(event: InventoryCloseEvent) {
        if (event.inventory.holder != this) return

        inventory.viewers.removeIf { player -> player == event.player }

        closeHandlers.forEach {
            try {
                it(event)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (inventory.viewers.isNullOrEmpty()) HandlerList.unregisterAll(this)
    }

    private fun firstEmpty(): Int {
        for (slot in 0..inventory.size) {
            if (buttons[slot] == null) {
                return slot
            }
        }
        return -1
    }
}