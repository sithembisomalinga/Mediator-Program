package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Object class to create a recommendation.
 *
 * @author SITHEMBISO MALINGA
 */
public class Recommendations {

    private static String recom;
    private static int choice;
    private static int entryId;

    /**
     * Default constructor.
     */
    public Recommendations() {
        recom = "";
    }

    /**
     * Generates a number to determine what criteria it needs to search entries
     * by to make a recommendation on (which is either on dateAdded, wishlist,
     * deadline, progress or just a random completed entry for relistening),
     * where thereafter it chooses an entry from the list and sets it as the id.
     */
    private void getRandomEntry() {
        Database db = new Database();
        ResultSet rs = db.selectAllEntries();
        List<Integer> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            int[] entryIDs = new int[list.size()];
            for (int i = 0; i < entryIDs.length; i++) {
                entryIDs[i] = list.get(i);
            }

            List<Integer> filteredList = new ArrayList<>();
            LocalDate now = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            //The use of Date was poorly thought out but since the idea was committed that the
            //Entry and Database uses Date, i could only use LocalDate in small scenarios like this.
            //A possible update may revamp this.
            switch (choice) {
                case 0: //deadline reminder.
                    for (int i = 0; i < entryIDs.length; i++) {
                        Entry en = new Entry(entryIDs[i]);
                        Date due = en.getDeadline();
                        if (due != null) {
                            LocalDate deadline = due.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            int difference = Period.between(deadline, now).getDays();
                            if (difference < 9 && difference > 0) {
                                filteredList.add(en.getId());
                            }
                        }
                    }
                    /*
                    This bottom 4 lines is weird but the explanation is that if
                    the filterlist from a choice is NOT empty (i.e. there are entries
                    that fit the criteria), it breaks the switch case. HOWEVER, if
                    the given filterList is empty, it goes to the next case to see
                    if that yields results. This makes sure that unless there are
                    no entries in the database at all, it should always return 
                    something.
                     */
                    choice = 0;
                    if (!filteredList.isEmpty()) {
                        break;
                    }

                case 1: //wishlisted.
                    for (int i = 0; i < entryIDs.length; i++) {
                        Entry en = new Entry(entryIDs[i]);
                        if (en.getWishlist()) {
                            filteredList.add(en.getId());
                        }
                    }
                    choice = 1;
                    if (!filteredList.isEmpty()) {
                        break;
                    }

                case 2: //almost finished progress.
                    for (int i = 0; i < entryIDs.length; i++) {
                        Entry en = new Entry(entryIDs[i]);
                        if ((en.getPercentage() >= 75.0) && (en.getPercentage() != 100.0)) {
                            filteredList.add(en.getId());
                        }
                    }
                    choice = 2;
                    if (!filteredList.isEmpty()) {
                        break;
                    }

                case 3: //added a while ago.
                    for (int i = 0; i < entryIDs.length; i++) {
                        Entry en = new Entry(entryIDs[i]);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date add = en.getDateAdded();
                        if (add != null) {
                            LocalDate sinceAdded = add.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            int difference = Period.between(sinceAdded, now).getDays();
                            if ((difference < 31 && difference > 0) && en.getPercentage() != 100) {
                                filteredList.add(en.getId());
                            }
                        }
                    }
                    choice = 3;
                    if (!filteredList.isEmpty()) {
                        break;
                    }
            }
            if (!filteredList.isEmpty()) {
                entryId = filteredList.get((int) (Math.random() * filteredList.size()));
            } else {
                choice = 4;
            }

            if (choice == 4) {//random selection
                if (!list.isEmpty()) {
                    entryId = list.get((int) (Math.random() * list.size()));
                } else {
                    entryId = -1; //Prevents error if the database is empty
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    /**
     * Receives the decided entry's id and choice of recommendation to generate
     * an appropriate paragraph with the entry's details.
     */
    private void formatString() {
        Entry en = new Entry(entryId);
        if (entryId != -1) {
            String entryString = en.getCreator() + " - " + en.getTitle() + " (" + en.getYear() + ")";
            LocalDate now = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            switch (choice) {
                case 0:
                    LocalDate deadline = en.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    recom = "Your set deadline for " + entryString + " is coming up in " + Period.between(deadline, now).getDays()
                            + " days. Consider getting it done by then!";
                    break;
                case 1:
                    recom = "You have " + entryString + " wishlisted. Why not try it?";
                    break;
                case 2:
                    recom = "You are almost done with " + entryString + ", current sitting at " + en.getPercentage() + "%. Try finishing it soon!";
                    break;
                case 3:
                    LocalDate added = en.getDateAdded().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    recom = "You added " + entryString + " over " + Period.between(added, now).getDays() + " days ago without consuming it, why not try it now?";
                    break;
                case 4:
                    recom = "Here's a random recommendation: " + entryString + ", consider reconsuming it.";
                    break;
            }
        } else {
            recom = "There are no entries!";
        }
    }

    /**
     * Retrieves recommendation.
     *
     * @return - String
     */
    public String getNewRecommendation() {
        choice = (int) (Math.random() * 5);
        getRandomEntry();
        formatString();
        return recom;
    }
}
