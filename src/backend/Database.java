package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

/**
 * Class that handles the database functions of Releases.accdb relating to
 * reading and writing.
 *
 * @author SITHEMBISO MALINGA
 */
public class Database {

    private static Connection con;
    private static String sql;
    private static PreparedStatement stmt = null;
    private static ResultSet rs;

    /**
     * Establishes a connection with the database.
     *
     * @return - Connection object
     */
    private static Connection connect() {
        String dbURL = "jdbc:ucanaccess://Releases.accdb";
        con = null;
        try {
            con = DriverManager.getConnection(dbURL);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return con;
    }

    /**
     * Basic method that takes a SQL Statement in the object and then executes
     * it, writing data to the database.
     */
    private static void write() {
        try {
            /*
            This code below is to work on the edge case of prepared statements with complex objects. 
            
            Most of the write commands are simple SQL functions with a single value or String, which
            can simply be operated by appending the data into the sql string object, and executing 
            the below loop of connect, prepare, execute, close.
            
            However, for updating an entry, which involves booleans and dates, a simple string cannot
            account for the data, so .setString() is done. But THAT can only be done AFTER preparing
            the statement, which is impossible if that is done in the write() abstraction afterwards.
            Therefore, in the case that stmt is not empty (i.e it's been prepared outside this method),
            the usual steps are assumed to be done and skipped. 
             */
            if (stmt == null) {
                connect();
                stmt = con.prepareStatement(sql);
            }
            stmt.executeUpdate();
            con.close();
            stmt = null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    /**
     * Basic method that takes a SQL Statement in the object and then executes
     * it, returning a ResultSet of the data selected.
     *
     * @param sql
     * @return - Result
     */
    private static ResultSet read() {
        try {
            //Refer to write()'s comment for context.
            if (stmt == null) {
                connect();
                stmt = con.prepareStatement(sql);
            }
            rs = stmt.executeQuery();
            con.close();
            stmt = null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return rs;
    }

    /**
     * Retrieves an entry's data from its id.
     *
     * @param entryID
     * @return - ResultSet
     */
    public ResultSet selectEntry(int entryID) {
        sql = "SELECT Title, Creator, ReleaseYear, Type, DateAdded, Percentage, TotalLength, LengthType, CompletedLength, DateCompleted, Note, Rating, ExactReleaseDate, Medium, Subtitle, Ownership, Wishlist, Deadline FROM (((tblMasterEntryList INNER JOIN tblProgress ON tblMasterEntryList.ID = tblProgress.ID) INNER JOIN tblCollection ON tblMasterEntryList.ID = tblCollection.ID) INNER JOIN tblSchedule ON tblMasterEntryList.ID = tblSchedule.ID) WHERE tblMasterEntryList.ID = " + entryID;
        return read();
    }

    /**
     * Retrieves a group's data from its id.
     *
     * @param groupID
     * @return - ResultSet
     */
    public ResultSet selectGroup(int groupID) {
        sql = "SELECT [Group] FROM tblMasterGroupList WHERE ID = " + groupID;
        return read();
    }

    /**
     * Retrieves all connections to a group (false) or to an entry (true).
     *
     * @param idType
     * @param id
     * @return - ResultSet
     */
    public ResultSet selectConnections(boolean idType, int id) {
        if (idType) //true means id is of an entry
        {
            sql = "SELECT GroupID FROM tblGroupEntry WHERE EntryID = " + id;
        } else {
            sql = "SELECT EntryID FROM tblGroupEntry WHERE GroupID = " + id;
        }
        return read();
    }

    /**
     * Retrieves a list of every entry by their unique id.
     *
     * @return - ResultSet
     */
    public ResultSet selectAllEntries() {
        sql = "SELECT tblMasterEntryList.ID FROM tblMasterEntryList";
        return read();
    }

    /**
     * Retrieves a list of every group by their unique id.
     *
     * @return - ResultSet
     */
    public ResultSet selectAllGroups() {
        sql = "SELECT ID FROM tblMasterGroupList";
        return read();
    }

    /**
     * Retrieves a list of all entries that either belong to a group (tg ==
     * true) or a type (tg == false)
     *
     * @param tg
     * @param id
     * @return - ResultSet
     */
    public ResultSet selectAllEntriesBy(boolean tg, int id) {
        if (tg) {
            sql = "SELECT ID FROM tblMasterGroupList WHERE GroupID = " + id;
        } else {
            String typ = "";
            if (id != -6) {
                switch (id) {
                    case -5:
                        typ = "Music";
                        break;
                    case -4:
                        typ = "Film";
                        break;
                    case -3:
                        typ = "TV";
                        break;
                    case -2:
                        typ = "Book";
                        break;
                    case -1:
                        typ = "Video Game";
                        break;
                }
                sql = "SELECT ID FROM tblMasterEntryList WHERE Type = \"" + typ + "\"";
            } else {
                sql = "SELECT ID FROM tblMasterEntryList";
            }
        }
        return read();
    }

    /**
     * Retrieves a list of all entries that either belong to a group (tg ==
     * true) or a type (tg == false) with additional search functions.
     *
     * @param tg
     * @param id
     * @param query
     * @param searchType
     * @return - ResultSet
     */
    public ResultSet selectAllEntriesBySearch(boolean tg, int id, String query, String searchType) {
        if (tg) {
            sql = "SELECT ID FROM tblMasterGroupList WHERE GroupID = " + id;
        } else {
            String typ = "";
            if (id != 0) {
                switch (id) {
                    case 1:
                        typ = "Music";
                        break;
                    case 2:
                        typ = "Film";
                        break;
                    case 3:
                        typ = "TV";
                        break;
                    case 4:
                        typ = "Book";
                        break;
                    case 5:
                        typ = "Video Game";
                        break;
                }
                sql = "SELECT ID FROM tblMasterEntryList WHERE Type = \"" + typ + "\"";
            } else {
                sql = "SELECT ID FROM tblMasterEntryList";
            }
        }
        sql += " AND " + searchType + " LIKE \"*" + query + "*\"";
        return read();
    }

    /**
     * Adds a new entry to the database.
     *
     * @param ent
     * @throws java.sql.SQLException
     */
    public void addEntry(Entry ent) throws SQLException {
        //Throwing the error as opposed to catching it is to put the duties
        //of actually handling it to the other programs. This helps to
        //simplify Database.java.
        stmt = null;
        Date now = ent.getDateAdded();
        java.sql.Date sqlNow = new java.sql.Date((now.getTime()));
        sql = "INSERT INTO tblMasterEntryList(DateAdded) VALUES (?)";
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setDate(1, sqlNow);
        write();

        int id;
        sql = "SELECT ID From tblMasterEntryList WHERE DateAdded = ?";
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setDate(1, sqlNow);
        ResultSet idSet = read();
        idSet.next();
        id = idSet.getInt(1);

        sql = "UPDATE tblMasterEntryList SET Title = ?, Creator = ?, ReleaseYear = ?, Type = ?, Percentage = ? WHERE ID = " + id;
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setString(1, ent.getTitle());
        stmt.setString(2, ent.getCreator());
        stmt.setString(3, ent.getYear());
        stmt.setString(4, ent.getType());
        stmt.setDouble(5, ent.getPercentage());
        write();

        sql = "INSERT INTO tblCollection(ID, ExactReleaseDate, Medium, Subtitle, Ownership) VALUES(?, ?, ?, ?, ?)";
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, id);
        if (!(ent.getExactReleaseDate() == null)) {
            stmt.setDate(2, new java.sql.Date(ent.getExactReleaseDate().getTime()));
        } else {
            stmt.setDate(2, null);
        }
        stmt.setString(3, ent.getMedium());
        stmt.setString(4, ent.getSubtitle());
        stmt.setString(5, ent.getOwnership());
        write();

        sql = "INSERT INTO tblProgress(ID, TotalLength, LengthType, CompletedLength, DateCompleted, Note, Rating) VALUES(?, ?, ?, ?, ?, ?, ?)";
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setInt(2, ent.getTotalLength());
        stmt.setString(3, ent.getLengthType());
        stmt.setInt(4, ent.getCompletedLength());
        if (ent.getDateCompleted() == null && ent.getPercentage() == 100) {
            stmt.setDate(5, new java.sql.Date(new Date().getTime()));
        } else {
            stmt.setDate(5, null);
        }
        stmt.setString(6, ent.getNote());
        stmt.setInt(7, ent.getRating());
        write();

        sql = "INSERT INTO tblSchedule(ID, Wishlist, Deadline) VALUES(?, ?, ?)";
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setBoolean(2, ent.getWishlist());
        if (!(ent.getDeadline() == null)) {
            stmt.setDate(3, new java.sql.Date(ent.getDeadline().getTime()));
        } else {
            stmt.setDate(3, null);
        }
        write();

        updateConnections(id, ent.getGroups());
    }

    /**
     * Adds a new group to the database.
     *
     * @param grp
     * @throws java.sql.SQLException
     */
    public void addGroup(Group grp) throws SQLException {
        stmt = null;
        sql = "INSERT INTO tblMasterGroupList ([Group]) VALUES ('" + grp.getName() + "')";
        write();
    }

    /**
     * Updates an already existing entry in the database.
     *
     * @param ent
     * @throws java.sql.SQLException
     */
    public void updateEntry(Entry ent) throws SQLException {
        stmt = null;
        java.sql.Date sqlNow = new java.sql.Date(ent.getDateAdded().getTime());
        int id = ent.getId();

        sql = "UPDATE tblMasterEntryList SET Title = ?, Creator = ?, ReleaseYear = ?, Type = ?, Percentage = ? WHERE ID = " + id;
        connect();
        ent.toString();
        stmt = con.prepareStatement(sql);
        stmt.setString(1, ent.getTitle());
        stmt.setString(2, ent.getCreator());
        stmt.setString(3, ent.getYear());
        stmt.setString(4, ent.getType());
        stmt.setDouble(5, ent.getPercentage());
        write();

        sql = "UPDATE tblCollection SET ExactReleaseDate = ?, Medium = ?, Subtitle = ?, Ownership = ? WHERE ID = " + id;
        connect();
        stmt = con.prepareStatement(sql);
        if (!(ent.getExactReleaseDate() == null)) {
            stmt.setDate(1, new java.sql.Date(ent.getExactReleaseDate().getTime()));
        } else {
            stmt.setDate(1, null);
        }
        stmt.setString(2, ent.getMedium());
        stmt.setString(3, ent.getSubtitle());
        stmt.setString(4, ent.getOwnership());
        write();

        sql = "UPDATE tblProgress SET TotalLength = ?, LengthType = ?, CompletedLength = ?, DateCompleted = ?, Note = ?, Rating = ? WHERE ID = " + id;
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, ent.getTotalLength());
        stmt.setString(2, ent.getLengthType());
        stmt.setInt(3, ent.getCompletedLength());
        if (ent.getDateCompleted() == null && ent.getPercentage() == 100) {
            stmt.setDate(4, new java.sql.Date(new Date().getTime()));
        } else {
            stmt.setDate(4, null);
        }
        stmt.setString(5, ent.getNote());
        stmt.setInt(6, ent.getRating());
        write();

        sql = "UPDATE tblSchedule SET Wishlist = ?, Deadline = ? WHERE ID = " + id;
        connect();
        stmt = con.prepareStatement(sql);
        stmt.setBoolean(1, ent.getWishlist());
        if (!(ent.getDeadline() == null)) {
            stmt.setDate(2, new java.sql.Date(ent.getDeadline().getTime()));
        } else {
            stmt.setDate(2, null);
        }
        write();

        updateConnections(ent.getId(), ent.getGroups());

    }

    /**
     * Updates an already existing group into the database.
     *
     * @param grp
     * @throws java.sql.SQLException
     */
    public void updateGroup(Group grp) throws SQLException {
        stmt = null;
        sql = "UPDATE tblMasterGroupList SET [Group] = '" + grp.getName() + "' WHERE ID = " + grp.getId();
        write();
    }

    /**
     * Removes a entry based on their unique id from the database.
     *
     * @param entryID
     * @throws java.sql.SQLException
     */
    public void removeEntry(int entryID) throws SQLException {
        stmt = null;
        sql = "DELETE FROM tblMasterEntryList WHERE ID = " + entryID;
        write();
        stmt = null;
        sql = "DELETE FROM tblProgress WHERE ID = " + entryID;
        write();
        stmt = null;
        sql = "DELETE FROM tblCollection WHERE ID = " + entryID;
        write();
        stmt = null;
        sql = "DELETE FROM tblSchedule WHERE ID = " + entryID;
        write();
        stmt = null;
        sql = "DELETE FROM tblGroupEntry WHERE EntryID = " + entryID;
        write();
    }

    /**
     * Removes a group based on their unique id from the database.
     *
     * @param groupID
     * @throws java.sql.SQLException
     */
    public void removeGroup(int groupID) throws SQLException {
        stmt = null;
        sql = "DELETE FROM tblMasterGroupList WHERE ID = " + groupID;
        write();
        stmt = null;
        sql = "DELETE FROM tblGroupEntry WHERE GroupID = " + groupID;
        write();
    }

    /**
     * Private method to be done in tandem with entry addition to update the
     * complex relationships of an entry to groups, with branch off methods to
     * account for list increases and decreases.
     */
    private void updateConnections(int entID, int[] groups) {
        //Elaborate on this algorithm especially for Phase 4A

        EntryGroupConnection egc = new EntryGroupConnection();
        int[] oldGroups = egc.getEntParents(entID);
        int[] newGroups;

        //confirm that the newgroups list actually DO correspond to actual
        //group IDs in the database, cleaning out impossible connections
        //to non-existent groups.
        List<Integer> funcNewGrps = new ArrayList<>();
        int[] allGrps = egc.getGroups();
        for (int i = 0; i < groups.length; i++) {
            for (int j = 0; j < allGrps.length; j++) {
                if (groups[i] == allGrps[j]) {
                    funcNewGrps.add(groups[i]);
                    break;
                }
            }
        }
        newGroups = new int[funcNewGrps.size()];
        for (int i = 0; i < funcNewGrps.size(); i++) {
            newGroups[i] = funcNewGrps.get(i);
        }

        //adds in connections that should be in the database but aren't
        for (int i = 0; i < newGroups.length; i++) {
            if (oldGroups.length != 0) {
                for (int j = 0; j < oldGroups.length; j++) {
                    if (newGroups[i] == oldGroups[j]) { //match found
                        break;
                    }
                    if (j == oldGroups.length - 1) { //could not find a match
                        addCon(entID, newGroups[i]);
                        oldGroups = egc.getEntParents(entID);
                    }
                }
            } else {
                addCon(entID, newGroups[i]);
            }
        }

        //removes connections that shouldn't be in the datas but are.
        for (int i = 0; i < oldGroups.length; i++) {
            if (newGroups.length != 0) {
                for (int j = 0; j < newGroups.length; j++) {
                    if (oldGroups[i] == newGroups[j]) { //match found
                        break;
                    }
                    if (j == newGroups.length - 1) { //could not find a match
                        removeCon(entID, oldGroups[i]);
                        oldGroups = egc.getEntParents(entID);
                    }
                }
            } else {
                removeCon(entID, oldGroups[i]);
            }
        }

    }

    /**
     * Used only by updateConnection() to add a new connection.
     *
     * @param entry
     * @param group
     */
    private void addCon(int entry, int group) {
        stmt = null;
        sql = "INSERT INTO tblGroupEntry(EntryID, GroupID) VALUES(" + entry + ", " + group + ")";
        write();
    }

    /**
     * Used only by updateConnection() to delete an connection.
     *
     * @param entry
     * @param group
     */
    private void removeCon(int entry, int group) {
        stmt = null;
        sql = "DELETE FROM tblGroupEntry WHERE EntryID = " + entry + " AND GroupID = " + group;
        write();
    }

    /**
     * A simple method to clean up a weird exception regarding null entries
     * tables, done either at the start of the program or in tandem with
     * database errors.
     *
     * @param id - Entry of the ID
     * @throws java.sql.SQLException
     */
    public void repairDatabase(int id) throws SQLException {
        //While this does work, future update can expand this method
        //to involve even more complex checks
        ResultSet rsTest;
        sql = "SELECT Id FROM tblCollection  WHERE ID = " + id;
        rsTest = read();
        if (rsTest.wasNull()) {
            {
                sql = "INSERT INTO tblCollection(ID) VALUES(?)";
                connect();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, id);
                write();
            }

            sql = "SELECT Id FROM tblProgress  WHERE ID = " + id;
            rsTest = read();
            if (rsTest.wasNull()) {
                sql = "INSERT INTO tblProgress(ID) VALUES(?)";
                connect();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, id);
                write();
            }

            sql = "SELECT Id FROM tblSchedule  WHERE ID = " + id;
            rsTest = read();
            if (rsTest.wasNull()) {
                sql = "INSERT INTO tblSchedule(ID) VALUES(?)";
                connect();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, id);
                write();
            }
        }
    }
}
