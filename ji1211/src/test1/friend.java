/*
package test1;

import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//친구등록 버튼을 누르면, 나의 User id, 대상의 User id를 전달함

//버튼을 누르면 friendregisterlistener(나의 i, 대상의i)로 리스너가 실행되고
//리스너에서 friend클래스 불러와서 Data형식으로 d.id에 나의 id, 
//d.friend_add에 대상의 id 넣어서 서버에 전달하면
//서버에서 d.id랑 d.friend_add 친구관계를 friend 테이블에 넣음.

public class friend {
	int check=-1;  //서버에서 테이블에 넣어지면 check 0, friend 테이블에 이미 있어서 못넣으면 check=1.
	public friend(user user1, user user2){
		{
			
			
			
			try {
				
				
				Client_Back cb=new Client_Back();
				Data d=new Data(); 
				 
				//user1(나)의 정보를 데이터에 저장.
				d.id=user1.id;
				d.pw=user1.pw;
				d.name=user1.name;
				d.nickname=user1.nickname;
				d.birthday=user1.birthday;
				d.email=user1.email;
				//친구의 id를 d.friendadd에 저장.
				d.friend_add=user2.id;
				d.request=6;
				
				cb.Transmit(d);
				
				ObjectInputStream in=new ObjectInputStream(cb.socket.getInputStream());
				
				//서버로부터 데이터 읽어옴.
				try {
					Data d2=(Data)in.readObject();
					
					if(d2.check==-1)//기본.
					{
						check=-1;
					}
					if(d2.check==1) //이미 친구등록돼있음.
					{
						check=1;
					}
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}catch(IOException e)
			{
				e.printStackTrace();
			}

			
			
		}
		
		
		
	}
	{
	}
}

//친구등록 버튼에 대해서 friendRegisterListener 실행.
//친구등록 버튼 생성할때 (UserList.get(i)로 나를 찾을 i값, UserList.get(i)로 대상을 찾을i값)으로 생성.
class friendRegisterListener implements ActionListener{
	int mygeti; //userlist.get(i)로 나의 user를 찾을때의 i값.
	int opponentgeti; //대상을 get(i)로 찾을때의 i값.
	
	public friendRegisterListener(int mygeti, int opponentgeti)
	{
		this.mygeti=mygeti;
		this.opponentgeti=opponentgeti;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		user user1; //나의 user를 입력받음.
		user user2; //대상의 user를 입력받음.
		
		try {
			
			//userlist 불러오기 위해 server_back 생성.
			Server_Back sb1=new Server_Back();
			sb1.connectDatabase(); //데이터베이스에 연결 pstmt=serverback.con.
			sb1.updateUser();  //sb1.userlist에 모든 유저 정보 저장.
			
			user1=sb1.UserList.get(mygeti);
			user2=sb1.UserList.get(opponentgeti);
			friend fr=new friend(user1, user2); //위에 있는 friend객체로 두개 보냄.
			
			if(fr.check==1) //이미 나와 대상이 친구인 상태.
			{//
				JFrame jf=new JFrame(); //"이미 친구임"을 표시하는 새창.
				jf.setSize(200, 100);
				jf.setTitle("warning");
				
				JLabel fjl=new JLabel("two person is already friend");
				jf.add(fjl);
				
				jf.setVisible(true);
				
			}
			else if(fr.check==-1) //일반적인 상황
			{
				JButton jb=(JButton)e.getSource();
				if(jb.getText().equals("친구등록"))
				{
					jb.setText("친구등록됨");
					
				}
				
				
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	
}
*/