package me.blueishberry.bluesquickequip;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class BluesQuickEquipClient implements ClientModInitializer {

	private static KeyBinding helmetKey;
	private static KeyBinding chestplateKey;
	private static KeyBinding leggingsKey;
	private static KeyBinding bootsKey;

	@Override
	public void onInitializeClient() {
		// Register keybinds
		helmetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.bluesquickequip.helmet",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_1,
				"category.bluesquickequip"
		));

		chestplateKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.bluesquickequip.chestplate",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_2,
				"category.bluesquickequip"
		));

		leggingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.bluesquickequip.leggings",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_3,
				"category.bluesquickequip"
		));

		bootsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.bluesquickequip.boots",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_4,
				"category.bluesquickequip"
		));

		// Add tick event listener
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.player == null || client.interactionManager == null) return;

			if(helmetKey.wasPressed()) {
				toggleArmor(client, EquipmentSlot.HEAD);
			}

			if(chestplateKey.wasPressed()) {
				toggleArmor(client, EquipmentSlot.CHEST);
			}

			if(leggingsKey.wasPressed()) {
				toggleArmor(client, EquipmentSlot.LEGS);
			}

			if(bootsKey.wasPressed()) {
				toggleArmor(client, EquipmentSlot.FEET);
			}
		});
	}

	private void toggleArmor(net.minecraft.client.MinecraftClient client, EquipmentSlot slot) {
		assert client.player != null;
		ItemStack equippedStack = client.player.getEquippedStack(slot);


		if(!equippedStack.isEmpty()) {
			if(isInventoryFull(client)) {
				client.player.sendMessage(Text.literal("Your inventory is full!").formatted(Formatting.RED), true);
				return;
			}
			// Remove armor and add it to the first available slot in the inventory
			for(int i = 9; i <= 44; i++) {
				if (client.player.getInventory().getStack(i).isEmpty()) {
					// Pick up equipped armor and place it in the first available slot
					client.interactionManager.clickSlot(0, getEquipmentSlotForArmor(slot), 0, SlotActionType.PICKUP, client.player);
					client.interactionManager.clickSlot(0, i, 0, SlotActionType.PICKUP, client.player);
					return;
				}
			}
		} else {
			// Equip the first matching armor item from the inventory
			for(int i = 9; i <= 44; i++) {
				ItemStack stack = client.player.getInventory().getStack(i);

				if(isArmorForSlot(stack, slot)) {
					// Pick up the matching armor item and place it in the armor slot
					client.interactionManager.clickSlot(0, i, 0, SlotActionType.PICKUP, client.player);
					client.interactionManager.clickSlot(0, getEquipmentSlotForArmor(slot), 0, SlotActionType.PICKUP, client.player);
					return;
				}
			}
		}
	}

	private boolean isInventoryFull(net.minecraft.client.MinecraftClient client) {
		assert client.player != null;
		for(int i = 9; i <= 35; i++) {
			if (client.player.getInventory().getStack(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean isArmorForSlot(ItemStack stack, EquipmentSlot slot) {
		return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlotType() == slot;
	}

	private int getEquipmentSlotForArmor(EquipmentSlot slot) {
		// Return the correct equipment slot
		return switch(slot) {
			case HEAD -> 5; // Helmet slot
			case CHEST -> 6; // Chestplate slot
			case LEGS -> 7; // Leggings slot
			case FEET -> 8; // Boots slot
			default -> -1; // Invalid slot
		};
	}
}
