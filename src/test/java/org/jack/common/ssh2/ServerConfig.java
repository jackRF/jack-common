package org.jack.common.ssh2;

import org.jack.common.util.net.AuthenticatePair;
import org.jack.common.util.net.ConnectionPair;
import org.jack.common.util.net.NetAddressPair;

public class ServerConfig implements IServerConfig{
	private ConnectionPair DEV_BMS;
	private ConnectionPair SIT1_BMS;
	private ConnectionPair SIT2_BMS;
	private ConnectionPair DEV_RULE;
	private ConnectionPair SIT1_RULE;
	private ConnectionPair SIT2_RULE;
	private ConnectionPair UAT_RULE;
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
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("10.100.200.85"));
			connectionPair.setAuthenticate(new AuthenticatePair("bms", "bms,123"));
			SIT1_BMS=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.131"));
			connectionPair.setAuthenticate(new AuthenticatePair("rule", "rule"));
			SIT1_RULE=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("10.100.200.88"));
			connectionPair.setAuthenticate(new AuthenticatePair("bmb", "bmb,123"));
			SIT2_BMS=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.148"));
			connectionPair.setAuthenticate(new AuthenticatePair("rule", "rule"));
			SIT2_RULE=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.250.237"));
			connectionPair.setAuthenticate(new AuthenticatePair("lookup", "lookup"));
			UAT_RULE=connectionPair;
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
		}else if("SIT1_BMS".equals(envApp)){
			return SIT1_BMS;
		}else if("SIT1_RULE".equals(envApp)){
			return SIT1_RULE;
		}else if("SIT2_BMS".equals(envApp)){
			return SIT2_BMS;
		}else if("SIT2_RULE".equals(envApp)){
			return SIT2_RULE;
		}else if("UAT_RULE".equals(envApp)){
			return UAT_RULE;
		}
		return null;
	}
}
