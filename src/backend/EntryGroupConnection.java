package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Object class to deal with the multiple connections of entries to groups.
 *
 * @author SITHEMBISO MALINGA
 */
public class EntryGroupConnection {

    private static Database db;
    private static ResultSet rs;
    private static int[] groups;
    private static int[] entries;
    private static int[] grpChildren;
    private static int[] entParents;

    /**
     * Default constructor.
     */
    public EntryGroupConnection() {
        db = new Database();
        createSingleArrays();
    }

    /**
     * Separate method to create two arrays, one of every entry and one of every
     * group.
     */
    private void createSingleArrays() {
        //The use of List<Int> are to allow for dynamic arrays, to later
        //be set as a static traditional array.
        List<Integer> grps = new ArrayList<>();
        List<Integer> ent = new ArrayList<>();
        try {
            rs = db.selectAllGroups();
            while (rs.next()) {
                grps.add(rs.getInt(1));
            }

            rs = db.selectAllEntries();
            while (rs.next()) {
                ent.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        groups = new int[grps.size()];
        entries = new int[ent.size()];

        for (int i = 0; i < groups.length; i++) {
            groups[i] = grps.get(i);
        }
        for (int i = 0; i < entries.length; i++) {
            entries[i] = ent.get(i);
        }
    }

    /**
     * Creates an array of all the entries ids connected to a group of specified
     * id.
     *
     * @param id
     */
    private void createGroupChildren(int id) {
        List<Integer> grpsRel = new ArrayList<>();
        try {
            if (id > -1) {
                rs = db.selectConnections(false, id);
            } else {
                rs = db.selectAllEntriesBy(false, id);
            }

            while (rs.next()) {
                grpsRel.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        grpChildren = new int[grpsRel.size()];
        for (int i = 0; i < grpChildren.length; i++) {
            grpChildren[i] = grpsRel.get(i);
        }
    }

    /**
     * Creates an array of all the groups ids connected to an entry of specified
     * id.
     *
     * @param id
     */
    private void createEntryParents(int id) {
        List<Integer> entRel = new ArrayList<>();
        try {
            rs = db.selectConnections(true, id);
            while (rs.next()) {
                entRel.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        entParents = new int[entRel.size()];
        for (int i = 0; i < entParents.length; i++) {
            entParents[i] = entRel.get(i);
        }
    }

    /**
     * Retrieves groups array
     *
     * @return - int[]
     */
    public int[] getGroups() {
        return groups;
    }

    /**
     * Retrieves entries array
     *
     * @return - int[]
     */
    public int[] getEntries() {
        return entries;
    }

    /**
     * Retrieves array of all the entries belonging to a group.
     *
     * @param grpID
     * @return - int[]
     */
    public int[] getGrpChildren(int grpID) {
        createGroupChildren(grpID);
        return grpChildren;
    }

    /**
     * Retrieves array of all the groups belonging to an entry.
     *
     * @param entID
     * @return - int[]
     */
    public int[] getEntParents(int entID) {
        createEntryParents(entID);
        return entParents;
    }

    /**
     * Retrieves groups as an array of names for folder list.
     *
     * @return - String[]
     */
    public String[] getGroupsAsText() {
        String[] temp = new String[groups.length + 6];
        temp[0] = "All Categories";
        temp[1] = "Music";
        temp[2] = "Film";
        temp[3] = "TV";
        temp[4] = "Books";
        temp[5] = "Games";
        for (int i = 0; i < groups.length; i++) {
            Group grp = new Group(groups[i]);
            temp[i + 6] = "  (" + grp.getId() + ")  " + grp.getName();
        }
        return temp;
    }

    /**
     * Retrieves a simple single line
     *
     * @param entID
     * @return - String
     */
    public String getEntParentsAsText(int entID) {
        createEntryParents(entID);
        String temp = "";
        for (int i = 0; i < entParents.length; i++) {
            Group grp = new Group(entParents[i]);
            if (i < entParents.length - 1) {
                temp += grp.getName() + ", ";
            } else {
                temp += grp.getName();
            }
        }
        return temp;
    }

    /**
     * Takes ever entry belonging to a group and averaging out for calculating
     * the group average.
     *
     * @param grpID
     * @return - double
     */
    public double calcAvgPercentage(int grpID) {
        getGrpChildren(grpID);
        double sum = 0;
        int tot = grpChildren.length;
        if (tot != 0) {
            for (int i = 0; i < tot; i++) {
                Entry en = new Entry(grpChildren[i]);
                sum += en.getPercentage();
            }
            return (sum * 1.0) / tot;
        } else {
            return 0;
            //Avoid division by 0
        }

    }

    /**
     * Complex function to return a String of a tabulated list of every
     * connection.
     *
     * @return - String
     */
    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < groups.length; i++) {

            createGroupChildren(groups[i]);
            out += groups[i] + "| ";
            for (int j = 0; j < grpChildren.length; j++) {
                out += grpChildren[j] + " ";
            }
            out += "\n";
        }
        return out;
    }
}
