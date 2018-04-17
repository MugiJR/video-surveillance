package com.uniq.dao;

import java.io.IOException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class MessageSender {

	/**
	 * @param
	 */
	void gcmAlert(String filename)

	{
		// TODO Auto-generated method stub
		System.out.println("File Name: " + filename);
		Sender sender = new Sender("AIzaSyB36xhJBNHfDKwoq8_qAVkJ8Wg9uLoBE2c");

		Message message = new Message.Builder().collapseKey("1").timeToLive(3)
				.delayWhileIdle(true).addData("filename", filename).build();

		try {
			Result result = sender
					.send(message,
							"APA91bEgWB41ZODDFT3qcVQH2CySt4nhTMEidvDMlQuM5F3VZWun3ESgKpgeeeB6hQ6HOtRW_baBJhGhpmDIRQys6qxuEt3t7sv0pJiMWDO72QrAwo8Hga2eq2TOxaCZTZPlBVXCBQr8B66pjP-UDmdpawhEWeLk4Q",
							3);
			System.out.println(result.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
