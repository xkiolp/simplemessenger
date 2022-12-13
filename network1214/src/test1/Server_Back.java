package test1;

import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.awt.FlowLayout;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Server_Back extends Thread 
{
   ArrayList<ReceiveInfo> ClientList = new ArrayList<ReceiveInfo>(); // 접속 클라이언트의 리스트
   ArrayList<user> UserList = new ArrayList<user>(); //전체 유저의 리스트
   ServerSocket serversocket;
   Socket socket;
   
   
   Connection con = null;
   Statement stmt = null;
   String url = "jdbc:mysql://localhost/chat";
   String user = "root";
   String passwd = "1234";
   
   //데이터베이스에서 user들 새로 읽어와서 서버 userlist 업데이트 && user의 today message도 업데이트
   public void updateUser() throws Exception 
   {
      //userList 전부 삭제
      UserList.removeAll(UserList);
        
      String sql ="select * from user";
      ResultSet rs;
      rs=stmt.executeQuery(sql);
     
      while(rs.next())
      {
         UserList.add(new user(rs.getString("id"),rs.getString("pw"),rs.getString("name"),rs.getString("nickname"),rs.getString("email"),rs.getString("birthday"),rs.getString("ip"),rs.getInt("portnum"),UserList,rs.getString("todayMessage")));
      }
   }
   
   //서버 시작 기본적으로 디폴트 설정 세팅
   public void Start_Server(int Port) 
   {
        try
        {
             Collections.synchronizedList(ClientList); 
             Collections.synchronizedList(UserList); 
          serversocket = new ServerSocket(Port);
           connectDatabase();
           updateUser();
                     
      } catch (Exception e) 
        {
         e.printStackTrace();
      }       
   }

   //데이터베이스 연결 함수
   public void connectDatabase() 
   {
      try
          {                         
           Class.forName("com.mysql.cj.jdbc.Driver");
           con = DriverManager.getConnection(url, user, passwd);
           stmt = con.createStatement();
           System.out.println("MySQL server connect");
        } catch(Exception e) {
           System.out.println("MySQL server failed> " + e.toString());
        } 
   }
   

   public void run() 
   {
      try {       
         while (true)
         {
            System.out.println("waiting new client...");
            socket = serversocket.accept(); // 포트 번호와 일치한 클라이언트의 소켓을 받습니다.
            Sign sign = new Sign(socket);   //첫 화면. 회원가입, id pw찾기, 로그인 등등
            
            //로그인 시 request가 2. 접속 & 로그인 성공. 메인페이지로 이동
            if((sign.d.request) == 2) 
            {
               socket = serversocket.accept(); // 포트 번호와 일치한 클라이언트의 소켓을 받습니다.
               System.out.println("[" + socket.getInetAddress() + "].");
               Data newData = sign.d;
               ReceiveInfo receive = new ReceiveInfo(socket,newData);
               ClientList.add(receive);
               receive.start();
            }
         }
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }

   
   // 모든 클라이언트들에게 메세지를 전송해줍니다.
   public void Transmitall(String Message)
   {
      for (int i = 0; i < ClientList.size(); i++) 
      {
         try {
            ReceiveInfo ri = ClientList.get(i);
            ri.Transmit(Message);
         } catch (Exception e) {
            System.out.println(e.getMessage());
         }
      }
   }
   
   
   //로그아웃 시간 기록
   public void logOutClient(user client) 
   {
     client.connection=false;
     client.logOutTime = LocalDateTime.now();
     
   }
   
 
   public void updateTodayMessage() throws SQLException 
   {
      String sql;
      ResultSet rs;
      
      for(user u : UserList) 
      {
         sql="select todayMessage from user where id ='"+u.id+"'";
         rs=stmt.executeQuery(sql);
         rs.next();
         u.todayMessage=rs.getString("todayMessage");
      }
   }
   
   //친구 업데이트
   public void updateFriends(user u,Data d) throws SQLException 
     {
       u.friendList.removeAll(u.friendList);
       String sql = "select * from friend where user1 = " +"'"+d.id+"' or user2 = "+"'"+d.id+"'";
       ResultSet rs=stmt.executeQuery(sql);
       
       while(rs.next()) 
       {
          String user1,user2;
          user1=rs.getString("user1");
          user2=rs.getString("user2");
          
         if(user1.equalsIgnoreCase(d.id)) 
          { 
             for(int i=0;i<UserList.size();i++) 
             {
                if(user2.equalsIgnoreCase(UserList.get(i).id)) 
                {
                   u.friendList.add(UserList.get(i));
                   break;
                }
             }
          }
          else 
          {
            for(int i=0;i<UserList.size();i++) 
             {
                if(user1.equalsIgnoreCase(UserList.get(i).id)) 
                {
                   u.friendList.add(UserList.get(i));
                   break;
                }
             }
          }
       }         
       rs.close();
     }
   
   
   class Sign extends Thread
   {
      Socket socket;
      public ObjectInputStream ois;
      public ObjectOutputStream oos;
      String id,pw;
      Data d;        
      
      
      public static String SHA512(String password, String hash) 
      {
        String salt = hash+password;
        String hex = null;
        
        try {

           MessageDigest msg = MessageDigest.getInstance("SHA-512");
           msg.update(salt.getBytes());
           
           hex = String.format("%128x", new BigInteger(1, msg.digest()));
           
        } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
        }
        return hex;
      }
      
      public Sign(Socket socket) 
      {
         this.socket = socket;      
   
         
         try
         {
           ois=new ObjectInputStream(this.socket.getInputStream());
            oos = new ObjectOutputStream(this.socket.getOutputStream());
      
            d = (Data)ois.readObject();
                  
            
            //id  찾기
            if(d.request==3) 
            {
               String sql = "select * from user where name = '"+d.name+"'and email = '"+d.email+"'and birthday = '"+d.birthday+"'";
               ResultSet rs = stmt.executeQuery(sql);
               
               if(rs.next())
               {
                  d.check=1;
                  d.id=rs.getString("id");
                    oos.writeObject(d);
               }else 
               {
                  d.check=-1;
                    oos.writeObject(d);
               }
            }
            
            //로그인
            if(d.request == 2) 
            {               
               String salt=getSalt(d.id);
               d.pw=SHA512(d.pw,salt);
            
               if(login_check(d.id,d.pw)==1)//로그인 체크
               {
                  d.check=1;
                  id=d.id;
                  pw=d.pw;
                  user u = checkLogIn(d,socket);      //로그인 시간 업데이트 & 접속정보 true로 업데이트
                  updateFriends(u,d);            //친구정보 업데이트
                  oos.writeObject(d);
                  oos.writeObject(u);
               }else if(login_check(d.id,d.pw)==-1)
               {
                  
                  d.check=-1;
                  d.request=0;
                  d.Message="id가 존재하지 않습니다.";
                  oos.writeObject(d);
               }else
               {
                  d.check=-1;
                  d.request=0;
                  d.Message="pw 오류입니다.";
                   oos.writeObject(d);
               }
            }
            
            //회원가입
            if(d.request == 1)
            {
               boolean success=true;

               //id 중복 확인
               if(id_duplication_check(d.id)==true)
                  success=false;
               
               if(success==true)
               {
                 insert_user(d.id,d.pw,d.name,d.nickname,d.email,d.birthday,socket.getInetAddress().toString().replace("/", ""),socket.getPort(),d.salt);        
                  d.check=1;
                  oos.writeObject(d);
                  UserList.add(new user(socket,d,UserList));
                  
               }else 
               {
                  d.check=-1;
                  oos.writeObject(d);
               }
            }      
         }catch(Exception e) 
         {
            e.printStackTrace();
         }
      }
    
      
      public String getSalt(String id) throws SQLException
      {
         String sql = "select salt from user where id = '"+id+"'";
         ResultSet rs= stmt.executeQuery(sql);
         if(rs.next())
            return rs.getString("salt");
         else
         {
            System.out.println("fadfs");
            return "null";
         }
      }
      
      
      //로그인 한 user의 정보 세팅 연결 상태, 로그인 시간, port, ip등등
      public user checkLogIn(Data d,Socket socket) throws SQLException
      {
         user u = null;
         for(int i=0;i<UserList.size();i++)
         {
            u = UserList.get(i);
            if(u.id.equalsIgnoreCase(d.id)) 
            {
               //오늘의 메세지 업데이트
               String sql = "select * from user where id = '"+d.id+"'";
               ResultSet rs = stmt.executeQuery(sql);
               rs.next();
               u.todayMessage = rs.getString("todayMessage");
               
               //포트, 아이피, 로그인 시간 업데이트
               u.port = socket.getPort();
               u.ip=socket.getInetAddress();
               u.connection=true;
               u.logInTime = LocalDateTime.now();
               rs.close();
               break;
            }
         }
         
         updateFriends(u,d);
         //지금 프로그램에 접속중인 클라이언트 리스트
         for(ReceiveInfo r : ClientList) 
         {
            //지금 접속한 user의 friendList(user와 친구)
            for(user friend : u.friendList) 
            {
               //user와 친구면서 지금 접속중이면
               if(r.id.equalsIgnoreCase(friend.id)) 
               {
                  try
                  {
                   //친구 리스트 업데이트 요청
                   r.d.request=9;
                   r.d.friendList=friend.friendList;
                   System.out.println(r.d.id+" "+u.id);
                  r.oos.writeObject(r.d);
                  } catch (IOException e) {
                  e.printStackTrace();
                  }
               }
            }
         }
         
      return u;
      }
      
      void insert_user(String id,String pw,String name,String nickname,String email,String birthday,String ip,int portnum,String salt) throws SQLException 
      {         
            String sql = "INSERT INTO user(id,pw,name,nickname,email,birthday,ip,portnum,salt) VALUES(" +"'"+id+"'"+","+"'"+pw+"' ,'"+name+"','"+nickname+"','"+email+"','"+birthday+"','"+ip+"',"+portnum+",'"+salt+"'"+")";
            System.out.println(ip);
            System.out.println(sql);
           stmt.executeUpdate(sql);
      }
          
      //회원 id 중복되었는지 확인 
      boolean id_duplication_check(String _i) {
            boolean flag = false;
            
            String id = _i;
            
            try {
               String checkingStr = "SELECT ID FROM user";
               ResultSet result = stmt.executeQuery(checkingStr);
               
               while(result.next()) 
               {
                  if(id.equals(result.getString("ID")))
                  {
                     flag =true;
                     System.out.println("duplication");
                     break;
                  }
               }
            } catch(Exception e) {
               flag = true;
               JOptionPane.showMessageDialog(null, "sign up failed > " + e.toString());
               System.exit(0);
            }
            return flag;
         }
      
      
      //login 확인. id에 대한 pw를 database에서 검색함.
      int login_check(String i, String p) {
         String id = i;
         String pw = p;
         
         try 
         {
           String checkingStr="select count(*) from user where id='"+id+"'";
           ResultSet result = stmt.executeQuery(checkingStr);
           result.next();
           //존재하지 않는 아이디 오류
           if(result.getInt("count(*)")==0) 
              return -1;
        
            checkingStr = "SELECT pw FROM user WHERE id='" + id + "'";
            result = stmt.executeQuery(checkingStr);
            result.next();
            
            //로그인 성공
            if(pw.equals(result.getString("pw")))         
                  System.out.println("success");
            else
            {
               System.out.println("fail");
               return -2;   //비밀번호가 다른 오류
            }
            
         } catch(Exception e) 
         {
            System.out.println("sign in failed > " + e.toString());
         }
         return 1;
      }
   }

   
   
   class ReceiveInfo extends Thread 
   { 
      // 각 네트워크(클라이언트)로부터 소켓을 받아 다시 내보내는 역할을 합니다.
     public ObjectInputStream ois;
     public ObjectOutputStream oos;
     public ObjectOutputStream out;
     public ObjectInputStream in;
     public OutputStream outStream; 
      String nickname,name,email,birthday;
      String Message;
      String location;
      Socket socket;
      Data d;
      String id;
      
      JLabel jl[]=new JLabel[1000]; //121209   
      JFrame jf[]=new JFrame[1000]; //121209
      int jljfcheckkey=0; //121209
      
      public ReceiveInfo(Socket socket,Data d)
      {
       
         try
         {
        	 this.socket = socket;
        	 outStream = socket.getOutputStream();
             this.d=d;
             this.id=d.id;
             oos = new ObjectOutputStream(outStream);
             ois = new ObjectInputStream(socket.getInputStream());
             out = oos;
             in=ois;
		} catch (IOException e) {
			e.printStackTrace();
		}     
      }
            
      public void run() 
      {
    	  try
    	  {     
    		  OutputStream outStream; 
              InputStream fin;

            while (true) 
            {
               Data d = (Data)in.readObject();
               
               if(d.request==15)
               {
            	   outStream = new FileOutputStream("C:\\network\\"+d.Message);
            	   //실제로 파일 바이트 스트림으로 전송
            	   fin = new FileInputStream(d.file);
            	   while(true)
            	   {
            		   int data = fin.read();
            		   outStream.write(data);
            		   
            		   if(data==-1)
            			   break;
            	   }
            	   fin.close();
            	   outStream.flush();
               }
               
               if(d.request==14&&d.check==0)
               {
            	   for(ReceiveInfo r : ClientList) {
            		   if(r.id.equalsIgnoreCase(d.opponent.id.trim())) 
            		   {
            			   r.oos.writeObject(d);
            			   break;
            		   }
            	   }
               }
               
               if(d.request==14&&d.check==1) 
               {
            	   for(ReceiveInfo r : ClientList) {
            		   if(r.id.equalsIgnoreCase(d.id.trim())) 
            		   {
            			   System.out.println("here "+r.id);
            			   r.oos.writeObject(d);
            			   break;
            		   }
            	   }   
               }
               
               if(d.request==13) 
               {
            	   for(ReceiveInfo r : ClientList) {
            		   if(r.id.equalsIgnoreCase(d.opponent.id.trim())) 
            		   {
            			   r.oos.writeObject(d);
            		   }
            	   }
               }
               
               if(d.request==12&&d.check==1)
               {
            	   for(ReceiveInfo r : ClientList) {
            		   if(r.id.equalsIgnoreCase(d.Message.trim())) 
            		   {
            			   d.request=12;
            			   d.check=2;
            			   r.oos.writeObject(d);
            		   }
            	   }
            	   
            	   for(user u : UserList)
            	   {
            		   if(u.id.equalsIgnoreCase(d.Message.trim()))
            		   {
            			   d.opponent=u;
            			   break;
            		   }
            	   }
            	   d.request=12;
            	   d.check=3;
            	   oos.writeObject(d);
               }
               
               if(d.request==12&&d.check==-1)
               {
            	   for(ReceiveInfo r : ClientList) {
            		   if(r.id.equalsIgnoreCase(d.Message.trim())) 
            		   {
            			   d.request=12;
            			   d.check=-2;
            			   r.oos.writeObject(d);
            		   }
            	   }
               }
               if(d.request==12&&d.check==-2) 
               {
            	   oos.writeObject(d);
               }
               
               
               if(d.request==12&&d.check==0) 
               {
            	   for(ReceiveInfo r : ClientList)
            	   {
            		   if(r.id.equalsIgnoreCase(d.Message.trim())) 
            		   {
            			   //상대방에게 승인여부 보내기 
            			   Data newData = new Data();
            			   newData.request=12;
            			   newData.check=0;
            			   newData.id=r.id;	
            			   newData.Message=d.id;
            			   r.oos.writeObject(newData); 			   
            		   }
            	   }
               }
               
               	//친구등록.
                if(d.request==6) //d는 자동적으로 입력받음.
                {
                   PreparedStatement pstmt;
                   
                   Server_Back sb1=new Server_Back();
                   sb1.connectDatabase(); //데이터베이스에 연결 pstmt=serverback.con.
                   sb1.updateUser();  //sb1.userlist에 모든 유저 정보 저장.
                                                         
                   int user11=-1;
                   int user22=-1;
                   String id1=d.id; //나의 아이디
                   
                   String id2=d.friend_add; //대상의 아이디.
                   //sb1.UserList는 유저목록. .get(i)는 [i]번째 유저. 
                   try 
                   {
                      for(int i=0;i<sb1.UserList.size();i++)
                      {                   
                         if((sb1.UserList.get(i).id).equals(id1))                                            
                            user11=i;                                          
                         
                         if(sb1.UserList.get(i).id.equals(id2))
                        	 user22=i; //sb1.UserList.get(user22)가 대상유저         
                      }
                                                          
                   }catch(IndexOutOfBoundsException e)
                   {
                      e.printStackTrace();
                   }
                   
                   sb1.UserList.get(user11).friendList.add(sb1.UserList.get(user22)); //나의 friendList에 대상을 추가.
                   sb1.UserList.get(user22).friendList.add(sb1.UserList.get(user11)); //friend의 friendList에 나를 추가 
                   
                    //데이터베이스에 나-대상관계가 있는지 확인하고 있으면 friend 테이블에 안 넣음.
                    String Query="select user1, user2 from friend";
                    Statement stmt2=sb1.con.createStatement();
                    ResultSet rs=stmt2.executeQuery(Query);

                   d.check=-1; //d.check로 친구등록 됐는지 안됐는지 확인.
                   
                     while(rs.next())
                      {
                        String rsid1=rs.getString(1);
                        String rsid2=rs.getString(2);

                                    
                         if((sb1.UserList.get(user11).id.equals(rsid1) && sb1.UserList.get(user22).id.equals(rsid2)) || (sb1.UserList.get(user11).id.equals(rsid2) && sb1.UserList.get(user22).id.equals(rsid1)))
                          {
                             
                             d.check=1; //이미 친구라서 등록안됨.
                             System.out.println("this relationship is already exists in friend table");//121209
                          }                                    
                      }
                                    
                   if(d.check==-1)
                   {
                      //데이터베이스에 나의 friendlist를 보고 friend에 (user1.id, user2.id)를 넣음.
                      String pQuery="insert into friend values(?, ?)";
                      pstmt=sb1.con.prepareStatement(pQuery);
                                    
                      pstmt.setString(1, sb1.UserList.get(user11).id);
                      pstmt.setString(2, sb1.UserList.get(user22).id);
                                    
                      pstmt.executeUpdate();
                      System.out.println(sb1.UserList.get(user11).id + " is now friend with " + sb1.UserList.get(user22).id);
                      
                      updateFriends(sb1.UserList.get(user11),d);            //친구정보 업데이트
                      d.friendList=sb1.UserList.get(user11).friendList;
                      oos.writeObject(d);
                      for(ReceiveInfo r : ClientList)
                      {
                    	  if(sb1.UserList.get(user22).id.equalsIgnoreCase(r.id)) 
                    	  {   
                    		  r.d.request=9;
                              r.d.friendList=sb1.UserList.get(user22).friendList;
                              r.oos.writeObject(r.d);
                    	  }
                      }
                   }
                   //d.check==1이면 friend 테이블에 이미 있는것이고, d.check==-1이면 새로 friend테이블에 넣는것인데
                   //check를 클라이언트에 보냄
                     //121209밑에 d.check확인부분추가
                    if(d.check==1)
                    {
                       if(jljfcheckkey!=0)
                       {
                          jf[jljfcheckkey-1].setVisible(false);
                       }
                       jf[jljfcheckkey]=new JFrame();
                       jl[jljfcheckkey]=new JLabel("이미 친구상태입니다.");
                       jl[jljfcheckkey].setSize(200, 100);
                       jf[jljfcheckkey].setLayout(new FlowLayout());
                       jf[jljfcheckkey].add(jl[jljfcheckkey]);
                       jf[jljfcheckkey].setLocation(700, 400);
                       jf[jljfcheckkey].setSize(200, 100);
                       jf[jljfcheckkey].setAlwaysOnTop(true);
                       jf[jljfcheckkey].setVisible(true);
                       jljfcheckkey=jljfcheckkey+1;                     
                       //JOptionPane.showMessageDialog(null, "이미 친구 상태입니다.");  //121209
                    }
                    else if(d.check==-1)
                    {
                       if(jljfcheckkey!=0)
                       {
                          jf[jljfcheckkey-1].setVisible(false);
                       }
                       jf[jljfcheckkey]=new JFrame();
                       jl[jljfcheckkey]=new JLabel("친구등록 성공");
                       jl[jljfcheckkey].setSize(200, 100);
                       jf[jljfcheckkey].setLayout(new FlowLayout());
                       jf[jljfcheckkey].add(jl[jljfcheckkey]);
                       jf[jljfcheckkey].setLocation(700, 400);
                       jf[jljfcheckkey].setSize(200, 100);
                       jf[jljfcheckkey].setAlwaysOnTop(true);
                       jf[jljfcheckkey].setVisible(true);
                       jljfcheckkey=jljfcheckkey+1;            
                       
                    }
  
                    oos.writeObject(d);
                }
               //탈퇴
               if(d.request==5) 
               {
                  removeClient(this,d.id);
               }
               //비밀번호 변경
               else if(d.request==7) 
               {
                  if(changePassword(d)) 
                  {
                     d.check=1;
                     oos.writeObject(d);
                  }else 
                  {
                     d.check=-1;
                     oos.writeObject(d);
                  }
               }
               else if(d.request==8)
               {
                  d.searchList = searchId(d.Message); 
                  d.check=1;   
                 oos.writeObject(d);
               }
               //오늘의 메세지 업데이트
               else if(d.request==10)
               {
                  if(d.Message.equalsIgnoreCase("update"))
                     updateTodayMessage();
                  else
                  {
                     //오늘의 메세지 변경
                     String sql = "update user set todayMessage='"+d.Message+"' where id = '"+d.id+"'";
                     stmt.executeUpdate(sql);
                     updateTodayMessage();
                  }
                }
            }
                        
            }catch(Exception e) 
         {
               e.printStackTrace();
         }
      }
      
      //id 부분검색해서 결과를 arrayList로 반환
      public ArrayList<String> searchId(String id) throws SQLException
      {
         String sql = "select id from user where id like '%"+id+"%'";
         ResultSet rs= stmt.executeQuery(sql);
         ArrayList<String> al = new ArrayList<>();
         
         while(rs.next())
         {
            al.add(rs.getString("id"));
         }
         
         return al;
      }
        
      //비밀번호 변경 함수. 성공하면 true 실패하면 false 반환
      public boolean changePassword(Data d)
      {
         boolean ischanged =false;
         
         for(user u : UserList) 
         {
            if(u.id==d.id) 
            {
               u.salt=d.salt;
               u.pw=d.pw;
               break;
            }
         }
         
         String sql = "update user set pw = '"+d.pw+"' where id = '"+d.id+"'";
         
         try
         {
         stmt.executeUpdate(sql);
          sql = "update user set salt = '"+d.salt+"' where id = '"+d.id+"'";
          stmt.executeUpdate(sql);
          
         } catch (SQLException e) {
         e.printStackTrace();
         }
        
         ischanged=true;
         
         return ischanged;
      }
      
      //탈퇴 기능
      public void removeClient(ReceiveInfo Client, String id) throws Exception 
      {
             //클라이언트 리스트에서 제거
         ClientList.remove(Client);
         
         //id 유일하다고 설정. id 기준으로 데이터베이스에서 user를 찾아서 삭제
         String sql = "delete from user where id = '"+ id+"'";
         System.out.println(sql);
         stmt.executeUpdate(sql);
         //user list 업데이트
         updateUser();
         
         System.out.println(id + " is removed.");
      }
      

      
      public void Transmit(String Message) throws IOException
      {
         // 전달받은 값(Message)를 각 클라이언트의 쓰레드에 맞춰 전송합니다.
            
      }
   }
   
}