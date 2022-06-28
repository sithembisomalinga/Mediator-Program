package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Object class for an entry.
 *
 * @author SITHEMBISO MALINGA
 */
public class Entry {

    private int id;
    //tblMasterReleaseList
    private String title;
    private String creator;
    private String year;
        //This may seem like a controversial decision to have
        //an interger cast as a String, but my reasoning is that since i will not
        //perform operations on it, it will be fine to use as a string.
    private String type;
    private Date dateAdded;
    private double percentage;
    //tblProgress
    private int totalLength;
    private String lengthType;
    private int completedLength;
    private Date dateCompleted;
    private String note;
    private int rating;
    //tblCollection
    private Date exactReleaseDate;
    private String medium;
    private String subtitle;
    private String ownership;
    //tblSchedule
    private boolean wishlist;
    private Date deadline;
    //tblGroupEntry
    private int[] groups;

    /**
     * Default constructor, id is set to -1 to indicate it is not in the
     * database.
     */
    public Entry() {
        this.id = -1; //-1 indicates it is not in the database.
        this.title = "";
        this.creator = "";
        this.year = "";
        this.type = "";
        this.medium = "";
        this.subtitle = "";
        this.ownership = "";
        this.lengthType = "";
        this.note = "";
        this.percentage = 0;
        this.totalLength = 0;
        this.rating = 0;
        this.completedLength = 0;
        this.wishlist = false;
        this.groups = new int[0];
        try {
            this.dateAdded = new SimpleDateFormat("dd-MM-yyyy").parse("1970-01-01");
            this.dateCompleted = new SimpleDateFormat("dd-MM-yyyy").parse("1970-01-01");
            this.exactReleaseDate = new SimpleDateFormat("dd-MM-yyyy").parse("1970-01-01");
            this.deadline = new SimpleDateFormat("dd-MM-yyyy").parse("1970-01-01");
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Date could not be parsed.\n" + ex);
        }
    }

