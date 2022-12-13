package test1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;


class user implements Serializable
{
	 
	private static final long serialVersionUID = 11L;

	  ArrayList<user> UserList; 
	  ArrayList<user> friendList = new ArrayList<user>(); 
	
	  LocalDateTime logOutTime;
	  LocalDateTime logInTime;
	  boolean connection=false;
	  InetAddress ip;
	  int port;
	  String id,pw,name,nickname,email,birthday;
	  String sql;
	  String salt;
	  String todayMessage;
	  
	  ObjectOutputStream oos;
	  ObjectInputStream ois;
	  
	  public user(String id,String pw,String name,String nickname,String email,String birthday,String ip,int portnum,ArrayList<user> UserList,String tm) throws Exception 
	  {
		  this.todayMessage=tm;
		  this.UserList=UserList;
		  this.id=id;
		  this.pw=pw;
		  this.name=name;
		  this.nickname=nickname;
		  this.email=email;
		  this.birthday=birthday;
		  this.port=portnum;
		  this.ip=InetAddress.getByName(ip);
	  }
	  
	  public user(Socket socket,Data d,ArrayList<user> UserList) throws SQLException, IOException
	  {			 
		 this.ois = new ObjectInputStream(socket.getInputStream());
		 this.oos = new ObjectOutputStream(socket.getOutputStream());
		 this.UserList=UserList;
		 this.id = d.id;
		 this.pw = d.pw;
		 this.name = d.name;
		 this.nickname = d.nickname;
		 this.email = d.email;
		 this.birthday = d.birthday;
		 this.salt=d.salt;
		 connection=true;
		 
		 ip=socket.getInetAddress();
		 port= socket.getLocalPort();
					 	
	  }
}