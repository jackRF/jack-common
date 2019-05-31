package org.jack.common.ssh2;

import org.jack.common.util.net.ConnectionPair;

public interface IServerConfig {
	ConnectionPair getServer(String app,String env);
}
