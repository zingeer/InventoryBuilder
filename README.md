#InventoryBuilder
A simple custom inventory builder for Kotlin.

Gradle Kotlin DSL:
```kotlin
repositories {
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.github.zingeer", "inventory", "1.0.1")
}
```

###Create Inventory Example

First example:
```kotlin
val builder = InventoryBuilder(plugin, 6, "Example") {
    addButton(1, Material.STONE, true) {
        player.sendMessage("You clicked at stone block")
    }
    onInteract {
        player.sendMessage("You interact with inventory")
    }
}

builder.open(player)
```

Second example:
```kotlin
val builder = object : InventoryBuilder(plugin, 3, "Example") {
    override fun onClose(event: InventoryCloseEvent) {
        event.player.sendActionBar(Component.text("You closed your inventory"))
    }
    override fun onDrag(event: InventoryDragEvent) {
        event.player.closeInventory()
    }
}

builder.addButton(1, Material.OAK_PLANKS, false) {
    player.allowFlight = true
    player.isFlying = true
    player.sendMessage("You were given a flight")
}
        
builder.open(player)
```