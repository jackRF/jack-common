package org.jack.common.util.net;

public class DBConnectionPair {
	private ConnectionPair connection;
	private DBPair db;
	public ConnectionPair getConnection() {
		return connection;
	}
	public void setConnection(ConnectionPair connection) {
		this.connection = connection;
	}
	public DBPair getDb() {
		return db;
	}
	public void setDb(DBPair db) {
		this.db = db;
	}
	public String toJdbcUrlString() {
		return db.toJdbcUrlString(connection.getNetAddress());
	}
}
