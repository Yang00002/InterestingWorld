package org.yang.interestingworld.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.yang.interestingworld.IWUtil;

import java.util.List;


public class IWNetwork
{

    private static final Identifier ItemBreakParticlePayload_ID = Identifier.of(IWUtil.Base.MOD_ID, "cooldown_sound");

    public record ItemBreakParticlePayload(ParticleEffect effect) implements CustomPayload
    {
        public static final Id<ItemBreakParticlePayload> ID = new CustomPayload.Id<>(ItemBreakParticlePayload_ID);
        public static final PacketCodec<RegistryByteBuf, ItemBreakParticlePayload> CODEC = PacketCodec.of(
                (ItemBreakParticlePayload payload, RegistryByteBuf buf) -> ParticleTypes.PACKET_CODEC.encode(buf,
                        payload.effect),
                (RegistryByteBuf buf) -> new ItemBreakParticlePayload(ParticleTypes.PACKET_CODEC.decode(buf)));

        @Override
        public Id<? extends CustomPayload> getId()
        {
            return ID;
        }
    }

}
