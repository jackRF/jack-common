package org.jack.common.util.net;

import java.util.HashSet;
import java.util.Set;

public class DBPair {
	public static final DBTypePair MYSQL=new DBTypePair(1, "jdbc:mysql://%s/%s");
	public static final DBTypePair ORACLE=new DBTypePair(2,"jdbc:oracle:thin:@%s:%s");
	public static final DBTypePair DB2=new DBTypePair(3,"jdbc:db2://%s/%s");
	public static final DBTypePair SQLSERVER=new DBTypePair(4,"jdbc:microsoft:sqlserver://%s;DatabaseName=%s");
	public static final DBTypePair SYBASE=new DBTypePair(5,"jdbc:sybase:Tds:%s/%s");
	public static final DBTypePair POSTGRESQL=new DBTypePair(6,"jdbc:postgresql://%s/%s");
	private final DBTypePair dbType;
	private final String database;
	public DBPair(DBTypePair dbType,String database) {
		this.dbType=dbType;
		this.database=database;
	}
	public DBTypePair getDbType() {
		return dbType;
	}
	public String getDatabase() {
		return database;
	}
	public String toJdbcUrlString(NetAddressPair netAddress) {
		return String.format(dbType.format, netAddress.toString(),database);
	}
	private static class DBTypePair {
		private static final Set<DBTypePair> dbTypes=new HashSet<DBTypePair>(); 
		private final int type;
		private final String format;
		private DBTypePair(int type,String format){
			this.type=type;
			this.format=format;
			if(!dbTypes.add(this)){
				throw new IllegalArgumentException("type 不能重复");
			}
		}
		@Override
		public int hashCode() {
			return Integer.hashCode(type);
		}
		@Override
		public boolean equals(Object obj) {
			if(this==obj){
				return true;
			}
			if(obj instanceof DBTypePair){
				return ((DBTypePair) obj).type==this.type;
			}
			return false;
		}
	}
}
