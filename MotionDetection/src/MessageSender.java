import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.uniq.connection.ConnectionManager;

public class MessageSender {

	/**
	 * @param
	 */
	Connection conn = ConnectionManager.getConnection();

	public void sendPostAlert(String filename) {
		// TODO Auto-generated method stub
		ArrayList<String> list_gcm_id = new ArrayList<String>();
		try {
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT * FROM usertable");
			while (rs.next()) {

				list_gcm_id.add(rs.getString("gcm_id"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println(list_gcm_id+"\n");
		
		
		if (list_gcm_id.size() == 1) {
			Sender sender = new Sender(
					"AIzaSyD4NbJbVTwW0jqDj5UWak_ZMIdGQe6n15s");
			
			//AIzaSyDb8elODgzbCGYavizhl-zGeiz5G29ZceM
			
			
			
			
			//AIzaSyB36xhJBNHfDKwoq8_qAVkJ8Wg9uLoBE2c
		   // AIzaSyDRaUVMIc4H16D9om3No-EnrWSormp2PV0

			Message message = new Message.Builder().collapseKey("1")
					.timeToLive(3).delayWhileIdle(true)
					.addData("filename", filename).build();

			try {
				Result result = sender.send(message, list_gcm_id.get(0), 3);
				
				
				System.out.println(result.toString());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println(result.toString());
			}
		} else if (list_gcm_id.size() != 0 && list_gcm_id.size() > 1) {
			Sender sender = new Sender(
					"AIzaSyD4NbJbVTwW0jqDj5UWak_ZMIdGQe6n15s");

			Message message = new Message.Builder().collapseKey("1")
					.timeToLive(3).delayWhileIdle(true)
					.addData("filename", filename).build();
			try {
				MulticastResult multicastResult = sender.sendNoRetry(message,
						list_gcm_id);
				System.out.println(multicastResult.toString());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * void gcmAlert(String filename) { // TODO Auto-generated method stub
	 * System.out.println("File Name: " + filename); Sender sender = new
	 * Sender("AIzaSyB36xhJBNHfDKwoq8_qAVkJ8Wg9uLoBE2c");
	 * 
	 * Message message = new Message.Builder().collapseKey("1").timeToLive(3)
	 * .delayWhileIdle(true).addData("filename", filename).build();
	 * 
	 * try { Result result = sender .send(message,
	 * "APA91bG1QUb2tKvlQPH9v09ZUe5Ady7GXntRJxZt-2nvcZr5QyeYb9gdogeVLSxxXhnbW1as5_rUicjHScmE8412R8ekFb2xQX4y77O3wy-2bwSizBHCDRw-Cg1sukBDEYvSi1SqS8Gp"
	 * , 3); System.out.println(result.toString()); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */

}
