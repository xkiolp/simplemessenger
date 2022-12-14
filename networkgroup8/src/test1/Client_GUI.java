package test1;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.*;
import java.io.*;

public class Client_GUI 
{
   public static void main(String[] args) throws Exception
   {
      Socket socket;
      String ip=null;
      int portNum = 0;
      FileReader fr = null;
      BufferedReader br = null;
      ObjectInputStream ois=null;
      
      try {
         fr = new FileReader(".\\config.txt");      //서버정보를 저장한 config.txt 파일
         
         br=new BufferedReader(fr);               //config.txt에서 서버 정보를 읽어옴
         ip=br.readLine();
         portNum=Integer.parseInt(br.readLine());     
                  
      }catch(FileNotFoundException e)               //config.txt파일을 찾을 수 없다면
      {
         ip="127.0.0.1";                        //ip를 127.0.0.1로 port를 9999로 설정
         portNum=9999;
      } catch (IOException e)
      {
      e.printStackTrace();
      };
    
      socket = new Socket(ip,portNum);
      LoginGUI LG = new LoginGUI(socket,ip,portNum);   
   }
}


class LoginGUI extends JFrame implements ActionListener 
{
   // 유저의 로그인 창
   
   /* Panel */
   JPanel basePanel;
   JPanel centerPanel = new JPanel(new BorderLayout());
   JPanel westPanel = new JPanel();
   JPanel eastPanel = new JPanel();
   JPanel southPanel = new JPanel();
   JPanel northPanel = new JPanel();
   
   /* Label */
   JLabel idL = new JLabel("id");
   JLabel pwL = new JLabel("password");
   JLabel nameL = new JLabel("name");
   JLabel nicknameL = new JLabel("nickname");
   JLabel emailL = new JLabel("email");
   JLabel birthdayL = new JLabel("birthday");


   /* TextField */
   JTextField idF = new JTextField();
   JPasswordField pwF = new JPasswordField();
   JTextField nameF = new JTextField();
   JTextField nicknameF = new JTextField();
   JTextField emailF = new JTextField();
   JTextField birthdayF = new JTextField();

   
   /* Button */
   JButton findBtn = new JButton("아이디 찾기");
   JButton loginBtn = new JButton("로그인");
   JButton joinBtn = new JButton("회원가입");
   JButton exitBtn = new JButton("종료");

   Socket socket;
   String id,pw,name,nickname,email,birthday;
   
   ObjectOutputStream oos = null;
   ObjectInputStream ois = null;
   user u;
   
   String ip;
   int portNum;
   
   public LoginGUI(Socket socket,String ip,int portNum) 
   {
         setTitle("로그인 창");
         this.socket=socket;
         this.ip=ip;
         this.portNum=portNum;
                
         //추가 뒷 배경 설정. 상대경로로 설정이 잘안되서 절대경로로 이미지 얻음.
         ImageIcon ic2=new ImageIcon("C:\\img\\bg.png"); 
         JPanel basePanel = new JPanel(new BorderLayout())
         {
                public void paintComponent(Graphics g) {
                    g.drawImage(ic2.getImage(), 0, 0, null);
                   
                    setOpaque(false); //그림을 표시하게 설정,투명하게 조절
                    super.paintComponent(g);
                }
         };
         setContentPane(basePanel); //background를 contentpane으로 사용.-> add 대신 background.add로.
         
         //추가 로고 띄우고 그 밑에 프로그램 이름 띄우기.
         ImageIcon ic=new ImageIcon("C:\\img\\msg2.png"); 
         JLabel imageL=new JLabel(ic); 
         JLabel textL=new JLabel("         simple messenger         ");   
         textL.setFont(new Font("Serif", Font.BOLD, 30));
         imageL.setSize(500, 150);
         textL.setSize(500, 50);
         textL.setHorizontalAlignment(JLabel.CENTER);
         northPanel.add(imageL);
         northPanel.add(textL);         
         
         /* Panel 크기 작업 */
         centerPanel.setPreferredSize(new Dimension(260, 200));
         westPanel.setPreferredSize(new Dimension(500, 270));
         southPanel.setPreferredSize(new Dimension(290, 70));
         northPanel.setPreferredSize(new Dimension(500,200));
         
         /* Label 크기 작업 */
         idL.setPreferredSize(new Dimension(80, 30));
         pwL.setPreferredSize(new Dimension(80, 30));
         nameL.setPreferredSize(new Dimension(80, 30));
         nicknameL.setPreferredSize(new Dimension(80, 30));
         emailL.setPreferredSize(new Dimension(80, 30));
         birthdayL.setPreferredSize(new Dimension(80, 30));
                
         /* TextField 크기 작업 */
         idF.setPreferredSize(new Dimension(150, 30));
         pwF.setPreferredSize(new Dimension(150, 30));
         nameF.setPreferredSize(new Dimension(150, 30));
         nicknameF.setPreferredSize(new Dimension(150, 30));
         emailF.setPreferredSize(new Dimension(150, 30));
         birthdayF.setPreferredSize(new Dimension(150, 30));

         
         /* Button 크기 작업 */
         findBtn.setPreferredSize(new Dimension(150,25));
         joinBtn.setPreferredSize(new Dimension(135, 25));
         exitBtn.setPreferredSize(new Dimension(135, 25));
         
         /* Panel 추가 작업 */
         westPanel.setBackground(new Color(255, 0, 0, 0));
         eastPanel.setBackground(new Color(255, 0, 0, 0));
         southPanel.setBackground(new Color(255, 0, 0, 0));
         northPanel.setBackground(new Color(255, 0, 0, 0));
         
         //패널에 컴포넌트 추가
         basePanel.add(northPanel,BorderLayout.NORTH);
         basePanel.add(centerPanel, BorderLayout.CENTER);
         basePanel.add(southPanel, BorderLayout.SOUTH);
         centerPanel.add(westPanel, BorderLayout.WEST);
         
         //패널 레이아웃
         westPanel.setLayout(new FlowLayout(FlowLayout.CENTER,70,10));
         southPanel.setLayout(new FlowLayout());
         
         /* westPanel 컴포넌트 */
         westPanel.add(idL);
         westPanel.add(idF);
         westPanel.add(pwL);
         westPanel.add(pwF);
         westPanel.add(loginBtn);
         loginBtn.setPreferredSize(new Dimension(245, 40));
         
         /* southPanel 컴포넌트 */
         southPanel.add(exitBtn);
         southPanel.add(joinBtn);
         southPanel.add(findBtn);
         
         /* Button 이벤트 리스너 추가 */
         loginBtn.addActionListener(this);
         exitBtn.addActionListener(this);
         joinBtn.addActionListener(this);
         findBtn.addActionListener(this);
         
         setSize(500, 600);
         setLocation(555, 200);
         setVisible(true);
         setResizable(false);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
     }
   
   
   
