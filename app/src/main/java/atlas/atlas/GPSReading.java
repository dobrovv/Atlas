package atlas.atlas;

import android.content.ContentValues;
import android.database.Cursor;

/* GPSReading
 * Class used by the database helper to return information
 * Parameters are described in DatabaseHelper
 * */

public class GPSReading {
    public Long GPSReadingID;
    public String TrackerID;
    public Long androidTimestamp;
    public Double serverTimestamp;
    public Double Latitude;
    public Double Longitude;
    public Double Speed;
    public Integer rawDate;
    public Integer rawTime;

    GPSReading(Long id, String trackerID, Long androidTimestamp, Double serverTimestamp, Double latitude, Double longitude, Double speed, Integer rawDate, Integer rawTime) {
        this.GPSReadingID = id;
        this.TrackerID = trackerID;
        this.androidTimestamp = androidTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Speed = speed;
        this.rawDate = rawDate;
        this.rawTime = rawTime;
    }

    public static GPSReading read_from_dbcursor(Cursor cursor) {
        if (cursor==null) return null;

        try {
            Long GPSReadingID = cursor.getLong(cursor.getColumnIndexOrThrow("GPSReadingID"));
            String TrackerID = cursor.getString(cursor.getColumnIndexOrThrow("TrackerID"));
            Long androidTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow("androidTimestamp"));
            Double serverTimestamp = cursor.getDouble(cursor.getColumnIndexOrThrow("serverTimestamp"));
            Double Latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Latitude"));
            Double Longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Longitude"));
            Double Speed = cursor.getDouble(cursor.getColumnIndexOrThrow("Speed"));
            Integer rawDate = cursor.getInt(cursor.getColumnIndexOrThrow("rawDate"));
            Integer rawTime = cursor.getInt(cursor.getColumnIndexOrThrow("rawTime"));
            return new GPSReading(GPSReadingID, TrackerID, androidTimestamp, serverTimestamp, Latitude, Longitude, Speed, rawDate, rawTime);
        } catch (Exception ex) {
            return null;
        }
    }

    public ContentValues as_content_values(boolean includeGPSReadingID) {
        ContentValues contentValues = new ContentValues();
        if (includeGPSReadingID) {
            contentValues.put("GPSReadingID", GPSReadingID);
        }
        contentValues.put("TrackerID", TrackerID);
        contentValues.put("androidTimestamp", androidTimestamp);
        contentValues.put("serverTimestamp", serverTimestamp);
        contentValues.put("Latitude", Latitude);
        contentValues.put("Longitude", Longitude);
        contentValues.put("Speed", Speed);
        contentValues.put("rawDate", rawDate);
        contentValues.put("rawTime", rawTime);

        return contentValues;
    }

    public String toString() {
        return String.format("GPSReading(ID=%d TrackerID=\"%s\" Timestamp=%d (%3.6f %3.6f))", GPSReadingID, TrackerID, serverTimestamp, Latitude, Longitude);
    }

}
