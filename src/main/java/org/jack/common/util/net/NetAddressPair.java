package org.jack.common.util.net;

public class NetAddressPair {
	private final String hostname;
	private final int port;
	public NetAddressPair(String hostname) {
		this(hostname,0);
	}
	public NetAddressPair(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}
	@Override
	public String toString() {
		if(port<=0){
			return hostname;
		}
		return hostname+":"+port;
	}
}
