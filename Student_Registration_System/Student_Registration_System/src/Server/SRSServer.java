/*
 * @author 雷浩洁
 * @version 1.0
 * 服务器实现代码
 *IP地址：本机IP（127.0.0.1），端口号:8888
 *用serversocket创建服务器，使用ExecutorService类创建线程池
 * 每收到一个来自客户端的连接，创建一个线程
 */
package Server;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;







public class SRSServer {
	private static int port=8888;  //服务器端口号
	public int clientNo;
	public ServerSocket serverSocket;
	public ExecutorService exec;
	public static int isRegistration_time;//当前系统是否处于正在注册的状态下，1为开放注册，0为关闭注册
	public static int isRegistration;//记录当前有多少人处于选课界面
	
	public static void main(String[] args) throws IOException {
		//创建服务器serversocket对象
		SRSServer srsServer = new SRSServer();
	}
	public SRSServer() throws IOException  {
		SRSServer.isRegistration=0;
		SRSServer.isRegistration_time=0;
        clientNo = 0;
        try {
			serverSocket = new ServerSocket(port);//创建一个服务器
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        // 创建线程池
        exec = Executors.newCachedThreadPool();
        while (true) {
            // 循环监听来自客户端的连接请求，并加入到线程池中
            Socket client = serverSocket.accept();
            clientNo++;
            exec.execute(new SingleServer(client, clientNo));
        }
	}
	
}



class SingleServer implements Runnable {
	/*对与server连接的每一个客户端，启动一个线程创建该对象，通过该对象执行针对当前客户端的一切操作
	 * */
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean isEnd;
    private int clientNo;
    private int idendity;//识别登录的客户端的类型，1为学生，2为教授，3为管理员
    
    //保存用户名与密码
    private String idString;
    private String pwString;
    
  //根据客户端身份的不同使用对应对象执行操作
    private Student student;
    private Professor professor;
    private Register register;
    
    private String request;//保存从客户端发来的请求码，格式为周华辉的协议标准
    
    public SingleServer(Socket socket, int clientNo) {
        this.socket = socket;
        this.clientNo = clientNo;
        this.idendity=0;
        this.isEnd=false;
        this.request=null;
        this.student = new Student(socket);
        this.professor = new Professor(socket);
        this.register = new Register(socket);
    }
    @Override
    public void run() {
    	//建立与客户端的IO流连接
    	try {
			dis = new DataInputStream(
			        new BufferedInputStream(socket.getInputStream()));
			dos = new DataOutputStream(
	                new BufferedOutputStream(socket.getOutputStream()));//输出流
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
    	do {
    		try {
                request=dis.readUTF();
                if((request.toCharArray())[0]=='1') {  //客户端身份为学生
                	idendity=1;
                	if(request.toCharArray()[1]=='0') {  //请求登录
                		idString = dis.readUTF();
                		pwString = dis.readUTF();
                		String result=student.login(idString, pwString);
                		dos.writeUTF(result);
                		dos.flush();
                	}
                	//补充：此处添加else if或是改成switch，补充完善学生角色的其他用例
                }else if(request.toCharArray()[0]=='2') {//客户端身份是教授
                	idendity=2;
                	if(request.toCharArray()[1]=='0') {  //请求登录
                		String id = dis.readUTF();
                		String pw = dis.readUTF();
                		String result=professor.login(id, pw);
                		dos.writeUTF(result);
                		dos.flush();
                	}
                	//补充：此处添加else if或是改成switch，补充完善教授角色的其他用例
                }else if(request.toCharArray()[0]=='3') {//客户端身份是注册员
                	idendity=3;
                	//补充：完善注册员角色的用例
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }finally {
            	try {
            		switch(idendity) {
            		case 1:student.close();break;
            		case 2:professor.close();break;
            		case 3:register.close();break;
            		default: break;
            		}
					socket.close();
					isEnd=true;
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
            }
    	}while(!isEnd);
    }
}