   public static String Salt() {
      
      String salt="";
      try {
         SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
         byte[] bytes = new byte[16];
         random.nextBytes(bytes);
         salt = new String(Base64.getEncoder().encode(bytes));
         
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      return salt;
   }

   public static String SHA512(String password, String hash) {
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
   
   
   public void actionPerformed(ActionEvent e) 
   {
      try 
      {
         oos = new ObjectOutputStream(socket.getOutputStream());
         ois = new ObjectInputStream(socket.getInputStream());
         
         if (e.getSource() == loginBtn)
         {
            String Id = idF.getText().trim();
            String Pw = (new String(pwF.getPassword())).trim();
            
            //Data 인스턴스 생성
            Data d = new Data(Id,Pw);
            d.request=2;
            
            //d 서버로 보냄
            oos.writeObject(d);
            d = (Data)ois.readObject();
            
            //로그인 성공
            if(d.check == 1) 
            { 
               u = (user)ois.readObject();
               Client_mainGUI cm =new Client_mainGUI(socket.getInetAddress(),socket.getPort(),u,d);
               cm.start();
               JFrame jf = new JFrame();
               setVisible(false);
            }else
            {
               //로그인 실패
               JOptionPane.showMessageDialog(null,d.Message); 
               setVisible(false);
               socket = new Socket(ip,portNum);
               new LoginGUI(socket,ip,portNum);   
               
            }
            //id 찾기
         }else if(e.getSource() == findBtn) 
         {
            //gui 세팅
            setVisible(false);
            westPanel.remove(loginBtn);            
            westPanel.remove(idL);
            westPanel.remove(idF);           
            westPanel.remove(pwL);
            westPanel.remove(pwF);
            westPanel.remove(loginBtn);
            southPanel.remove(findBtn);
            southPanel.remove(exitBtn);
            southPanel.remove(joinBtn);          
            westPanel.add(nameL);
            westPanel.add(nameF);           
            westPanel.add(emailL);
            westPanel.add(emailF);
            westPanel.add(birthdayL);
            westPanel.add(birthdayF);
  
            JButton findIdBtn = new JButton("id 찾기");
            southPanel.add(findIdBtn);  
            setVisible(true);
             
            //id 찾기 버튼을 눌렀을때 action listener
            findIdBtn.addActionListener(new ActionListener() 
            {
            	@Override
            	public void actionPerformed(ActionEvent e)
            	{   
	                //Data 생성
	                Data d = new Data();
	                d.name=nameF.getText();
	                d.email=emailF.getText();
	                d.birthday=birthdayF.getText();
	                //id 찾기 요청 의미 
	                d.request=3;
                try 
                {
                   // Data 송신 & 수신
                   oos.writeObject(d);
                   d = (Data)ois.readObject();               
                } catch (IOException | ClassNotFoundException e1) 
                {
                   e1.printStackTrace();
                }
                
                //id 찾기 성공
                if(d.check == 1) 
                {
                   //id 표시
                    JOptionPane.showMessageDialog(null, "id :" + d.id);
                   
                    //gui 세팅
                    setVisible(false);
                    westPanel.add(loginBtn);
                    southPanel.remove(findIdBtn);                           
                    westPanel.remove(nameL);
                    westPanel.remove(emailL);
                    westPanel.remove(birthdayL);
                    westPanel.remove(nameF);
                    westPanel.remove(emailF);
                    westPanel.remove(birthdayF);                         
                    westPanel.add(idL);
                    westPanel.add(idF);                  
                    emailF.setText("");
                    birthdayF.setText("");
                    nameF.setText("");
                    idF.setText("");                       
                    westPanel.add(pwL);
                    westPanel.add(pwF);
                    westPanel.add(loginBtn);
                    southPanel.add(exitBtn);
                    southPanel.add(joinBtn);
                    southPanel.add(findBtn);
                    pwF.setText("");
                    westPanel.setLayout(new FlowLayout(FlowLayout.CENTER,70,10));
                    southPanel.setLayout(new FlowLayout());
                    setVisible(true);   
                }else
                {
                   //서버에서 id찾기 실패시 
                   JOptionPane.showMessageDialog(null, "해당하는 정보의 id가 없습니다");
                }
             }
             });
           
             socket = new Socket(ip,portNum);
         } 
         //회원가입
         else if(e.getSource() == joinBtn)
         {
            //gui 세팅
             setVisible(false);
            westPanel.remove(loginBtn);         
             westPanel.add(nameL);
             westPanel.add(nameF);
             westPanel.add(nicknameL);
             westPanel.add(nicknameF);
             westPanel.add(emailL);
             westPanel.add(emailF);
             westPanel.add(birthdayL);
             westPanel.add(birthdayF);
             southPanel.remove(exitBtn);
             southPanel.remove(joinBtn);
             southPanel.remove(findBtn);  
             
             JButton joinButton = new JButton("회원가입 완료");
             southPanel.add(joinButton);   
             setVisible(true);
             
             //회원가입 완료 버튼을 누르면 서버로 회원 정보 전송
            joinButton.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               boolean success=true;
               
               id=idF.getText();
               pw=new String(pwF.getPassword());         
               name=nameF.getText();
               nickname=nicknameF.getText();
               email=emailF.getText();
               birthday=birthdayF.getText();
               
                //id 길이 0과 26사이 pw 길이 0과 16사이
                   if(id.length()==0 || pw.length()==0 ||id.length()>26||pw.length()>16)
                       success=false;
                                       
                   //무조건 pw에 대문자 하나 이상 특수문자 하나이상 있어야 함
                   Boolean hasCapital = false,hasSpecialChar = false;
                   for(int i=0;i < pw.length();i++)
                   {
                      if( '!' <= pw.charAt(i) && pw.charAt(i)<'/' || pw.charAt(i) == '?' ) 
                      {
                         hasSpecialChar = true;
                      }
                      if(pw.charAt(i) >= 'A'&&pw.charAt(i) <= 'Z') {
                         hasCapital =true;
                           }
                   }
                   if( (hasCapital && hasSpecialChar) == false) 
                      success=false;                  
                  
                   //위의 id pw 조건을 만족하지 못함. id pw 다시 입력
                   if(!success) 
                   {
                  JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 다시 확인하세요.");
                   }
                   else 
                   {
                     //id pw 조건 만족. 서버로 전송
                     //비밀번호 보내기전 암호화
                     String salt = Salt();
                     pw=SHA512(pw,salt); 
                  Data d = new Data(id,pw,name,nickname,email,birthday);      
                  d.salt=salt;
                  d.request=1;
                  
                  try {
                     oos.writeObject(d);
                     d = (Data)ois.readObject();               
                  } catch (IOException | ClassNotFoundException e1) 
                  {
                     e1.printStackTrace();
                  }
                  
                  //회원가입 성공
                  if(d.check == 1) 
                  {
                     //gui 
                     JOptionPane.showMessageDialog(null, "회원가입 성공");
                     westPanel.add(loginBtn);
                        setVisible(false);   
                        southPanel.remove(exitBtn);
                        southPanel.remove(joinBtn);
                        southPanel.remove(findBtn);                        
                        westPanel.remove(nameL);
                        westPanel.remove(nicknameL);
                        westPanel.remove(emailL);
                        westPanel.remove(birthdayL);
                        westPanel.remove(nameF);
                        westPanel.remove(nicknameF);
                        westPanel.remove(emailF);
                        westPanel.remove(birthdayF);                      
                        southPanel.remove(joinButton);                            
                        southPanel.add(exitBtn);
                        southPanel.add(joinBtn);
                        southPanel.add(findBtn);
                        idF.setText("");
                        pwF.setText("");
                        setVisible(true);                         
                  }else
                  {
                     //회원가입 실패
                     JOptionPane.showMessageDialog(null, "회원가입 실패(id 중복)");
                     setVisible(false);
                        new LoginGUI(socket,ip,portNum);   
                  }
               }
            }
            });
            socket = new Socket(ip,portNum);
         }else if(e.getSource()==exitBtn) 
         {
            //종료
            System.exit(0);
         }
      } catch (Exception a) 
      {
        a.printStackTrace();
         JOptionPane.showMessageDialog(null, "Error in signing up");
      }
   }
}


