package it.polimi.processing.collector.saver;

import it.polimi.processing.collector.Collectable;
import it.polimi.processing.collector.CollectableData;
import it.polimi.processing.enums.ExecutionStates;
import it.polimi.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Getter;

import org.apache.log4j.Logger;

@Getter
public class SQLLiteEventSaver implements EventSaver {

	private static final String JDBC_SQLITE_OBQAATCEP_DB = "jdbc:sqlite:obqaatcep.db";
	private static final String ORG_SQLITE_JDBC = "org.sqlite.JDBC";
	private long timestamp;
	private Connection c;
	private Statement stmt = null;
	private ExecutionStates status;

	@Override
	public boolean save(Collectable e) {
		if (ExecutionStates.READY.equals(status) && e != null) {
			String sql = DatabaseUtils.EXPERIMENT_INSERT + e.getSQL();
			Logger.getRootLogger().debug("EXPERIMENT EVENT SQL VALUE " + sql);
			try {
				stmt = c.createStatement();
				if (sql != null && stmt != null && c != null && !c.isClosed()) {

					stmt.executeUpdate(sql);
					stmt.close();
				} else {
					return false;
				}
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		} else {
			Logger.getRootLogger().warn("Not ready to write db");
			return false;
		}
	}

	/**
	 * Initialize the mains tables of the system
	 * 
	 * @see it.polimi.output.filesystem.DatabaseManager#init()
	 **/
	@Override
	public ExecutionStates init() throws ClassNotFoundException, SQLException {
		timestamp = System.currentTimeMillis();
		Class.forName(ORG_SQLITE_JDBC);
		c = DriverManager.getConnection(JDBC_SQLITE_OBQAATCEP_DB);
		Logger.getRootLogger().info("Opened database successfully");
		stmt = c.createStatement();
		stmt.executeUpdate(DatabaseUtils.EXPERIMENT_TABLE);
		stmt.executeUpdate(DatabaseUtils.COMPARATION_TABLE);
		stmt.close();
		Logger.getRootLogger().info("Table created successfully");
		return this.status = ExecutionStates.READY;

	}

	@Override
	public ExecutionStates close() throws ClassNotFoundException, SQLException {
		Logger.getRootLogger().info("Stop the Database System");
		queryAllComp();
		c.close();
		Logger.getRootLogger().info("Closed database successfully");
		return this.status = ExecutionStates.CLOSED;

	}

	public void queryAll() throws SQLException {
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM EXPERIMENT;");
		while (rs.next()) {
			Logger.getRootLogger().info(
					"EXPERIMENT_ID = " + rs.getString("EXP_ID"));
			Logger.getRootLogger().info(
					"TS_INIT = " + Long.parseLong(rs.getString("TS_INIT")));
			Logger.getRootLogger().info(
					"TS_END = " + Long.parseLong(rs.getString("TS_END")));
			Logger.getRootLogger().info(
					"INPUT_FILE = " + rs.getString("INPUT_FILE"));
			Logger.getRootLogger().info(
					"RESULT_FILE = " + rs.getString("RESULT_FILE"));
			Logger.getRootLogger().info(
					"FILE_LOG_FOLDER" + rs.getString("FILE_LOG_FOLDER"));
		}
		rs.close();
		stmt.close();
	}

	public void queryAllComp() throws SQLException {
		Logger.getRootLogger().info("QUERY ALL");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM COMPARATION;");
		while (rs.next()) {
			Logger.getRootLogger().debug(
					"EXPERIMENT_ID = " + rs.getString("EXP_ID"));
			Logger.getRootLogger().debug(
					"EVENT_ID = " + rs.getString("EVENT_ID"));
			Logger.getRootLogger().debug("SOUND = " + rs.getBoolean("SOUND"));
			Logger.getRootLogger().debug(
					"COMPLETE = " + rs.getBoolean("COMPLETE"));
			Logger.getRootLogger().debug("MEMORY = " + rs.getDouble("MEMORY"));
			Logger.getRootLogger().debug("LATENCY = " + rs.getLong("LATENCY"));

		}
		rs.close();
		stmt.close();
	}

	@Override
	public boolean save(CollectableData d) {
		if (ExecutionStates.READY.equals(status) && d != null) {
			String sql = DatabaseUtils.EXPERIMENT_INSERT + d.getData();
			Logger.getRootLogger().debug("EXPERIMENT EVENT SQL VALUE " + sql);
			try {
				stmt = c.createStatement();
				if (sql != null && stmt != null && c != null && !c.isClosed()) {

					stmt.executeUpdate(sql);
					stmt.close();
				} else {
					return false;
				}
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		} else {
			Logger.getRootLogger().warn("Not ready to write db");
			return false;
		}
	}

}