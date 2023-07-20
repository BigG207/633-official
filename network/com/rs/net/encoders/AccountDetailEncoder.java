package com.rs.net.encoders;

import com.rs.io.OutputStream;
import com.rs.net.Session;

public class AccountDetailEncoder extends Encoder {

	public AccountDetailEncoder(Session session) {
		super(session);
	}
	
	/**
	 * Email Status Codes 
	 * 
	 * Code: 6 - Unexpected Server Respoonse.
	 * Code: 14 - Unexpected Server Respoonse.
	 * Code: 7 - The Server is very busy, Please try again shortly
	 * Code: 8 - Unexpected Server Respoonse
	 * Code: 15 - you are not eligible to create account (for Ip banned player?)
	 * Code: 2 - Account Succesfully Created.
	 */
    public void sendAccountDetailsComplete(int opcode) {
    	OutputStream bldr = new OutputStream();
    	bldr.writeByte(opcode); //do not set this to 2 as default change this for email in use ect
        session.write(bldr);
        session.creatingAccounts.add(session.getAccountCreateIP());
    }
}