class searchFrame extends JFrame
{
   private static final long serialVersionUID = 1L;

   static String myuserid="not set";
   Client_Back CB;
   searchFrame sf;
   Data d;
   
   public searchFrame(Client_Back CB,ArrayList<String> searchList,Data d) throws Exception 
   {
      super("사용자 검색");
      this.CB=CB;
      this.d=d;
      this.setSize(500, 500);
      //this.setLocationRelativeTo(null);
      this.setLocation(550,90);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      sf=this;
      
      Server_Back sb1=new Server_Back();   
      try {
         
         sb1.connectDatabase(); //데이터베이스에 연결
         sb1.updateUser();
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      
      ImageIcon ic2=new ImageIcon("C:\\img\\bg.png"); //12-12추가

      JPanel basePanel = new JPanel() 
      {
          public void paintComponent(Graphics g) {
              g.drawImage(ic2.getImage(), 0, 0, null);
             
              setOpaque(false); //그림을 표시하게 설정,투명하게 조절
              super.paintComponent(g);
          }
      };

      setContentPane(basePanel); //1212add
      basePanel.setLayout(null);
      
      
      DefaultListModel<String> dm = new DefaultListModel<String>();
      JList<String> UserList;
      JPopupMenu popupMenu;
   
      
      for (int i = 0; i < searchList.size(); i++)      
         dm.addElement(searchList.get(i));
      
      UserList = new JList<String>(dm);   //userlist에 defaultListModel dm 넣기
      UserList.setBackground(Color.white);
      UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      //팝업 메뉴 만들기
      popupMenu = new JPopupMenu();
      JMenuItem assign =new JMenuItem("친구 등록");
      JMenuItem info = new JMenuItem("상세 정보");
      popupMenu.add(assign);
      popupMenu.add(info);
         
      
      //우클릭시 팝업 메뉴 보여주기
      UserList.addMouseListener(new MouseAdapter() 
      {
    	  public void mouseClicked(MouseEvent me) 
    	  {           
    		  // if right mouse button clicked 
    		  if (SwingUtilities.isRightMouseButton(me)&& !UserList.isSelectionEmpty()&& UserList.locationToIndex(me.getPoint())== UserList.getSelectedIndex()) 
    		  {
    			  int findmyvalue=-1;
    			  for(int j=0;j<sb1.UserList.size();j++) //추가
    			  {
    				  if(sb1.UserList.get(j).id.equals(myuserid))//유저리스트에서 내유저의 j값찾기.
    				  {
    					  findmyvalue=j;
    					  break;
                         }
                      }
    			  int i=UserList.getSelectedIndex();    //12-11추가   
    			  user u = null;
    			  String id=UserList.getSelectedValue(); //12-11추가 클릭된유저의 id를 저장
    			  int findwhere=-1; //12-11추가
    			  for(int j=0;j<sb1.UserList.size();j++) //추가
    			  {
    				  if(sb1.UserList.get(j).id.equals(id))//유저리스트에서 해당유저의i찾기.
    				  {
    					  u=sb1.UserList.get(j);
    					  findwhere=j;
    					  break;
    				  }
    			  }
                             
    			  if(findmyvalue!=-1 && findwhere!=-1) //add
    			  {
    				  //list를 마우스 우클릭할시에 클릭된 사람의 정보 얻어서 클릭된 사람에 대한 리스너 생성.
    				  assign.addActionListener(new friendRegisterListener(sf,CB,findmyvalue, findwhere));
    				  showInfo si=new showInfo(u,CB,d);
    				  info.removeActionListener(si);
    				  info.addActionListener(si);
    				  popupMenu.show(UserList, me.getX(), me.getY());
    				  popupMenu.setSelectionModel(popupMenu.getSelectionModel());
    			  }
    		  }
    	  }
      });

      UserList.setBounds(100,50,300,400);
      basePanel.add(UserList);
      
      this.setVisible(true);
   }
}
class Client_mainGUI extends Thread implements ActionListener, KeyListener 
{
   private static final long serialVersionUID = 1L;
   JFrame jf = new JFrame();
   Client_Back CB = new Client_Back();
   JPanel ClientGUIPanel = new JPanel(null);
   JPanel northPanel = new JPanel(null); 
   ImageIcon ic2=new ImageIcon("C:\\img\\bg.png");  
   JPanel southPanel; 
   JLabel UserLabel = new JLabel("friends");
   JList<String> UserList;
   JTextArea UserInfo = new JTextArea(10,5);
   JTextField searchField = new JTextField(20);
   JButton searchBtn = new JButton("검색");
   JButton changePwBtn = new JButton("암호 변경");
   JButton deleteBtn = new JButton("탈퇴");
   InetAddress ip = null;
   int portNum;
   user u;
   DefaultListModel<String> dm = new DefaultListModel<String>();
   JPopupMenu popupMenu;
   Data d;
   JPanel basePanel;
   Server_Back sb1;
   JMenuItem jm1,jm2,jm3,jm4;
   JFrame chatFrame;
   JFrame mainFrame;
   sendFile sf;
   JTextArea area;

