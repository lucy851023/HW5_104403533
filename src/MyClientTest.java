//104403533_���2B_���_��                 @����Client���D�{����
import javax.swing.*;
public class MyClientTest {
	public static void main(String[] args){
		MyClient c;
		if ( args.length == 0 )
	         c = new MyClient( "127.0.0.1" ); // localhost
	      else
	         c = new MyClient( args[ 0 ] ); // use args

	      
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
