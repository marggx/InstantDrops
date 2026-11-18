package dev.marggx.plugin.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Drops NPC loot the tick the NPC dies instead of waiting for the corpse to disappear.
 * Mirrors the vanilla {@code NPCDamageSystems.DropDeathItems} behavior minus the corpse
 * timer gate, then flags the role so the vanilla corpse-timed drop never double-fires.
 */
public class InstantDropSystem extends DeathSystems.OnDeathSystem {

    @Nonnull
    private static final Query<EntityStore> QUERY = NPCEntity.getComponentType();

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (component.getItemsLossMode() != DeathConfig.ItemsLossMode.ALL) return;

        final var npcComponent = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
        if (npcComponent == null) return;

        final var role = npcComponent.getRole();
        if (role == null || role.hasDroppedDeathItems()) return;

        final List<ItemStack> itemsToDrop = new ObjectArrayList<>();

        if (role.isPickupDropOnDeath()) {
            final var storageComponent = commandBuffer.getComponent(ref, InventoryComponent.Storage.getComponentType());
            if (storageComponent != null) {
                itemsToDrop.addAll(storageComponent.getInventory().dropAllItemStacks());
            }
        }

        final String dropListId = role.getDropListId();
        if (dropListId != null) {
            final var itemModule = ItemModule.get();
            if (itemModule.isEnabled()) {
                itemsToDrop.addAll(itemModule.getRandomItemDrops(dropListId));
            }
        }

        // Flag before spawning so the vanilla corpse-timed system can never double-drop.
        role.setDeathItemsDropped();

        if (itemsToDrop.isEmpty()) return;

        final var transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        final var headRotationComponent = commandBuffer.getComponent(ref, HeadRotation.getComponentType());
        if (transformComponent == null || headRotationComponent == null) return;

        final var dropPosition = new Vector3d(transformComponent.getPosition()).add(0, 1, 0);
        final var drops = ItemComponent.generateItemDrops(store, itemsToDrop, dropPosition, new Rotation3f(headRotationComponent.getRotation()));
        commandBuffer.addEntities(drops, AddReason.SPAWN);
    }
}
