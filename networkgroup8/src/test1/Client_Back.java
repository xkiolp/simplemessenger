package test1;

import java.io.*;
import java.net.*;
import java.util.*;

import test1.Client_mainGUI.Chat;

public class Client_Back extends Thread {
   int portNum;
   InetAddress ip=null;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   private Client_mainGUI chatgui;
   ArrayList<String> NickNameList = new ArrayList<String>(); // 유저목록을 저장합니다.
   Socket socket;
   
   public void getUserInfo( InetAddress ip,int portNum) 
   {
      this.ip=ip;
      this.portNum=portNum;
   }

   public void setGui(Client_mainGUI chatgui) {
      // 실행했던 Client_GUI 그 자체의 정보를 들고옵니다.
      this.chatgui = chatgui;
   }

   public void run() 
   {
      try {
         socket = new Socket(ip,portNum);
         InputStream inStream = socket.getInputStream();
         
         out = new ObjectOutputStream(socket.getOutputStream());
         in = new ObjectInputStream(inStream);
         Data d;
         
         while (in != null) 
         { 
            //서버로부터 받음
            d = (Data)in.readObject();
            
            if(d.request==15) 
            {
        		chatgui.showMessage(chatgui.mainFrame,d.Message+"파일이 C:\\network에 저장되었습니다.");
            }
            
            
            if(d.request==14&&d.check!=0)
            {
            	if(d.check==1)
            	{
            		chatgui.showMessage(chatgui.mainFrame,d.opponent.id+"수신자가 파일 전송을 수락했습니다.");
            		chatgui.sf.send();
            		
            	}else 
            	{
            		chatgui.showMessage(chatgui.mainFrame,"수신자가 파일 전송을 거부했습니다.");
            	}
            }
            if(d.request==14&&d.check==0)
            {
            	//yes
            	if(chatgui.confirm(chatgui.mainFrame, d.id+" 사용자에게 "+d.Message+"파일을 수신하시겠습니까?")==0) {            	
            		//서버에게 피요청자가 수락했다고 알려주기
            		d.check =1;           		           		            		        	
            	}
            	else        	
            		//서버에게 피요청자가 거절했다고 알려주기
            		d.check =-1;        
            	Transmit(d);
            }
            
           
            
            //d.request==7 암호번경, d.check==1 성공
            if(d.request==7 && d.check==1 )
            {
            	System.out.println("7 btn");
               chatgui.showMessage(chatgui.mainFrame,"암호 변경 성공");
            }
            
            //d.request==7 암호번경, d.check==-1 실패
            if(d.request==7 && d.check==-1) 
            {
            	System.out.println("7 btn");
               chatgui.showMessage(chatgui.mainFrame,"암호 변경 실패");
            }
            
            if(d.request==6 )
            {
            	chatgui.AppendUserList(d.friendList);
            }
            
            //사용자 검색
            if(d.request==8) 
            {
               System.out.println("8 btn");
               chatgui.showSearchFrame(d.searchList);
            }
            

            if(d.request==13&&d.check==-1)
            {
            	chatgui.chatFrame.dispose();
            }
            
            if(d.request==13&&d.check==1)
            {
            	chatgui.area.append(d.id+": "+d.Message+"\n");
            }
            
            //친구가 접속한 상황
            if(d.request==9) 
            {              
            	System.out.println("9 btn");
               chatgui.AppendUserList(d.friendList);
            }
            
            if(d.request==12&&d.check==3)
            {
            	Chat c=chatgui.getChat(chatgui.u, d.opponent, this);
            	c.jf.setVisible(true);
            }
            
            if(d.request==12&& d.check==0) 
            {
            	//yes
            	if(chatgui.confirm(chatgui.chatFrame,d.Message+" 1:1 채팅 요청")==0)
            	{
            		//서버에게 피요청자가 수락했다고 알려주기
            		d.check =1;
            		Transmit(d);
            		            		
            	}else 
            	{
            		//서버에게 피요청자가 거절했다고 알려주기
            		d.check =-1;
            		Transmit(d);   		    		
            	}
            }
            
            if(d.request==12&& d.check==-2) 
            {
            	chatgui.showMessage(chatgui.chatFrame,d.id+"이 1:1 채팅 거부");
            	System.out.println("refuse"+chatgui.u.id);
            	chatgui.chatFrame.setVisible(false);
            }
            
            if(d.request==12&& d.check==2)
            {
            	chatgui.showMessage(chatgui.chatFrame,d.Message+" 1:1 채팅 시작");
            } 
            
         }
      } catch (Exception e)
      {
    	  e.printStackTrace();
      }
   }

   public void Transmit(Data d) throws IOException
   {
	  System.out.println("id "+d.id+"request "+d.request);
      out.writeObject(d);
      out.flush();
   }
}