package atlas.atlas;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/* GPSReading
 * Class used by the database helper to return information
 * Parameters are described in DatabaseHelper
 * */

//"CREATE TABLE GPSReadings(GPSReadingID INTEGER PRIMARY KEY AUTOINCREMENT,
// TrackerID VARCHAR(5), androidTimestamp INTEGER, serverTimestamp REAL, Latitude REAL,
// Longitude REAL, Speed REAL, GSMSignal INTEGER, GPSSignal INTEGER, BatteryLevel INTEGER, PowerStatus INTEGER);";


public class GPSReading {

    private static final String TAG = "Atlas"+DatabaseHelper.class.getSimpleName();

    public Long GPSReadingID;
    public String TrackerID;
    public Long androidTimestamp;
    public Double serverTimestamp;
    public Double Latitude;
    public Double Longitude;
    public Double Speed;
    public Integer GSMSignal;
    public Integer GPSSignal;
    public Integer BatteryLevel;
    public Integer PowerStatus;

    GPSReading(Long id, String trackerID, Long androidTimestamp, Double serverTimestamp, Double latitude, Double longitude, Double speed, Integer gSMSignal, Integer gPSSignal, Integer batteryLevel, Integer powerStatus) {
        this.GPSReadingID = id;
        this.TrackerID = trackerID;
        this.androidTimestamp = androidTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Speed = speed;
        this.GSMSignal = gSMSignal;
        this.GPSSignal = gPSSignal;
        this.BatteryLevel = batteryLevel;
        this.PowerStatus = powerStatus;
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
            Integer GSMSignal = cursor.getInt(cursor.getColumnIndexOrThrow("GSMSignal"));
            Integer GPSSignal = cursor.getInt(cursor.getColumnIndexOrThrow("GPSSignal"));
            Integer BatteryLevel = cursor.getInt(cursor.getColumnIndexOrThrow("BatteryLevel"));
            Integer PowerStatus = cursor.getInt(cursor.getColumnIndexOrThrow("PowerStatus"));
            return new GPSReading(GPSReadingID, TrackerID, androidTimestamp, serverTimestamp, Latitude, Longitude, Speed, GSMSignal, GPSSignal,BatteryLevel,PowerStatus);
        } catch (Exception ex) {
            Log.d(TAG, "GPSReading.read_from_dbcursor() Exception: " +  ex.getMessage());
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
        contentValues.put("GSMSignal", GSMSignal);
        contentValues.put("GPSSignal", GPSSignal);
        contentValues.put("BatteryLevel", BatteryLevel);
        contentValues.put("PowerStatus", PowerStatus);

        return contentValues;
    }

    public String toString() {
        return String.format("GPSReading(ID=%d TrackerID=\"%s\" Timestamp=%d (%3.6f %3.6f))", GPSReadingID, TrackerID, serverTimestamp, Latitude, Longitude);
    }

}
