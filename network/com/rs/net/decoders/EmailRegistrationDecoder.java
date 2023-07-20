package com.rs.net.decoders;


import com.rs.GameConstants;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.EmailList;
import com.rs.net.Session;
import com.rs.utilities.Utility;

public final class EmailRegistrationDecoder extends Decoder {

	public EmailRegistrationDecoder(Session session) {
		super(session);
	}

	@Override
	public void decode(InputStream stream) {
		session.setDecoder(-1);
        int packetSize = stream.readUnsignedShort();
        if (packetSize != stream.getRemaining()) {
            session.getChannel().close();
            return;
        }
        
        if (stream.readUnsignedShort() != GameConstants.CLIENT_REVISION) {
            session.getLoginPackets().sendClientPacket(6);
            return;
        }
        int rsaBlockSize = stream.readUnsignedShort();
        if (rsaBlockSize > stream.getRemaining()) {
            session.getLoginPackets().sendClientPacket(10);
            return;
        }
        byte[] data = new byte[rsaBlockSize];
        stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(
				Utility.cryptRSA(data, GameConstants.PRIVATE_EXPONENT, GameConstants.MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
        int[] isaacKeys = new int[4];
        for (int i = 0; i < isaacKeys.length; i++)
            isaacKeys[i] = rsaStream.readInt();
        stream.decodeXTEA(isaacKeys, stream.getOffset(), stream.getLength());
        String email = stream.readString();
        int lang = stream.readUnsignedByte();
        
		if (EmailList.emailAlreadyUsed(email.toLowerCase())) {
			session.getEmailComplete().sendEmailDetailsComplete(20);
			return;
		}
		
		session.getEmailComplete().sendEmailDetailsComplete(2);     
	}
}