   public void run() 
   {
	   
		 try {
			 sb1=new Server_Back();   
			 sb1.connectDatabase();
			 sb1.updateUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	      
	   //우클릭시 팝업 메뉴 보여주기
	      UserList.addMouseListener(new MouseAdapter()
	      {
	            public void mouseClicked(MouseEvent me) 
	            {
	              
	              // if right mouse button clicked (or me.isPopupTrigger())
	              if (SwingUtilities.isRightMouseButton(me)&& !UserList.isSelectionEmpty() && UserList.locationToIndex(me.getPoint())== UserList.getSelectedIndex()) 
	              {
	            	  user user = null;
	            	  String id=UserList.getSelectedValue().replace("online","").trim();
	                  popupMenu.show(UserList, me.getX(), me.getY());            

	                  System.out.println("size "+sb1.UserList.size());
	                  for(int j=0;j<sb1.UserList.size();j++) //추가
	                    {	                 
	                	  System.out.println(sb1.UserList.get(j).id);

	                       if(sb1.UserList.get(j).id.equals(id))//유저리스트에서 해당유저의i찾기.
	                       {
	                          user=sb1.UserList.get(j);	          
	                         break;
	                       }
	                    }
	                  
	                  //친구정보보기 메뉴를 클릭했을때
	                  jm1.addActionListener(new showInfo(user,CB,d));
	                  jm2.addActionListener(new Chat(u,user,CB));
	                  jm3.addActionListener(new sendFile(u,user,CB));
	              }
	              
	            }
	               }
	            );
	      
	      
	      
	      UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   }
   
   
   public Client_mainGUI(InetAddress ip, int portNum,user u,Data d) throws Exception
   {
	  jf.setTitle("메인 페이지");
	  mainFrame=jf;
	  mainFrame.addWindowListener(new WindowAdapter() 
 		{
 			public void windowClosing(WindowEvent e)
 			{
 				try {
					CB.socket.close();
					
 				} catch (IOException e1)
 				{
					e1.printStackTrace();
				}
 			}		
 		});
	  
	  this.ip = ip;
      this.portNum=portNum;
      this.u=u;
      this.d=d;
      
      basePanel = new JPanel()
      {
          public void paintComponent(Graphics g) {
              g.drawImage(ic2.getImage(), 0, 0, null);
             
              setOpaque(false); //그림을 표시하게 설정,투명하게 조절
              super.paintComponent(g);
          }
      };

      jf.setContentPane(basePanel);
      basePanel.setLayout(null);

      
      jf.setLocation(355,10);
      jf.setSize(800, 800);
      jf.setResizable(false);
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      northPanel.setBackground(new Color(255,0,0,0));
      ClientGUIPanel.setBackground(new Color(255,0,0,0));
      ClientGUIPanel.setSize(400,400);
      searchBtn.addActionListener(this);
      changePwBtn.addActionListener(this);
      deleteBtn.addActionListener(this);
      
      searchFrame.myuserid=u.id; //12-11추가.
      //user info
      UserInfo.append("ID : "+u.id +"(Avaliable)");
      
      northPanel.setBounds(0,0,800,200);
      ClientGUIPanel.setBounds(0,200,800,300);
      UserInfo.setBackground(new Color(255, 0, 0, 0)); //1212add
      UserInfo.setBounds(0,20,150,30);   //1212change
      searchBtn.setBounds(200,20,80,30); //1212change
      searchField.setBounds(300,20,200,30); //1212change
      changePwBtn.setBounds(500,20,100,30); //1212change
      deleteBtn.setBounds(620,20,80,30); //1212change

      
      northPanel.add(UserInfo);
      northPanel.add(searchBtn);
      northPanel.add(searchField);
      northPanel.add(changePwBtn);
      northPanel.add(deleteBtn);
      UserLabel.setBounds(375,0,50,20); //1212change
      ClientGUIPanel.add(UserLabel);
      basePanel.add(ClientGUIPanel); //1212 변경
      basePanel.add(northPanel); //1212변경
  

      //공공데이터 southPanel
      ApiExplorer ae=new ApiExplorer();  
      String composite = ae.getForsbandcovidstr();
      
      String[] composite2=composite.split("///"); //composite2[0]= forSb, [1]=covidstr

      String strr=composite2[0];
      String covidstr=composite2[1];     

      JPanel cjp=new JPanel();      
      //setTitle("공공데이터를 활용해보자");
      
      cjp.setLayout(null);
      JLabel label1 = new JLabel("오늘의 기상특보");
      // label1.setBounds();
      label1.setBounds(0, 500, 400, 50);
      label1.setBorder(BorderFactory.createLineBorder(Color.black, 1));//1212add
      label1.setHorizontalAlignment(JLabel.CENTER); //글자 중앙정렬.
      basePanel.add(label1); //1212변경

      JLabel label3 = new JLabel("오늘의 코로나 정보");      
      label3.setBounds(400, 500, 400, 50); //1212change
      label3.setHorizontalAlignment(JLabel.CENTER);
      label3.setBorder(BorderFactory.createLineBorder(Color.black, 1));//1212add
      basePanel.add(label3); 

      String[] strry=strr.split("\n");

      TextArea textarea = new TextArea(strry[1]+"\n\n"+strry[0], 10, 10, TextArea.SCROLLBARS_VERTICAL_ONLY);
      textarea.setBounds(0, 550, 400, 250); //1212add
      textarea.setBackground(Color.white);  //12-12add
      textarea.setEditable(false);
      /*
      JScrollPane scrollPane = new JScrollPane(textarea);
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBounds(0, 100, 400, 200);
      */
      basePanel.add(textarea); 

      String[] strry2=covidstr.split(" ");  
         
      //    JPanel coronaP=new JPanel();
      //    coronaP.setLayout(null);
      //    coronaP.setBorder(BorderFactory.createLineBorder(Color.black, 5));
      //    coronaP.setBackground(new Color(255, 0, 0, 0));
      //    coronaP.setBounds(400, 550, 400, 250);
      //    basePanel.add(coronaP);
      
      String[] savedKey= {"    데이터 조회일시:", "    일일확진:", "    일일사망:"}; 
      for(int i=0;i<strry2.length;i++)
      {
         JLabel label2=new JLabel(savedKey[i]+strry2[i]);
         label2.setBounds(400, 550+50*i, 400, 50);        
         basePanel.add(label2); //1212변경.
      }
      
      JButton coronaButton=new JButton("구체적인 코로나 정보 확인"); 
      coronaButton.setHorizontalAlignment(SwingConstants.CENTER); 
      coronaButton.setVerticalAlignment(SwingConstants.CENTER); 
      coronaButton.setBounds(400, 700, 400, 100); 
      //코로나 정보 url 열기
      coronaButton.addActionListener(new ActionListener() 
      {
         public void actionPerformed(ActionEvent e) {
            
            if (Desktop.isDesktopSupported()) {
                   Desktop desktop = Desktop.getDesktop();
                   try {
                       URI uri = new URI("https://ncov.kdca.go.kr/");
                       desktop.browse(uri);
                   } catch (IOException ex) {
                       ex.printStackTrace();
                   } catch (URISyntaxException ex) {
                       ex.printStackTrace();
                   }
           }           
         }         
      }); 
      basePanel.add(coronaButton);
      jf.setVisible(false);
      AppendUserList(u.friendList);
      jf.setVisible(true);

      CB.setGui(this);
      CB.getUserInfo(ip,portNum);
      CB.start(); // 채팅창이 켜짐과 동시에 접속을 실행해줍니다.
   }

   public void showSearchFrame(ArrayList<String> searchList) throws Exception 
   {
      new searchFrame(CB,searchList,d);
   }
   
   //클라이언트에게 컨펌창 띄움
   public int confirm(JFrame jf,String message) 
   {
      int result = JOptionPane.showConfirmDialog(jf, message ,"확인창",JOptionPane.YES_NO_OPTION);
      return result;      //yes 0 no 1
   }
   
   
   //메세지창 띄움
   public void showMessage(JFrame jf,String message)
   {
      JOptionPane.showMessageDialog(jf, message);
   }
   
   
   //암호화
   public static String SHA512(String password, String hash) {
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
   
   // 액션
   public void actionPerformed(ActionEvent e) 
   { 
      //검색 버튼을 누르면 사용자 검색
      if(e.getSource()==searchBtn) 
      {
         try 
         {
          System.out.println("search button");
          d.request=8;
          d.Message=searchField.getText().trim();
          
          Data newData =  new Data();
          newData.Message=d.Message;
          newData.id=d.id;
          newData.pw=d.pw;
          newData.name=d.name;
          newData.nickname=d.nickname;
          newData.birthday=d.birthday;
          newData.email=d.email;
          newData.request=8;
          CB.Transmit(newData);
         } 
         catch (IOException e1)
         {
         e1.printStackTrace();
         }
           
      }else if(e.getSource()==changePwBtn) //비밀번호 변경
      {
       
       try {
            String m = JOptionPane.showInputDialog("새로운 비밀번호를 입력하세요");
            
             d.pw = m.trim();
             d.request=7;
             d.pw=SHA512(d.pw,d.salt);
             Data newData =  new Data();
             newData.id=d.id;
             newData.pw=d.pw;
             newData.name=d.name;
             newData.nickname=d.nickname;
             newData.birthday=d.birthday;
             newData.email=d.email;
             newData.request=7;
             CB.Transmit(newData);
      } 
       catch (IOException e1) 
       {
         e1.printStackTrace();
      }
       
         
      }else if(e.getSource()==deleteBtn)   //탈퇴 
      {
         try 
         {
        	 
        	 
         d.request=5;
         Data newData =  new Data();
         newData.id=d.id;
         newData.pw=d.pw;
         newData.name=d.name;
         newData.nickname=d.nickname;
         newData.birthday=d.birthday;
         newData.email=d.email;
         newData.request=5;
         CB.Transmit(newData);
         JOptionPane.showMessageDialog(jf, "탈퇴 완료");
         System.exit(0);
         } catch (IOException e1) {
         e1.printStackTrace();
      }
      }
     
   }
   
   
   //키보드 액션
   public void keyPressed(KeyEvent e) 
   { 
  
   }

  
   // 유저목록을 유저리스트에 띄워줍니다.
   public void AppendUserList(ArrayList<user> FriendList) throws Exception
   {
     if(dm!=null) 
        dm.removeAllElements();
     
     
     String id;
      for (int i = 0; i < FriendList.size(); i++)
      { 
         id = FriendList.get(i).id;
         if(FriendList.get(i).connection == true)  
             dm.addElement(id+" online");   //online
         else 
             dm.addElement(id+" offline"); //offline
       }
      
      UserList = new JList<String>(dm);   //userlist에 defaultListModel dm 넣기
      JScrollPane p=new JScrollPane(UserList); //1212추가 userlist에 스크롤바 추가.
      UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      p.setBackground(Color.white);
      p.setBounds(200,20,400,300);
      
      TitledBorder tb=new TitledBorder(new LineBorder(Color.black));//1212add
      UserList.setBorder(tb); //1212add
      
      
      //팝업 메뉴 만들기
      popupMenu = new JPopupMenu();
      popupMenu.setSelectionModel(popupMenu.getSelectionModel());
      jm1= new JMenuItem("친구 정보 보기");
      jm2= new JMenuItem("1:1 채팅하기");
      jm3= new JMenuItem("파일 보내기");
      jm4= new JMenuItem("1:1 게임 시작하기"); 

      popupMenu.add(jm1);
      popupMenu.add(jm2);
      popupMenu.add(jm3);
      popupMenu.add(jm4);
    
      ClientGUIPanel.add(p);
   }
   

   public void check(MouseEvent e) {
       if (e.isPopupTrigger()) { //if the event shows the menu
           UserList.setSelectedIndex(UserList.locationToIndex(e.getPoint())); //select the item
           popupMenu.show(UserList, e.getX(), e.getY()); //and show the menu
       }
   }

   public void keyTyped(KeyEvent e) {
   }

   public void keyReleased(KeyEvent e) {
   }
   
   public Chat getChat(user me,user opponent,Client_Back CB)
   {
	return new Chat(me,opponent,CB);
	   
   }
   
   
   //파일 보내기 클래스
   class sendFile extends Thread implements ActionListener
   {
	   user me;
	   user opponent;
	   Client_Back cb;
	   JFileChooser fc;
	   File file;
	   
	   public sendFile(user me,user opponent,Client_Back cb) 
	   {
		   this.me=me;
		   this.opponent=opponent;
		   this.cb=cb;
           sf=this;
	   }

		public void actionPerformed(ActionEvent e) 
		{
			 //먼저 파일을 고르고
			   fc = new JFileChooser();
	           fc.setCurrentDirectory(new java.io.File(""));
	           FileNameExtensionFilter ff = new FileNameExtensionFilter("* image","jpg","png");
	           fc.setFileFilter(ff);
	           if(fc.showOpenDialog(null)==fc.APPROVE_OPTION) 
	           {
	        	    file = fc.getSelectedFile();
	           }
	           //수신자에게 파일 제목을 받을거냐고 물어봄 Data 객체의 Message가 파일 이름.
	           Data newData =  new Data();
	           newData.Message=file.getName();
	           newData.opponent=opponent;
	           newData.id=me.id;
	           //파일 보내기 요청
	           newData.request=14;
	           newData.check=0;
	           try {
				cb.Transmit(newData);
	           } catch (IOException e1) 
	           {
				e1.printStackTrace();
	           }      
		}
		
		public void send() 
		{
			Data newData =  new Data();
	        newData.Message=file.getName();
	        newData.opponent=opponent;
	        newData.id=me.id;
	        //실제로 파일 보내기
	        newData.request=15;
	        newData.file=file;
	        try 
	        {
	        	cb.Transmit(newData);
	        } catch (IOException e1) 
	        {
	        	e1.printStackTrace();
	        }	   
		}
  }
   
   //채팅 클래스
   class Chat extends Thread implements ActionListener
   {
   	user opponent;
   	user me;
   	Client_Back CB;
   	JButton send ;
   	JTextField sendF;
   	JFrame jf;
   	public Chat(user me,user opponent,Client_Back CB) 
   	{
   		jf=new JFrame("채팅창");
   		chatFrame=jf;
   		this.me=me;
   		this.opponent=opponent;
   		this.CB=CB;
   			
   		JPanel basePanel = new JPanel(null);
   		area = new JTextArea();
   		area.setBounds(0,0,500,500);
   		area.setBackground(Color.white);
   		send = new JButton("전송");
   		sendF = new JTextField();
   		send.setBounds(400,500,80,50);
   		sendF.setBounds(0,500,400,50);
   		basePanel.add(send);
   		basePanel.add(sendF);
   		basePanel.add(area);
   		jf.setContentPane(basePanel);
   		jf.setSize(500, 580);
   		jf.setLocation(555, 200);
   		jf.setResizable(false);   		
   		//종료. 한명이 나가면 채팅 해제
   		jf.addWindowListener(new WindowAdapter() 
   		{
   			public void windowClosing(WindowEvent e)
   			{
   				jf.dispose();
   				Data newData = new Data();
   				newData.request=13;
   				newData.check=-1;
   				newData.id=me.id;
				newData.opponent=opponent;
   				try {
   					CB.Transmit(newData);
   				} catch (IOException e1) {
   					e1.printStackTrace();
   				} 
   			}		
   		});
   		
   		send.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) 
			{
				Data newData = new Data();
				newData.opponent=opponent;
				newData.id=me.id;
				newData.check=1;
				newData.request=13;
				newData.Message=sendF.getText();
				sendF.setText("");
				area.append(me.id+": "+newData.Message+"\n");
				try {
					CB.Transmit(newData);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
   			
   		});
   	}
   	
   	public Chat() 
   	{
   		jf=new JFrame("채팅창");
   		chatFrame=jf;
   	}
   	@Override
   	public void actionPerformed(ActionEvent e)
   	{
   		jf.setVisible(true);
   		Data newData = new Data();
   		newData.request=12;
   		newData.check=0;
   		newData.id=me.id;
   		newData.friendList=me.friendList;
   		newData.Message=opponent.id;
   		try {
   			CB.Transmit(newData);
   		} catch (IOException e1) {
   			e1.printStackTrace();
   		}
   		
   	}	
   }
}


//정보 보기 메뉴의 actionListenr
class showInfo implements ActionListener
{
   user u;
   Client_Back cb;
   Data d;
   
