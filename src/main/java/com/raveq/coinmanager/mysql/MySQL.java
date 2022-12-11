package com.raveq.coinmanager.mysql;

import com.raveq.coinmanager.CoinManager;

import javax.xml.transform.Result;
import java.sql.*;

public class MySQL {

    private String HOST = "";
    private String DATABASE = "";
    private String USER = "";
    private String PASSWORD = "";

    private Connection con;

    public MySQL(String host, String database, String user, String password) {
        this.HOST = host;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;
        connect();
    }

    public void connect() {
        try {
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.HOST + ":3306/" + this.DATABASE + "?autoReconnect=true", this.USER, this.PASSWORD);
            CoinManager.sender.sendMessage("§8| §bCoinManager §8» §7Die Verbindung zu MySQL wurde §ehergestellt!");
        } catch (SQLException e) {
            CoinManager.sender.sendMessage("§8| §bCoinManager §8» §7Die Verbindung zur MySQL ist §4fehlgeschlagen! §7Fehler §4" + e.getMessage());
        }
    }

    public void update(String qry) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch(SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;
        try {
            Statement st = this.con.createStatement();
            rs = st.executeQuery(qry);
        } catch(SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

}
