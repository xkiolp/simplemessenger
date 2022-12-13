package test1;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   int request;      //10오늘의 메세지 업데이트 9친구 리스트 업데이트 8사용자 검색 7pw변경 6친구등록 5탈퇴 ,3이면 id찾기, 2이면 로그인, 1이면 회원가입
   String id, pw,nickname,name,birthday,email;
   int check=-1;      //기본은 -1, 로그인이 되면 서버에서 1로 바꿔서 전송할 것.
   String Message = null;
   String salt=null;
   ArrayList<String> searchList = null;
   String friend_add=""; //12-10추가. 친구등록할 대상의 id
   ArrayList<user> friendList = null;
   
   public Data() 
   {
      this.id=null;
      this.pw=null;
      this.name=null;
      this.nickname=null;
      this.birthday=null;
      this.email=null;
   }
   
   public Data(String id,String pw) 
   {
      this.id=id;
      this.pw=pw;
   }
   
   public Data(String id,String pw,String name, String nickname,String email,String birthday) 
   {
      this.id=id;
      this.pw=pw;
      this.name=name;
      this.nickname=nickname;
      this.birthday=birthday;
      this.email=email;
   }
}