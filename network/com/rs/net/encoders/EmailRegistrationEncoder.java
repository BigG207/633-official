package com.rs.net.encoders;

import com.rs.io.OutputStream;
import com.rs.net.Session;
/**
 * 
 * @author Seth Rogen
 * Email Encoder (For Account Creation)
 *
 */
public class EmailRegistrationEncoder extends Encoder {

	public EmailRegistrationEncoder(Session session) {
		super(session);
	}
	/**
	 * Email Status Codes 
	 * 
	 * @Error contacting server - Code: -1, -4 or 3
	 * @No response from server - Code: -5
	 * @The server is currently very busy. Please try again shortly - Code: 7
	 * @You cannot create an account at this time. Please try again later - Code: 38, 9
	 * @Email already in use. Try a different email or click - Code: 20
	 * @Please enter a valid Email address - Code: 21
	 * @RuneScape has been updated. Please reload this page - Code: 37
	 * @Unexpected server response - Default 
	 * @Sign Up Complete - Code: 2
	 */
    public void sendEmailDetailsComplete(int opcode) {
    	OutputStream bldr = new OutputStream();
    	bldr.writeByte(opcode); //do not set this to 2 as default change this for email in use ect
        session.write(bldr);
}
}
