//104403533_資管2B_莊于萱                       Server
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.nio.*;
import javax.swing.text.BadLocationException;

public class MyServer extends JFrame{
	private JTextField userNumber;//顯示線上人數的Field
	private JTextField post;//傳送廣播的Field
	private JTextArea status;//顯示訊息記錄的TextArea(訊息紀錄區)
	private JTextArea user;//顯示線上人員名單的TextArea
	private JLabel label1;//顯示"線上人數:"的label
	private JLabel label2;//顯示"廣播:"的label
	private JPanel up;//位於畫面上方的panel
	private ServerSocket server;//伺服器
	private ExecutorService runGame;
	private Map<String,Integer> map = new HashMap<String,Integer>();//紀錄使用者暱稱和ID的Map
	private int count;//紀錄線上人數
	private ArrayList<Person> people = new ArrayList<Person>();//存入執行中的執行序
	private int id;//紀錄目前使用過的ID的最大值
	
	
	public MyServer(){
		super("Chat Room Server");
		label1 = new JLabel("線上人數:");
		label2 = new JLabel("廣播:");
		userNumber = new JTextField("0",20);
		userNumber.setEnabled(false);
		post = new JTextField(15);
		post.setEditable(false);
		post.addActionListener(new Handler());
		status = new JTextArea();
		user = new JTextArea(100,20);
		up = new JPanel();
		up.add(label1);
		up.add(userNumber);
		up.add(label2);
		up.add(post);
		add(up,BorderLayout.NORTH);
		add(new JScrollPane(user),BorderLayout.EAST);
		add(new JScrollPane(status),BorderLayout.CENTER);
		
		runGame = Executors.newCachedThreadPool();
		
		setSize(600,600);
		setVisible(true);
		setResizable(false);
	}
	
	private class Handler implements ActionListener{//廣播所使用的Handler，用於接收輸入進來的訊息，加入自己的訊息紀錄區並傳到客戶端
		public void actionPerformed(ActionEvent e){
			String message = "***ServerBrocast***"+e.getActionCommand()+"\n";//設定要顯示在自己訊息紀錄區和傳給客戶端的字串
			
			for(Person pp:people)//讓迴圈跑所有的在線客戶端
				pp.sendData(message);//傳字串給客戶端
			status.append(message);//把字串加入自己的訊息紀錄區
			post.setText("");//清空廣播的Field
			
		}
	}
	
	public void runServer(){
		try 
	    {
			server = new ServerSocket(12345,10);//建立伺服器
			status.append("<System>聊天室已經上線，等待來自port:12345的連線\n");//建立完將此字串加入自己的訊息紀錄區
			while ( true ) 
			{
				try 
				{
					Person p = new Person(server.accept(),id);//等待客戶端連線，，每有一個客戶端連上線，就建立一個Person物件
					people.add(p);//連線後將物件放入ArrayList裡
					runGame.execute(p);//執行物件的run()方法(啟動執行序)
					post.setEditable(true);
					id++;
				}catch ( EOFException eofException ){
					eofException.printStackTrace();
				} 
			}
			
	      }catch ( IOException ioException ) {
	         ioException.printStackTrace();
	      } 
		
	}
	 
	

	private class Person implements Runnable{//處理伺服器端和客戶端連結的物件(執行序)
		private Socket connection; //和客戶端的連結
	      private ObjectInputStream input; 
	      private ObjectOutputStream output;
	      private boolean isError = true;//判斷暱稱是否重複的旗標，為了要執行迴圈先設為true
	      private String myName="";//客戶端取的暱稱
	      private int myID;//客戶的ID
	     
	      
		public Person(Socket socket,int ID){
			connection = socket;
			myID = ID;
			
			try 
	        {
				output = new ObjectOutputStream(connection.getOutputStream());//建立ObjectOutputStream
				output.flush();
				input = new ObjectInputStream( connection.getInputStream() );//建立ObjectInputStream
	        }catch( IOException ioException ){ 
	            ioException.printStackTrace();
	            System.exit( 1 );
	        } 
			
		}
		
		private void sendData( String message ){//傳送字串給客戶端的方法
		
			 try{
				 ChatMember member = new ChatMember(message,count);//建立ChatMember物件，可同時傳送訊息和線上人數
		         output.writeObject( member );//傳送物件給客戶端
		         output.flush();
			 }catch(IOException ioe){
					ioe.printStackTrace();    
				}
		}
		
		public void run(){
			
			try 
	         {
					
					while(isError)//判斷暱稱是否重複
					{
						String username = (String)input.readObject();//接收到從客戶端傳來的暱稱
					if(map.containsKey(username)){//若重複了
						output.writeBoolean(false);//回傳false給客戶端
						output.flush();
					}else{//若沒有重複
						output.writeBoolean(true);//回傳true給客戶端
						output.flush();
						map.put(username,myID);//將此客戶的暱稱、ID存入map中
						user.append(username+"(ID:"+myID+")\n");//將此客戶加入線上人員名單(TextArea)
						count++;//線上人數加一
						ChatMember ss = new ChatMember("<Server>您已成功連線至server:'127.0.0.1',port:12345\n"
								,count);//建立一個ChatMember物件，加入要傳給客戶的字串和線上人數
						output.writeObject(ss);//將物件傳給客戶端
						status.append("<Server>client:'127.0.0.1'連線建立，ID:'"+myID+"'\n");//將和客戶端連線成功的訊息加入伺服器的訊息紀錄區
						for(Person pp:people)//傳訊息給所有客戶端
							pp.sendData("<Server>ClientNAME:"+username+"進入聊天室\n");
						
						myName=username;
			
						userNumber.setText(String.valueOf(count));//更新顯示伺服器端線上人數的Field
						isError = false;//暱稱沒重複，設為false好離開迴圈
					}
				}
					
				while(!isError){//暱稱沒重複
					String s = (String)input.readObject();//接收從客戶端傳來的字串
					status.append(s);//加到伺服器端的訊息紀錄區
					for(Person pp:people)//將收到的字串傳給所有客戶端
						pp.sendData(s);
				
				}
	         }catch(SocketException se){//客戶端停止連線
				
				if(!myName.equals("")){
					
					try{
						status.append(String.format("<Server>Client ID:'%d'已終止連線\n", map.get(myName)));//把停止連線訊息加到伺服器端的訊息紀錄區
						int start = user.getLineStartOffset(map.get(myName));//用ID對應名單
						int end = user.getLineEndOffset(map.get(myName));//用ID對應名單
						user.replaceRange("\n",start,end);//將客戶從伺服器的線上人員名單中清掉
					}catch(BadLocationException be){
		        	 be.printStackTrace();
					}catch(NullPointerException npe){
		        	 
					}
				
					count--;//線上人數減一
					userNumber.setText(String.valueOf(count));//更新顯示線上人數的Field
					map.remove(myName);
					people.remove(this);//將自己(Person物件)從ArrayList中移除
					for(Person p:people)//傳送離開訊息給所有在線客戶端
					p.sendData(String.format("<Server>ClientNAME:'%s'已離開聊天室\n",myName));
				}else{
					//user.append("\n");
					id--;
					people.remove(this);
				}
				//System.out.println(se.getMessage());
				
	         }catch ( ClassNotFoundException e){
	        	 e.printStackTrace();
	         }catch ( IOException ioException ){
			     ioException.printStackTrace();
			 } 
			
		}
	}

}
