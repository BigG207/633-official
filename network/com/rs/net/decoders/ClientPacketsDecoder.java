package com.rs.net.decoders;

import com.rs.GameConstants;
import com.rs.io.InputStream;
import com.rs.net.Session;
import com.rs.utilities.LogUtility;
import com.rs.utilities.LogUtility.LogType;

public final class ClientPacketsDecoder extends Decoder {

	public ClientPacketsDecoder(Session connection) {
		super(connection);
	}

	@Override
	public final void decode(InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		switch (packetId) {
		case 14:
			decodeLogin(stream);
			break;
		case 15:
			decodeGrab(stream);
			break;
			//Account Creation Stuff 
		case 28:
			EmailRegistration(stream);
			break;
		case 22:
			DetailsRegistration(stream);
			break;
		default:
			if (GameConstants.DEBUG)
				LogUtility.log(LogType.TRACE, "PacketId " + packetId);
			session.getChannel().close();
			break;
		}
	}

	private final void decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
			session.getChannel().close();
			return;
		}
		session.setDecoder(2);
		session.setEncoder(1);
		session.getLoginPackets().sendStartUpPacket();
	}
	
	private final void EmailRegistration(InputStream stream) {
		session.setDecoder(4); //register decode
		session.setEncoder(3); //register encode
		session.getDecoder().decode(stream);
	}
	
	private final void DetailsRegistration(InputStream stream) {
		session.setDecoder(5); //register decode
		session.setEncoder(4); //register encode
		session.getDecoder().decode(stream);
	}
	
	private final void decodeGrab(InputStream stream) {
		if (stream.getRemaining() != 4) {
			System.out.println("Stream: " + stream.getRemaining());
			session.getChannel().close();
			return;
		}
		session.setEncoder(0);
		if (stream.readInt() != GameConstants.CLIENT_REVISION /*
													 * || stream.readInt() !=
													 * Settings
													 * .CUSTOM_CLIENT_BUILD
													 */) {
			session.setDecoder(-1);
			session.getGrabPackets().sendOutdatedClientPacket();
			return;
		}
		
		session.setDecoder(1);
		session.getGrabPackets().sendStartUpPacket();
	}
}