   public showInfo(user u,Client_Back cb,Data d) 
   {
      this.d=d;
      this.u=u;
      this.cb=cb;
   }
   
   public void actionPerformed(ActionEvent e)
   {
      JFrame jf = new JFrame("사용자 정보");
      JPanel mainPanel = new JPanel(null);
      JPanel imagePanel = new JPanel(null);
      jf.setLocationRelativeTo(null);
      jf.setSize(400, 300);
      jf.setResizable(false);
      jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     
      imagePanel.setBounds(150, 100, 100, 100);
      mainPanel.setBackground(Color.white);
      mainPanel.setBounds(0, 0, 400, 300);
      jf.add(imagePanel);
      jf.add(mainPanel);
      
     
      //프로필 버튼
      JButton profileImage = new JButton();
      //프로필 이미지 서버에서 가져오기
      //Data newData = new Data();
      //newData.request=11;
      //cb.Transmit(newData);
      
      
      profileImage.setBounds(0,0,100,100);
      imagePanel.add(profileImage);
      
      
      //프로필 이미지 변경
      profileImage.addActionListener(new ActionListener() 
      {

		@Override
		public void actionPerformed(ActionEvent e) 
		{
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new java.io.File(""));
            FileNameExtensionFilter ff = new FileNameExtensionFilter("* image","jpg","png");
            fc.setFileFilter(ff);
            if(fc.showOpenDialog(null)==fc.APPROVE_OPTION) 
            {
               //내 gui에서 프로필 변경
               File file = fc.getSelectedFile();
               Icon icon = new ImageIcon(file.getAbsolutePath());
               profileImage.setIcon(icon);
               
               //image 파일 서버로 전송
               Data newData = new Data();
               d.request=11;
               d.file=file;
               
            }	
		}
      });
      
