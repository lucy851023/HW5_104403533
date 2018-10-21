//104403533_資管2B_莊于萱                
import java.io.*;
public class ChatMember implements Serializable {//為了可以同時傳字串和線上人數所寫的class
	private String message;
	private int userNum;
	
	public ChatMember(String s,int u){
		message = s;
		userNum = u;
	}
	
	public String getMessage(){
		return message;
	}
	
	public int getUserNum(){
		return userNum;
	}
}
