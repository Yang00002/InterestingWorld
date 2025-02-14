package org.yang.iw.entity.player;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.yang.iw.network.payload.C2SAbilityKeyPressPayload;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;

public class IWClientPlayerData
{
	public AbstractRuneAbility WeaponAbility = IWRuneAbilities.DEFAULT_ABILITY;
	public int shown_energy = 0;
	public int charge_rate16 = 0;
	public int shown_number = 0;
	public boolean is_charged = false;
	public boolean client_ability_on = true;

	/**
	 * 默认情况下能力栏与 client 能力开关相关, 这是为了延时考虑
	 * <p>
	 * 然而对于连斩这种开关能力会改变其能力栏进度状态的能力, 与 client 相关会导致一瞬间的能力栏闪烁. 具体的说, 当连斩第一击打出时关闭能力, 首先是进度条变灰, 然后才是进度条清 0
	 * <p>
	 * 为了修复这种问题, 引入了可选的 server 相关能力支持. client 发送能力开关状态, server 回应, 而后 client 使用 server 状态渲染
	 * <p>
	 * server 相关不是默认的. 即如果不维护, server_ability_on_opt 不能代表 server 的状态. 一般该状态使用能力数据包同步. 这需要实现:
	 * <p>
	 * - 在 writeClientRenderDataToBuf 时写入 isAbilityOn(), readClientRenderDataFromBuf 接收时将其赋值给 server_ability_on_opt
	 * <p>
	 * - 所有的 client_ability_on 改成 server_ability_on_opt. 由于 RuneAbility 默认在 shouldRenderAbilityBar 使用 client, 要重写这个函数
	 * <p>
	 * - onServerAbilityOpen 和 onServerAbilityClose 中发起主动同步, shouldSync 设置为 true
	 * <p>
	 * 可以说明这么做, 在能力切换时也不会遇到奇怪的问题. 从 server 切换到 client 自然没问题. 从 client 切换到 server 时, 第一帧的显示使用的数据包已经有 server 的信息, 不会发生错乱
	 * . server 唯一的问题在较慢, 所以它也不是默认的.
	 */
	public boolean server_ability_on_opt = true;

	public void handleS2CInitializeDataBuffer(ByteBuf buf)
	{
		client_ability_on = buf.readBoolean();
	}

	public void switchAbilityOpen()
	{
		client_ability_on = !client_ability_on;
		ClientPlayNetworking.send(new C2SAbilityKeyPressPayload(client_ability_on));
	}
}