    /**
     * Parameterised constructor, initialises the Entry object using only an id
     * entry.
     *
     * @param id
     */
    public Entry(int id) {
        Database db = new Database();
        this.id = id;
        ResultSet rs = db.selectEntry(this.id);
        try {
            if (rs.next()) {

                String titleTemp = rs.getString(1);
                String creatorTemp = rs.getString(2);
                String yearTemp = rs.getString(3);
                String typeTemp = rs.getString(4);
                String dateAddedTemp = rs.getString(5);
                String percentageTemp = rs.getString(6);
                String totalLengthTemp = rs.getString(7);
                String lengthTypeTemp = rs.getString(8);
                String completedLengthTemp = rs.getString(9);
                String dateCompletedTemp = rs.getString(10);
                String noteTemp = rs.getString(11);
                String ratingTemp = rs.getString(12);
                String exactReleaseDateTemp = rs.getString(13);
                String mediumTemp = rs.getString(14);
                String subtitleTemp = rs.getString(15);
                String ownershipTemp = rs.getString(16);
                String wishlistTemp = rs.getString(17);
                String deadlineTemp = rs.getString(18);

                this.title = titleTemp;
                this.creator = creatorTemp;
                this.year = yearTemp;
                this.type = typeTemp;
                this.medium = mediumTemp;
                this.subtitle = subtitleTemp;
                this.ownership = ownershipTemp;
                this.lengthType = lengthTypeTemp;
                this.note = noteTemp;

                EntryGroupConnection egc = new EntryGroupConnection();
                this.groups = egc.getEntParents(this.id);

                if (!percentageTemp.isEmpty()) {
                    this.percentage = Double.parseDouble(percentageTemp);
                }

                if (!totalLengthTemp.isEmpty()) {
                    this.totalLength = Integer.parseInt(totalLengthTemp);
                }

                if (!completedLengthTemp.isEmpty()) {
                    this.completedLength = Integer.parseInt(completedLengthTemp);
                }

                if (!ratingTemp.isEmpty()) {
                    this.rating = Integer.parseInt(ratingTemp);
                }

                if (!wishlistTemp.isEmpty()) {
                    this.wishlist = Boolean.parseBoolean(wishlistTemp);
                }
                if (!(dateAddedTemp == null)) {
                    this.dateAdded = new SimpleDateFormat("yyyy-MM-dd").parse(dateAddedTemp.substring(0, 10));
                }
                if (!(dateCompletedTemp == null)) {
                    this.dateCompleted = new SimpleDateFormat("yyyy-MM-dd").parse(dateCompletedTemp.substring(0, 10));
                }
                if (!(exactReleaseDateTemp == null)) {
                    this.exactReleaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(exactReleaseDateTemp.substring(0, 10));
                }
                if (!(deadlineTemp == null)) {
                    this.deadline = new SimpleDateFormat("yyyy-MM-dd").parse(deadlineTemp.substring(0, 10));
                } else {

                }

            } else {
                db.repairDatabase(id);
                JOptionPane.showMessageDialog(null, "An error occured while getting an Entry, try again.");
                //This error will occur when an ID is not present in a table, thus
                //this method seeks to rectify it.
            }
        } catch (SQLException | ParseException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Retrieves ID
     *
     * @return - int
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves title
     *
     * @return - String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves creator
     *
     * @return - String
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Retrieves year
     *
     * @return - String
     */
    public String getYear() {
        return year;
    }

    /**
     * Retrieves type
     *
     * @return - String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves dateAdded
     *
     * @return - Date
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * Retrieves percentage
     *
     * @return - double
     */
    public double getPercentage() {
        calcPercentage(); //final check just in case;
        return percentage;
    }

    /**
     * Retrieves totalLength
     *
     * @return - int
     */
    public int getTotalLength() {
        return totalLength;
    }

    /**
     * Retrieves lengthType
     *
     * @return - String
     */
    public String getLengthType() {
        return lengthType;
    }

    /**
     * Retrieves completedLength
     *
     * @return - int
     */
    public int getCompletedLength() {
        return completedLength;
    }

    /**
     * Retrieves dateCompleted
     *
     * @return - Date
     */
    public Date getDateCompleted() {
        return dateCompleted;
    }

    /**
     * Retrieves note
     *
     * @return - String
     */
    public String getNote() {
        return note;
    }

    /**
     * Retrieves rating
     *
     * @return - int
     */
    public int getRating() {
        return rating;
    }

    /**
     * Retrieves exactReleaseDate
     *
     * @return - Date
     */
    public Date getExactReleaseDate() {
        return exactReleaseDate;
    }

    /**
     * Retrieves medium
     *
     * @return - String
     */
    public String getMedium() {
        return medium;
    }

    /**
     * Retrieves subtitle
     *
     * @return - String
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Retrieves ownership
     *
     * @return - String
     */
    public String getOwnership() {
        return ownership;
    }

    /**
     * Retrieves wishlist
     *
     * @return - boolean
     */
    public boolean getWishlist() {
        return wishlist;
    }

    /**
     * Retrieves deadline
     *
     * @return - Date
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * Retrieves group
     *
     * @return - int[]
     */
    public int[] getGroups() {
        return groups;
    }

    /**
     * Sets title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets creator
     *
     * @param creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Sets year
     *
     * @param year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Sets type
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets dateAdded
     *
     * @param dateAdded
     */
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * Sets totalLength
     *
     * @param totalLength
     */
    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
        calcPercentage();
    }

    /**
     * Sets lengthType
     *
     * @param lengthType
     */
    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    /**
     * Sets completedLength
     *
     * @param completedLength
     */
    public void setCompletedLength(int completedLength) {
        this.completedLength = completedLength;
        calcPercentage();
    }

    /**
     * Sets dateCompleted
     *
     * @param dateCompleted
     */
    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    /**
     * Sets note
     *
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Sets rating
     *
     * @param rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Sets exactReleaseDate
     *
     * @param exactReleaseDate
     */
    public void setExactReleaseDate(Date exactReleaseDate) {
        this.exactReleaseDate = exactReleaseDate;
    }

    /**
     * Sets medium
     *
     * @param medium
     */
    public void setMedium(String medium) {
        this.medium = medium;
    }

    /**
     * Sets subtitle
     *
     * @param subtitle
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Sets ownership
     *
     * @param ownership
     */
    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    /**
     * Sets wishlist
     *
     * @param wishlist
     */
    public void setWishlist(boolean wishlist) {
        this.wishlist = wishlist;
    }

    /**
     * Sets deadline
     *
     * @param deadline
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Sets groups
     *
     * @param groups
     */
    public void setGroups(int[] groups) {
        this.groups = groups;
    }

    /**
     * Calculates the percentage of an entry from completedLength divided by
     * totalLength, which doesn't have a public setter for syncing reasons.
     *
     * @return - double
     */
    private void calcPercentage() {
        if (totalLength != 0) {
            this.percentage = 100 * ((completedLength * 1.0) / totalLength);
        } else {
            this.percentage = 0;
        }
    }

    /**
     * Returns a string of all the fields.
     *
     * @return - String
     */
    @Override
    public String toString() {
        String temp = "";
        temp += "[" + this.type + "] " + this.id + "  " + this.title;
        if (this.subtitle != null) {
            temp += " (" + this.subtitle + ")";
        }
        temp += " - " + this.creator + " (" + this.year + ")   \n" + this.percentage + "% " + "{" + this.completedLength + "/" + this.totalLength;
        if (!"[none]".equals(this.lengthType)) {
            temp += " " + this.lengthType;
        }
        temp += "}   \n\t";

        if (this.exactReleaseDate != null) {
            temp += "Release Date: " + this.exactReleaseDate + "   \n\t";
        }
        if (this.dateAdded != null) {
            temp += "Date Added: " + this.dateAdded + "   \n\t";
        }
        if (this.dateCompleted != null) {
            temp += "Date Completed: " + this.dateCompleted + "   \n\t";
        }
        if (this.medium != null) {
            temp += "Medium: " + this.medium + "   \n\t";
        }
        if (this.ownership != null) {
            temp += "Ownership: " + this.ownership + "   \n\t";
        }
        if (this.rating > 0) {
            temp += "Rating: " + this.rating + "/10   \n\t";
        }
        if (this.note != null) {
            temp += "Note: " + this.note + "   \n\t";
        }
        if (this.wishlist) {
            temp += "Wanted to consume ";
        }
        if (this.deadline != null) {
            temp += "on " + this.deadline + "   \n\t";
        }
        /*
        This method seems much more complex than it needs to be, but it's
        to create a much more pleasing layout for when it gets exported or
        shown in the tooltip.
        */
        return (temp);
    }

}