      try 
      {
    	  //오늘의 메세지 업데이트
         Data newData =  new Data();
         newData.Message=d.Message;
         newData.id=d.id;
         newData.pw=d.pw;
         newData.name=d.name;
         newData.nickname=d.nickname;
         newData.birthday=d.birthday;
         newData.email=d.email;
         newData.request=10;
         System.out.println("today message update");
         cb.Transmit(newData);
      } catch (IOException e2) 
      {
         e2.printStackTrace();
      }
      
      JButton todayMessageBtn = new JButton();
      if(u.todayMessage!=null)         
         todayMessageBtn.setText(u.todayMessage);
      todayMessageBtn.setBounds(100,240,200,20);
      //오늘의 메세지 변경
      todayMessageBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e) 
         {
            try {
               String newTodayMessage=JOptionPane.showInputDialog(jf,"오늘의 메세지");
               //받은 새 오늘의 메세지 서버로 전송 & 서버에서는 오늘의 메세지 update
               d.request=10;
               d.Message=newTodayMessage;
               Data newData =  new Data();
               newData.Message=d.Message;
               newData.id=d.id;
               newData.pw=d.pw;
               newData.name=d.name;
               newData.nickname=d.nickname;
               newData.birthday=d.birthday;
               newData.email=d.email;
               newData.request=d.request;
               cb.Transmit(newData);
               todayMessageBtn.setText(newTodayMessage);
            } catch (IOException e1) 
            {
               e1.printStackTrace();
            }         
         }
      }
      );
      JTextArea nameArea = new JTextArea(u.id);
      nameArea.setBounds(180,200,50,20);
      mainPanel.add(nameArea);
      mainPanel.add(todayMessageBtn);
      jf.setVisible(true);

   }  
}




