//104403533_���2B_���_��                
import java.io.*;
public class ChatMember implements Serializable {//���F�i�H�P�ɶǦr��M�u�W�H�ƩҼg��class
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
