package test1;

	public class Server_listener
	{
		
		Server_Back SB = new Server_Back();
		public Server_listener(int Port) 
		{	
				SB.Start_Server(Port);
				SB.start(); 
		}
		public static void main(String[] args) {
			new Server_listener(9999);
		}
	}

	

