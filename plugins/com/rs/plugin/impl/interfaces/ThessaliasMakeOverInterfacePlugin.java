package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = 729)
public class ThessaliasMakeOverInterfacePlugin implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2)
			throws Exception {
	}
}