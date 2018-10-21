//104403533_資管2B_莊于萱                 @執行Server的主程式於此
import javax.swing.*;
public class MyServerTest {
	public static void main(String[] args){
		MyServer s = new MyServer();
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.runServer();
	}
}
