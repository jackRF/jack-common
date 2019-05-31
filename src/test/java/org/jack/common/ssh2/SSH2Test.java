package org.jack.common.ssh2;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.BaseTest;
import org.jack.common.util.IOUtils;
import org.jack.common.util.PathPair;
import org.jack.common.util.Task;
import org.jack.common.util.net.ConnectionPair;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.Session;

public class SSH2Test extends BaseTest {
	protected void execute(ConnectionPair connectionPair,String cmd,Task<String> task){
		Connection connection=RemoteCommandUtils.login(connectionPair);
		try {
			Session session=RemoteCommandUtils.executeCmd(connection, cmd);
			IOUtils.processText(session.getStdout(), "UTF-8",task);
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected String execute(Connection connection,String cmd) {
		String stdout=RemoteCommandUtils.execute(connection, cmd);
		log(stdout);
		return stdout;
	}
	protected void compareUpdateDir(Connection connection,PathPair pathPair) {
		Map<String,Date> destMap=new HashMap<String,Date>();
		try {
			SFTPv3Client sftp=new SFTPv3Client(connection);
			List<SFTPv3DirectoryEntry> list=sftp.ls(pathPair.getDest());
			for(SFTPv3DirectoryEntry entry:list){
				if(entry.attributes.isDirectory()){
					continue;
				}
				Integer mtime=entry.attributes.mtime;
				if(mtime!=null){
					destMap.put(entry.filename, new Date(mtime.longValue()*1000));
				}
			}
			sftp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File sourceDir=new File(pathPair.getSource());
		File[] files=sourceDir.listFiles();
		Set<String> needUpdate=new HashSet<String>();
		for(File file:files){
			if(file.isDirectory()){
				continue;
			}
			String filename=file.getName();
			if(destMap.containsKey(filename)){
				if(file.lastModified()>destMap.get(filename).getTime()){
					needUpdate.add(filename);
				}
			}else{
				needUpdate.add(filename);
			}
		}
		if(!needUpdate.isEmpty()){
			RemoteCommandUtils.uploadFile(needUpdate, pathPair, connection);
		}
	}
	protected ProcessInfo grepParse(String stdout,String grep) {
		String[] lines=stdout.split("\n");
		Pattern pattern=Pattern.compile(grep);
		for(String input:lines){
			if(pattern.matcher(input).find()){
				return parse(input);
			}
		}
		return null;

	}
	protected ProcessInfo parse(String input) {
		String pidRegex="\\b(\\d+)\\b";
		String usernameRegex="^(\\w+)";
		String[] regexs={usernameRegex,pidRegex};
		String[] values=new String[regexs.length];
		int i=-1;
		for(String regex:regexs){
			i++;
			Matcher matcher=Pattern.compile(regex).matcher(input);
			if(matcher.find()){
				values[i]=matcher.group(1);
			}
		}
		ProcessInfo processInfo=new ProcessInfo();
		processInfo.username=values[0];
		processInfo.pid=Integer.valueOf(values[1]);
		return processInfo;
	}
	protected static class ProcessInfo{
		private int pid;
		private String username;
		public int getPid() {
			return pid;
		}
		public void setPid(int pid) {
			this.pid = pid;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
	}
}
