//104403533_���2B_���_��                       Server
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
	private JTextField userNumber;//��ܽu�W�H�ƪ�Field
	private JTextField post;//�ǰe�s����Field
	private JTextArea status;//��ܰT���O����TextArea(�T��������)
	private JTextArea user;//��ܽu�W�H���W�檺TextArea
	private JLabel label1;//���"�u�W�H��:"��label
	private JLabel label2;//���"�s��:"��label
	private JPanel up;//���e���W�誺panel
	private ServerSocket server;//���A��
	private ExecutorService runGame;
	private Map<String,Integer> map = new HashMap<String,Integer>();//�����ϥΪ̼ʺ٩MID��Map
	private int count;//�����u�W�H��
	private ArrayList<Person> people = new ArrayList<Person>();//�s�J���椤�������
	private int id;//�����ثe�ϥιL��ID���̤j��
	
	
	public MyServer(){
		super("Chat Room Server");
		label1 = new JLabel("�u�W�H��:");
		label2 = new JLabel("�s��:");
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
	
	private class Handler implements ActionListener{//�s���ҨϥΪ�Handler�A�Ω󱵦���J�i�Ӫ��T���A�[�J�ۤv���T�������ϨöǨ�Ȥ��
		public void actionPerformed(ActionEvent e){
			String message = "***ServerBrocast***"+e.getActionCommand()+"\n";//�]�w�n��ܦb�ۤv�T�������ϩM�ǵ��Ȥ�ݪ��r��
			
			for(Person pp:people)//���j��]�Ҧ����b�u�Ȥ��
				pp.sendData(message);//�Ǧr�굹�Ȥ��
			status.append(message);//��r��[�J�ۤv���T��������
			post.setText("");//�M�żs����Field
			
		}
	}
	
	public void runServer(){
		try 
	    {
			server = new ServerSocket(12345,10);//�إߦ��A��
			status.append("<System>��ѫǤw�g�W�u�A���ݨӦ�port:12345���s�u\n");//�إߧ��N���r��[�J�ۤv���T��������
			while ( true ) 
			{
				try 
				{
					Person p = new Person(server.accept(),id);//���ݫȤ�ݳs�u�A�A�C���@�ӫȤ�ݳs�W�u�A�N�إߤ@��Person����
					people.add(p);//�s�u��N�����JArrayList��
					runGame.execute(p);//���檫��run()��k(�Ұʰ����)
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
	 
	

	private class Person implements Runnable{//�B�z���A���ݩM�Ȥ�ݳs��������(�����)
		private Socket connection; //�M�Ȥ�ݪ��s��
	      private ObjectInputStream input; 
	      private ObjectOutputStream output;
	      private boolean isError = true;//�P�_�ʺ٬O�_���ƪ��X�СA���F�n����j����]��true
	      private String myName="";//�Ȥ�ݨ����ʺ�
	      private int myID;//�Ȥ᪺ID
	     
	      
		public Person(Socket socket,int ID){
			connection = socket;
			myID = ID;
			
			try 
	        {
				output = new ObjectOutputStream(connection.getOutputStream());//�إ�ObjectOutputStream
				output.flush();
				input = new ObjectInputStream( connection.getInputStream() );//�إ�ObjectInputStream
	        }catch( IOException ioException ){ 
	            ioException.printStackTrace();
	            System.exit( 1 );
	        } 
			
		}
		
		private void sendData( String message ){//�ǰe�r�굹�Ȥ�ݪ���k
		
			 try{
				 ChatMember member = new ChatMember(message,count);//�إ�ChatMember����A�i�P�ɶǰe�T���M�u�W�H��
		         output.writeObject( member );//�ǰe���󵹫Ȥ��
		         output.flush();
			 }catch(IOException ioe){
					ioe.printStackTrace();    
				}
		}
		
		public void run(){
			
			try 
	         {
					
					while(isError)//�P�_�ʺ٬O�_����
					{
						String username = (String)input.readObject();//������q�Ȥ�ݶǨӪ��ʺ�
					if(map.containsKey(username)){//�Y���ƤF
						output.writeBoolean(false);//�^��false���Ȥ��
						output.flush();
					}else{//�Y�S������
						output.writeBoolean(true);//�^��true���Ȥ��
						output.flush();
						map.put(username,myID);//�N���Ȥ᪺�ʺ١BID�s�Jmap��
						user.append(username+"(ID:"+myID+")\n");//�N���Ȥ�[�J�u�W�H���W��(TextArea)
						count++;//�u�W�H�ƥ[�@
						ChatMember ss = new ChatMember("<Server>�z�w���\�s�u��server:'127.0.0.1',port:12345\n"
								,count);//�إߤ@��ChatMember����A�[�J�n�ǵ��Ȥ᪺�r��M�u�W�H��
						output.writeObject(ss);//�N����ǵ��Ȥ��
						status.append("<Server>client:'127.0.0.1'�s�u�إߡAID:'"+myID+"'\n");//�N�M�Ȥ�ݳs�u���\���T���[�J���A�����T��������
						for(Person pp:people)//�ǰT�����Ҧ��Ȥ��
							pp.sendData("<Server>ClientNAME:"+username+"�i�J��ѫ�\n");
						
						myName=username;
			
						userNumber.setText(String.valueOf(count));//��s��ܦ��A���ݽu�W�H�ƪ�Field
						isError = false;//�ʺ٨S���ơA�]��false�n���}�j��
					}
				}
					
				while(!isError){//�ʺ٨S����
					String s = (String)input.readObject();//�����q�Ȥ�ݶǨӪ��r��
					status.append(s);//�[����A���ݪ��T��������
					for(Person pp:people)//�N���쪺�r��ǵ��Ҧ��Ȥ��
						pp.sendData(s);
				
				}
	         }catch(SocketException se){//�Ȥ�ݰ���s�u
				
				if(!myName.equals("")){
					
					try{
						status.append(String.format("<Server>Client ID:'%d'�w�פ�s�u\n", map.get(myName)));//�ⰱ��s�u�T���[����A���ݪ��T��������
						int start = user.getLineStartOffset(map.get(myName));//��ID�����W��
						int end = user.getLineEndOffset(map.get(myName));//��ID�����W��
						user.replaceRange("\n",start,end);//�N�Ȥ�q���A�����u�W�H���W�椤�M��
					}catch(BadLocationException be){
		        	 be.printStackTrace();
					}catch(NullPointerException npe){
		        	 
					}
				
					count--;//�u�W�H�ƴ�@
					userNumber.setText(String.valueOf(count));//��s��ܽu�W�H�ƪ�Field
					map.remove(myName);
					people.remove(this);//�N�ۤv(Person����)�qArrayList������
					for(Person p:people)//�ǰe���}�T�����Ҧ��b�u�Ȥ��
					p.sendData(String.format("<Server>ClientNAME:'%s'�w���}��ѫ�\n",myName));
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
