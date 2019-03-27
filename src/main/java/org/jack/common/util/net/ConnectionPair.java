package org.jack.common.util.net;

public class ConnectionPair {
	private NetAddressPair netAddress;
	private AuthenticatePair authenticate;
	public NetAddressPair getNetAddress() {
		return netAddress;
	}
	public void setNetAddress(NetAddressPair netAddress) {
		this.netAddress = netAddress;
	}
	public AuthenticatePair getAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(AuthenticatePair authenticate) {
		this.authenticate = authenticate;
	}
}
