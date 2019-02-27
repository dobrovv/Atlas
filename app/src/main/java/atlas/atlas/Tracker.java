package atlas.atlas;

import android.content.ContentValues;
import android.database.Cursor;

/* Tracker
 * Class used by the database helper to return information
 * Parameters are described in DatabaseHelper
 * */

public class Tracker {
    public String TrackerID;
    public String TrackerName;
    public String TrackerIcon;
    public Double AllowedDistance;

    Tracker(String id, String name, String icon, Double allowedDistance) {
        this.TrackerID = id;
        this.TrackerName = name;
        this.TrackerIcon = icon;
        this.AllowedDistance = allowedDistance;
    }

    public static Tracker read_from_dbcursor(Cursor cursor) {
        if (cursor == null) return null;

        String TrackerID = cursor.getString(cursor.getColumnIndexOrThrow("TrackerID"));
        String TrackerName = cursor.getString(cursor.getColumnIndexOrThrow("TrackerName"));
        String TrackerIcon = cursor.getString(cursor.getColumnIndexOrThrow("TrackerIcon"));
        Double AllowedDistance = cursor.getDouble(cursor.getColumnIndexOrThrow("AllowedDistance"));

        Tracker tracker = new Tracker(TrackerID, TrackerName, TrackerIcon, AllowedDistance);

        return tracker;
    }

    public ContentValues as_content_values() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TrackerID", TrackerID);
        contentValues.put("TrackerName", TrackerName);
        contentValues.put("TrackerIcon", TrackerIcon);
        contentValues.put("AllowedDistance", AllowedDistance);
        return contentValues;
    }

    public String toString() {
        return String.format("Tracker(ID=\"%s\" Name=\"%s\" Icon=\"%s\" AllowedDistance=%3.1f)", TrackerID, TrackerName, TrackerIcon, AllowedDistance);
    }
}

