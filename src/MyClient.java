//104403533_���2B_���_��                  Client
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
	private JLabel[] label = new JLabel[5]; //��ܤ�r������label
	private JTextField enterField;//�@�}�l�]�w�ʺ٪�Field
	private JTextField numberField;//��ܽu�W�H�ƪ�Field
	private JTextField nicknameField;//��ܼʺ٪�Field
	private JTextField sendMessage;//�ǰe�T����Field
	private JTextArea chat;//��ܰT����area(��Ѭ�����)
	private JPanel up;//���e���W�誺panel
	private JPanel down;//���e���U�誺panel
	private String myHost;//�s��IP
	private Socket connection; //�M���A���ݪ��s��
	private ObjectInputStream input; 
	private ObjectOutputStream output; 
	private String name="";//�ʺ�
	private boolean isError=true;//�P�_�ʺ٬O�_���ƪ��X�СA���F������j��@���ҥH�]��true
	
	public MyClient(String host){
		super("Chat Room Client");
		myHost = host;
		
		for(int i=0;i<label.length;i++)
			label[i] = new JLabel();
		label[2].setText("�п�J�z���ʺ١G");
		label[0].setText("�ڪ��ʺ�:");
		label[1].setText("�u�W�H��:");
		label[3].setText("���ʺ٤w���H�ϥ�");
		label[4].setText("�ǰe�T��:");
		
		label[0].setVisible(false);//���]������
		label[1].setVisible(false);
		label[3].setVisible(false);
		label[4].setVisible(false);
		
		nicknameField = new JTextField(10);
		nicknameField.setVisible(false);//���]������
		
		
		numberField = new JTextField(10);
		numberField.setVisible(false);//���]������
		sendMessage = new JTextField(20);
		sendMessage.addActionListener(new Handler());
		sendMessage.setVisible(false);//���]������
		enterField = new JTextField(10);
		enterField.addActionListener(new ActionListener() 
        {
           public void actionPerformed( ActionEvent event )
           {
        	   try{
           	
        		   output.writeObject(name = event.getActionCommand());//��J���ʺٶǰe�ܦ��A����
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
			 connection = new Socket(InetAddress.getByName( myHost ), 12345);//�إ߻P���A���ݪ��s�u
	      }catch ( IOException ioException )
	      {
	         JOptionPane.showMessageDialog(null,"Can not connect to chat server"); //�L�k�s�u�A���X�T����
	         System.exit(1);
	      } 
		ExecutorService worker = Executors.newFixedThreadPool( 1 );
        worker.execute( this ); //����ۤv��run()��k
	}
	public String getName(){
		return name;
	}
	private class Handler implements ActionListener{//�ǰe�T���ҥΪ�Handler
		public void actionPerformed(ActionEvent e){
			String message = "<"+name+">:"+e.getActionCommand()+"\n";//�]�w�|��ܦb��Ѭ������r��
			try{
			output.writeObject(message);//�ǰe�ܦ��A����
			output.flush();
			sendMessage.setText("");//�N�ǰe�T����Field�M��
			}catch(IOException ioe){
				ioe.printStackTrace();    
			}
		}
	}
	public void run()
	   {
		
		try 
	      {
			if(connection!=null){//�P�_�O�_���\�s�u
				input = new ObjectInputStream( connection.getInputStream() );//�إ�ObjectInputStream
				output = new ObjectOutputStream( connection.getOutputStream() );//�إ�ObjectOutputStream
         
				while(isError){//�P�_�ʺ٬O�_����
					if(input.readBoolean()){//�Y���A���ݦ^��true�A��ܼʺ٨S����
						isError = false;//�ʺ٨S���ơA�NisError�]��false
						label[0].setVisible(true);//����"�ڪ��ʺ�:"���ê��A
						label[1].setVisible(true);//����"�u�W�H��:"���ê��A
						label[2].setVisible(false);//"�п�J�z���ʺ١G"�]������
						label[3].setVisible(false);//"���ʺ٤w���H�ϥ�"�]������
						label[4].setVisible(true);//����"�ǰe�T��:"���ê��A
						enterField.setVisible(false);//�]������
						numberField.setVisible(true);//�������ê��A
						numberField.setEnabled(false);//�]������
						nicknameField.setVisible(true);//�������ê��A
						nicknameField.setText(name);//��ܼʺ�
						nicknameField.setEditable(false);//�]������ק�
						sendMessage.setVisible(true);//�������ê��A
					}else{//�Y���A���ݦ^��false
						label[3].setVisible(true);//���label:"���ʺ٤w���H�ϥ�"
					}
				}
				
				while(true){
					ChatMember member = (ChatMember)input.readObject();//����q���A���ݶǨӪ�����(�T���B�u�W�H��)
					chat.append(member.getMessage());//�N�ǨӪ��T���[���Ѭ�����
					numberField.setText(String.valueOf(member.getUserNum()));//��s�B��ܽu�W�H��
				}
				
			}
	      
	      }catch(SocketException se){
	    	  JOptionPane.showMessageDialog(null,"Can not connect to chat server");//���A�����_�s�u�A���X�T����
	    	  System.exit(1);
	      }catch(ClassNotFoundException nfe){
	    	  nfe.printStackTrace();
	      }catch ( IOException ioException ){	    
	         ioException.printStackTrace();         
	      } 
		
	   }
	
}
