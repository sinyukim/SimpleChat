import java.net.*;
import java.io.*;

public class ChatClient {

	public static void main(String[] args) {
		if(args.length != 2){ // 글자가 2개 더 안붙어잇으면
			System.out.println("Usage : java ChatClient <username> <server-ip>");  //사용법 알려줌
			System.exit(1);  //끝냄
		}
		Socket sock = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		boolean endflag = false;
		try{
			sock = new Socket(args[1], 10001);   //"null이엿던 sock -> Socker(args[1], 10001)"
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));  //" pw = 새로운 PrintWriter(byte단위를 char형태로씀(sock쓰기)"
			br = new BufferedReader(new InputStreamReader(sock.getInputStream())); // " br = buffering char-input stream(byte단위를 char형태로읽음(sock읽기)"
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); //"새BufferedReader keyboard = system에서 받은 값을 char로 전환 후 BufferedReader형태로"
			// send username.
			pw.println(args[0]); //thread 한테 보냄/////////////////////////////////////////
			pw.flush(); 
			InputThread it = new InputThread(sock, br);
			it.start();
			String line = null;
			while((line = keyboard.readLine()) != null){
				pw.println(line); //line을 pw에 보냄
				pw.flush();
				if(line.equals("/quit")){ // "/quit"이면
					endflag = true;
					break; //끝냄
				}
			}
			System.out.println("Connection closed.");
		}catch(Exception ex){
			if(!endflag)
				System.out.println(ex);
		}finally{
			try{
				if(pw != null)
					pw.close();
			}catch(Exception ex){}
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		} // finally
	} // main
} // class

class InputThread extends Thread{
	private Socket sock = null;
	private BufferedReader br = null;
	public InputThread(Socket sock, BufferedReader br){
		this.sock = sock;
		this.br = br;
	}
	public void run(){
		try{
			String line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
		}catch(Exception ex){
		}finally{
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // InputThread
}
