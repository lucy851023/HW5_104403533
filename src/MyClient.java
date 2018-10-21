//104403533_資管2B_莊于萱                  Client
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyClient extends JFrame implements Runnable{
	private JLabel[] label = new JLabel[5]; //顯示文字說明的label
	private JTextField enterField;//一開始設定暱稱的Field
	private JTextField numberField;//顯示線上人數的Field
	private JTextField nicknameField;//顯示暱稱的Field
	private JTextField sendMessage;//傳送訊息的Field
	private JTextArea chat;//顯示訊息的area(聊天紀錄區)
	private JPanel up;//位於畫面上方的panel
	private JPanel down;//位於畫面下方的panel
	private String myHost;//存放IP
	private Socket connection; //和伺服器端的連結
	private ObjectInputStream input; 
	private ObjectOutputStream output; 
	private String name="";//暱稱
	private boolean isError=true;//判斷暱稱是否重複的旗標，為了先執行迴圈一次所以設為true
	
	public MyClient(String host){
		super("Chat Room Client");
		myHost = host;
		
		for(int i=0;i<label.length;i++)
			label[i] = new JLabel();
		label[2].setText("請輸入您的暱稱：");
		label[0].setText("我的暱稱:");
		label[1].setText("線上人數:");
		label[3].setText("此暱稱已有人使用");
		label[4].setText("傳送訊息:");
		
		label[0].setVisible(false);//先設為隱藏
		label[1].setVisible(false);
		label[3].setVisible(false);
		label[4].setVisible(false);
		
		nicknameField = new JTextField(10);
		nicknameField.setVisible(false);//先設為隱藏
		
		
		numberField = new JTextField(10);
		numberField.setVisible(false);//先設為隱藏
		sendMessage = new JTextField(20);
		sendMessage.addActionListener(new Handler());
		sendMessage.setVisible(false);//先設為隱藏
		enterField = new JTextField(10);
		enterField.addActionListener(new ActionListener() 
        {
           public void actionPerformed( ActionEvent event )
           {
        	   try{
           	
        		   output.writeObject(name = event.getActionCommand());//輸入的暱稱傳送至伺服器端
        		   output.flush();

        	   }catch(IOException e){
        		   System.out.println(e.getMessage());
        	   }
           } 
        });
		
		chat = new JTextArea();
		
		up = new JPanel();
		up.setLayout(new GridLayout(2,4));
		up.add(label[0]);
		up.add(nicknameField);
		up.add(label[1]);
		up.add(numberField);
		up.add(label[2]);
		up.add(enterField);
		up.add(label[3]);
		
		down = new JPanel();
		down.setLayout(new GridLayout(1,2));
		down.add(label[4]);
		down.add(sendMessage);
		add(up,BorderLayout.NORTH);
		add(down,BorderLayout.SOUTH);
		add(new JScrollPane(chat),BorderLayout.CENTER);
		
		
		setSize(600,400);
		setVisible(true);
		setResizable(false);
		
		try 
	      {
			 connection = new Socket(InetAddress.getByName( myHost ), 12345);//建立與伺服器端的連線
	      }catch ( IOException ioException )
	      {
	         JOptionPane.showMessageDialog(null,"Can not connect to chat server"); //無法連線，跳出訊息框
	         System.exit(1);
	      } 
		ExecutorService worker = Executors.newFixedThreadPool( 1 );
        worker.execute( this ); //執行自己的run()方法
	}
	public String getName(){
		return name;
	}
	private class Handler implements ActionListener{//傳送訊息所用的Handler
		public void actionPerformed(ActionEvent e){
			String message = "<"+name+">:"+e.getActionCommand()+"\n";//設定會顯示在聊天紀錄的字串
			try{
			output.writeObject(message);//傳送至伺服器端
			output.flush();
			sendMessage.setText("");//將傳送訊息的Field清空
			}catch(IOException ioe){
				ioe.printStackTrace();    
			}
		}
	}
	public void run()
	   {
		
		try 
	      {
			if(connection!=null){//判斷是否成功連線
				input = new ObjectInputStream( connection.getInputStream() );//建立ObjectInputStream
				output = new ObjectOutputStream( connection.getOutputStream() );//建立ObjectOutputStream
         
				while(isError){//判斷暱稱是否重複
					if(input.readBoolean()){//若伺服器端回傳true，表示暱稱沒重複
						isError = false;//暱稱沒重複，將isError設為false
						label[0].setVisible(true);//取消"我的暱稱:"隱藏狀態
						label[1].setVisible(true);//取消"線上人數:"隱藏狀態
						label[2].setVisible(false);//"請輸入您的暱稱："設為隱藏
						label[3].setVisible(false);//"此暱稱已有人使用"設為隱藏
						label[4].setVisible(true);//取消"傳送訊息:"隱藏狀態
						enterField.setVisible(false);//設為隱藏
						numberField.setVisible(true);//取消隱藏狀態
						numberField.setEnabled(false);//設為隱藏
						nicknameField.setVisible(true);//取消隱藏狀態
						nicknameField.setText(name);//顯示暱稱
						nicknameField.setEditable(false);//設為不能修改
						sendMessage.setVisible(true);//取消隱藏狀態
					}else{//若伺服器端回傳false
						label[3].setVisible(true);//顯示label:"此暱稱已有人使用"
					}
				}
				
				while(true){
					ChatMember member = (ChatMember)input.readObject();//收到從伺服器端傳來的物件(訊息、線上人數)
					chat.append(member.getMessage());//將傳來的訊息加到聊天紀錄區
					numberField.setText(String.valueOf(member.getUserNum()));//更新、顯示線上人數
				}
				
			}
	      
	      }catch(SocketException se){
	    	  JOptionPane.showMessageDialog(null,"Can not connect to chat server");//伺服器中斷連線，跳出訊息框
	    	  System.exit(1);
	      }catch(ClassNotFoundException nfe){
	    	  nfe.printStackTrace();
	      }catch ( IOException ioException ){	    
	         ioException.printStackTrace();         
	      } 
		
	   }
	
}