//친구등록 버튼에 대해서 friendRegisterListener 실행.
//친구등록 버튼 생성할때 (UserList.get(i)로 나를 찾을 i값, UserList.get(i)로 대상을 찾을i값)으로 생성.
class friendRegisterListener implements ActionListener
{
   int mygeti; //userlist.get(i)로 나의 user를 찾을때의 i값.
   int opponentgeti; //대상을 get(i)로 찾을때의 i값.
   Client_Back CB;
   searchFrame sf;
   
   public friendRegisterListener(searchFrame sf,Client_Back CB,int mygeti, int opponentgeti)
   {   
      this.sf=sf;
      this.CB=CB;
      this.mygeti=mygeti;
      this.opponentgeti=opponentgeti;
   }
   
   public void actionPerformed(ActionEvent e)
   {
      user user1; //나의 user를 입력받음.
      user user2; //대상의 user를 입력받음.
         
      try 
      {
         Server_Back sb1 = new Server_Back();
         sb1.connectDatabase(); //데이터베이스에 연결 pstmt=serverback.con.
         sb1.updateUser();  //sb1.userlist에 모든 유저 정보 저장.
      
         user1=sb1.UserList.get(mygeti);
         user2=sb1.UserList.get(opponentgeti);

         friend fr=new friend(CB,user1, user2); //위에 있는 friend객체로 두개 보냄.
      }catch(Exception e1) {
         e1.printStackTrace();
      }
   }
}      
      
