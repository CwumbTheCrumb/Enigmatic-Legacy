package com.integral.enigmaticlegacy.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.integral.enigmaticlegacy.EnigmaticLegacy;
import com.integral.enigmaticlegacy.client.fx.PermanentItemPickupParticle;
import com.integral.enigmaticlegacy.client.renderers.PermanentItemRenderer;
import com.integral.enigmaticlegacy.client.renderers.UltimateWitherSkullRenderer;
import com.integral.enigmaticlegacy.entities.EnigmaticPotionEntity;
import com.integral.enigmaticlegacy.entities.PermanentItemEntity;
import com.integral.enigmaticlegacy.entities.UltimateWitherSkullEntity;
import com.integral.enigmaticlegacy.objects.RevelationTomeToast;
import com.integral.enigmaticlegacy.objects.TransientPlayerData;
import com.integral.etherium.client.ShieldAuraLayer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientProxy extends CommonProxy {
	private static final Random random = new Random();
	protected final Map<PlayerEntity, TransientPlayerData> clientTransientPlayerData;

	public ClientProxy() {
		super();
		this.clientTransientPlayerData = new WeakHashMap<>();
	}

	@Override
	public void clearTransientData() {
		super.clearTransientData();
		this.clientTransientPlayerData.clear();
	}

	@Override
	public Map<PlayerEntity, TransientPlayerData> getTransientPlayerData(boolean clientOnly) {
		if (clientOnly)
			return this.clientTransientPlayerData;
		else
			return this.commonTransientPlayerData;
	}

	@Override
	public void handleItemPickup(int pickuper_id, int item_id) {
		try {
			Entity pickuper = Minecraft.getInstance().level.getEntity(pickuper_id);
			Entity entity = Minecraft.getInstance().level.getEntity(item_id);

			// TODO Verify fix... someday

			Minecraft.getInstance().particleEngine.add(new PermanentItemPickupParticle(Minecraft.getInstance().getEntityRenderDispatcher(), Minecraft.getInstance().renderBuffers(), Minecraft.getInstance().level, pickuper, entity));
			Minecraft.getInstance().level.playLocalSound(pickuper.getX(), pickuper.getY(), pickuper.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (ClientProxy.random.nextFloat() - ClientProxy.random.nextFloat()) * 1.4F + 2.0F, false);
		} catch (Throwable ex) {
			Exception log = new Exception("Unknown error when rendering permanent item pickup", ex);
			EnigmaticLegacy.logger.catching(log);
		}
	}

	@Override
	public void initAuxiliaryRender() {
		Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();

		PlayerRenderer renderSteve;
		PlayerRenderer renderAlex;

		renderSteve = skinMap.get("default");
		renderAlex = skinMap.get("slim");

		renderSteve.addLayer(new ShieldAuraLayer(renderSteve));
		renderAlex.addLayer(new ShieldAuraLayer(renderAlex));

		//render.addLayer(new ShieldAuraLayer(render, false));
	}

	@Override
	public void initEntityRendering() {
		RenderingRegistry.registerEntityRenderingHandler(PermanentItemEntity.TYPE, renderManager -> new PermanentItemRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(EnigmaticPotionEntity.TYPE, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(UltimateWitherSkullEntity.TYPE, UltimateWitherSkullRenderer::new);
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event) {
		this.initAuxiliaryRender();
	}

	@Override
	public boolean isInVanillaDimension(PlayerEntity player) {
		return player.level.dimension().equals(this.getOverworldKey()) || player.level.dimension().equals(this.getNetherKey()) || player.level.dimension().equals(this.getEndKey());
	}

	@Override
	public boolean isInDimension(PlayerEntity player, RegistryKey<World> world) {
		return player.level.dimension().equals(world);
	}

	@Override
	public World getCentralWorld() {
		return Minecraft.getInstance().level;
	}

	@Override
	public UseAction getVisualBlockAction() {
		return UseAction.BLOCK;
	}

	@Override
	public PlayerEntity getPlayer(UUID playerID) {
		if (Minecraft.getInstance().level != null)
			return Minecraft.getInstance().level.getPlayerByUUID(playerID);
		else
			return null;
	}

	@Override
	public void pushRevelationToast(ItemStack renderedStack, int xp, int knowledge) {
		ToastGui gui = Minecraft.getInstance().getToasts();
		gui.addToast(new RevelationTomeToast(renderedStack, xp, knowledge));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void spawnBonemealParticles(World world, BlockPos pos, int data) {
		if (data == 0) {
			data = 15;
		}

		BlockState blockstate = world.getBlockState(pos);
		if (!blockstate.isAir(world, pos)) {
			double d0 = 0.5D;
			double d1;
			if (blockstate.is(Blocks.WATER)) {
				data *= 3;
				d1 = 1.0D;
				d0 = 3.0D;
			} else if (blockstate.isSolidRender(world, pos)) {
				pos = pos.above();
				data *= 3;
				d0 = 3.0D;
				d1 = 1.0D;
			} else {
				d1 = blockstate.getShape(world, pos).max(Direction.Axis.Y);
			}

			world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

			for(int i = 0; i < data; ++i) {
				double d2 = random.nextGaussian() * 0.02D;
				double d3 = random.nextGaussian() * 0.02D;
				double d4 = random.nextGaussian() * 0.02D;
				double d5 = 0.5D - d0;
				double d6 = pos.getX() + d5 + random.nextDouble() * d0 * 2.0D;
				double d7 = pos.getY() + random.nextDouble() * d1;
				double d8 = pos.getZ() + d5 + random.nextDouble() * d0 * 2.0D;

				world.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
			}

		}

	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

}
