package org.jack.common.ssh2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileHandle;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class RemoteCommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(RemoteCommandUtils.class);
    private static String  DEFAULTCHART="UTF-8"; 

    /** 
     * 登录主机 
     * @return 
     *      登录成功返回true，否则返回false 
     */  
    public static Connection login(String ip,
                            String userName,
                            String userPwd){  

        boolean flg=false;
        Connection conn = null;
        try {  
            conn = new Connection(ip);  
            conn.connect();//连接  
            flg=conn.authenticateWithPassword(userName, userPwd);//认证  
            if(flg){
                logger.info("=========登录成功========="+conn);
                return conn;
            }
        } catch (IOException e) {  
        	logger.error("=========登录失败========="+e.getMessage());
            e.printStackTrace();  
        }  
        return conn;  
    }
    public static void uploadFile(File file,String remoteFile,Connection conn){
    	try {
			SFTPv3Client sftp=new SFTPv3Client(conn);
			SFTPv3FileHandle fileHandle=sftp.createFile(remoteFile);
			FileInputStream fin=new FileInputStream(file);
			BufferedInputStream bin=new BufferedInputStream(fin);
			byte[] bytes=new byte[1024];
			int size=0;
			while(true){
				int count=bin.read(bytes);
				if(count>0){
					sftp.write(fileHandle, size, bytes, 0, count);
					size+=count;
				}else if(count==-1){
					break;
				}
			}
			bin.close();
			sftp.closeFile(fileHandle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static String execute(Connection conn,String cmd) {
		try {
			Session session=conn.openSession();
			String result=execute(session, cmd);
			session.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    /** 
     * 远程执行shll脚本或者命令 
     * @param cmd 
     *      即将执行的命令 
     * @return 
     *      命令执行完后返回的结果值 
     */  
    public static String execute(Session session,String cmd){  
        String result="";  
        try {  
        	session.execCommand(cmd);
            result=processStdout(session.getStdout(),DEFAULTCHART);  
            //如果为得到标准输出为空，说明脚本执行出错了  
            if(StringUtils.isEmpty(result)){
            	logger.info("得到标准输出为空,执行的命令："+cmd);
                result=processStdout(session.getStderr(),DEFAULTCHART);  
            }else{
            	logger.info("执行命令成功,执行的命令："+cmd);
            }
        } catch (IOException e) {
            logger.info("执行命令失败,执行的命令："+cmd+"  "+e.getMessage());
            e.printStackTrace();  
        }  
        return result;  
    }
    /** 
     * 解析脚本执行返回的结果集 
     * @param in 输入流对象 
     * @param charset 编码 
     * @return 
     *       以纯文本的格式返回 
     */  
     private static String processStdout(InputStream in, String charset){  
         StringBuffer buffer = new StringBuffer();;  
         try {
             BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(in),charset));  
             String line=null;  
             while((line=br.readLine()) != null){  
                 buffer.append(line+"\n");  
             }
             br.close();
         } catch (UnsupportedEncodingException e) { 
             logger.error("解析脚本出错："+e.getMessage());
             e.printStackTrace();  
         } catch (IOException e) {
        	 logger.error("解析脚本出错："+e.getMessage());
             e.printStackTrace();  
         }  
         return buffer.toString();  
     }  
}
