package test1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client_Back extends Thread {
   int portNum;
   InetAddress ip=null;
   private String Message;
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
         out = new ObjectOutputStream(socket.getOutputStream());
         in = new ObjectInputStream(socket.getInputStream());
         Data d;
         
         while (in != null) 
         { 
            //서버로부터 받음
            d = (Data)in.readObject();
            
            //d.request==7 암호번경, d.check==1 성공
            if(d.request==7 && d.check==1 )
            {
               System.out.println("7 btn");
               chatgui.showMessage("암호 변경 성공");
            }
            
            //d.request==7 암호번경, d.check==-1 실패
            if(d.request==7 && d.check==-1) 
            {
               System.out.println("7 btn");
               chatgui.showMessage("암호 변경 실패");
            }
            
            //사용자 검색
            if(d.request==8) 
            {
               System.out.println("8 btn");
               chatgui.showSearchFrame(d.searchList);
            }
            
            //친구가 접속한 상황
            if(d.request==9) 
            {              
               System.out.println("9 btn");
               chatgui.AppendUserList(d.friendList);
            }
            
            
         }
      } catch (Exception e)
      {
         System.out.println(e.getMessage());
      }
   }

   public void Transmit(Data d) throws IOException
   {
     System.out.println("id "+d.id+"request "+d.request);
      out.writeObject(d);
      out.flush();
   }
}