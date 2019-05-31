package org.jack.common.ssh2;

import org.jack.common.util.net.AuthenticatePair;
import org.jack.common.util.net.ConnectionPair;
import org.jack.common.util.net.NetAddressPair;

public class ServerConfig implements IServerConfig{
	private ConnectionPair DEV_BMS;
	private ConnectionPair DEV_RULE;
	private ConnectionPair DEV_CFS;
	private ConnectionPair DEV_BDS;
	public ServerConfig() {
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.235.9"));
			connectionPair.setAuthenticate(new AuthenticatePair("root", "zd,123"));
			DEV_BMS=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.50"));
			connectionPair.setAuthenticate(new AuthenticatePair("rule", "zd,123"));
			DEV_RULE=connectionPair;
		}{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.235.172"));
			connectionPair.setAuthenticate(new AuthenticatePair("cfs", "zd,123"));
			DEV_CFS=connectionPair;
		}{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.49"));
			connectionPair.setAuthenticate(new AuthenticatePair("root", "zd,123"));
			DEV_BDS=connectionPair;
		}
	}
	@Override
	public ConnectionPair getServer(String app, String env) {
		String envApp=env.toUpperCase()+"_"+app.toUpperCase();
		if("DEV_BDS".equals(envApp)){
			return DEV_BDS;
		}else if("DEV_CFS".equals(envApp)){
			return DEV_CFS;
		}else if("DEV_RULE".equals(envApp)){
			return DEV_RULE;
		}else if("DEV_BMS".equals(envApp)){
			return DEV_BMS;
		}
		return null;
	}
}
