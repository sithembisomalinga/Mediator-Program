package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Object class for a group.
 * @author SITHEMBISO MALINGA
 */
public class Group {

    private int id;
    private String name;

    /**
     * Default constructor, id is set to -1 to indicate to other programs that it isn't a real group.
     */
    public Group(){
        id = -1;
        name = "";
    }
    
    /**
     * Parameterised constructor, taking in the id of a group to load in from the database the name.
     * @param id (int) Group UniqueID
     */
    public Group(int id) {
        Database db = new Database();
        this.id = id;
        ResultSet rs = db.selectGroup(this.id);
        try {
            if (rs.next()) 
                this.name = rs.getString(1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    /**
     * Retrieves id.
     * @return - int
     */
    public int getId(){
        return id;
    }
    
    /**
     * Retrieves name.
     * @return - String
     */
    public String getName(){
        return name;
    }
    
    /**
     * Sets name.
     * @param name 
     */
    public void setName(String name){
        this.name  = name;
    }
    
    /**
     * Returns a string of all the fields.
     * @return - String
     */
    @Override
    public String toString(){
        return id + " " + name;
    }
}