//친구등록 버튼을 누르면, 나의 User id, 대상의 User id를 전달함

//버튼을 누르면 friendregisterlistener(나의 i, 대상의i)로 리스너가 실행되고
//리스너에서 friend클래스 불러와서 Data형식으로 d.id에 나의 id, 
//d.friend_add에 대상의 id 넣어서 서버에 전달하면
//서버에서 d.id랑 d.friend_add 친구관계를 friend 테이블에 넣음.

class friend 
{
   int check=-1;  //서버에서 테이블에 넣어지면 check 0, friend 테이블에 이미 있어서 못넣으면 check=1.
   Client_Back cb;
   
   public friend(Client_Back cb,user user1, user user2)
   {
      this.cb=cb;   
      {
         try 
         {   
            Data newData =new Data(); 
            
            //user1(나)의 정보를 데이터에 저장.
            newData.id=user1.id;
            newData.pw=user1.pw;
            newData.name=user1.name;
            newData.nickname=user1.nickname;
            newData.birthday=user1.birthday;
            newData.email=user1.email;
            //친구의 id를 d.friendadd에 저장.
            newData.friend_add=user2.id;
            newData.request=6;
            
            cb.Transmit(newData);      
         }catch(IOException e)
         {
            e.printStackTrace();
         }         
      }      
   }
}




