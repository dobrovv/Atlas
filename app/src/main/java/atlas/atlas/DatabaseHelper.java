package atlas.atlas;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.util.ArrayList;

/*  DatabaseHeler - a helper class to access the database:
    addNewTracker()     - adds a new tracker
    deleteTracker()     - deletes the tracker and its GPS readings
    updateTracker()     - update the tracker with new information
    getTrackerByID()    - retrieves the tracker by its TrackerID
    hasTrackerID()      - checks if the tracker with TrackerID is in the db
    getAllTrackers()    - returns a list of trackers in the db
    addGPSReading()     - add a new GPS reading to db
    getGPSReadingByID() - retrieves the gps reading by its GPSReadingID
    getGPSReadings()    - get a list of GPS readings for a particular Tracker, the list is sorted - newest first
    getLatestGPSReading() - get the most recent GPS Reading in the DB for a tracker

    recreateDatabase()  - delete all data and recreate the db

    Trackers Table
        TrackerID           (Primary Key)
        TrackerName
        TrackerIcon
        AllowedDistance
        TrackerType
        EnableNotification
    GPSReadings Table
        GPSReadingID        (Primary Key)
        TrackerID           (id of the tracker
        androidTimestamp    (at what time the GPS reading arrived to the phone) time in millis since epoch (long int)
        serverTimestamp     (at what time the GPS reading arrived to the server) time in millis since epoch (long int)
        Latitude
        Longitude
        Speed               Speed in kilometers per hour (double)
        integer GPSSignal  (0 or 1)
        integer GSMSignal  (0 to 30)
        integer BatteryLevel (0 to 100)
        integer PowerStatus (0 or 1)
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "atlas-db";
    public static final int DATABASE_VERSION = 4;
    private static final String TAG = "Atlas"+DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String trackersTable =
                "CREATE TABLE Trackers(TrackerID CHAR(32) PRIMARY KEY, TrackerName TEXT, TrackerIcon TEXT, AllowedDistance REAL, TrackerType VARCHAR(32), EnableNotification INTEGER);";
        String gpsReadingsTable =
                "CREATE TABLE GPSReadings(GPSReadingID INTEGER PRIMARY KEY AUTOINCREMENT, TrackerID VARCHAR(32), androidTimestamp INTEGER, serverTimestamp REAL, Latitude REAL, Longitude REAL, Speed REAL, GSMSignal INTEGER, GPSSignal INTEGER, BatteryLevel INTEGER, PowerStatus INTEGER);";

        db.execSQL(trackersTable);
        db.execSQL(gpsReadingsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Trackers");
        db.execSQL("DROP TABLE IF EXISTS GPSReadings");
        onCreate(db);
    }

    // recreates the database, and deletes the data
    public void recreateDatabase() {
        onUpgrade(getWritableDatabase(), 1, 1);
    }

    // Returns true if tracker was added successfully, TrackerID must be new
    public boolean addNewTracker(Tracker tracker){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (tracker.TrackerID == null) {
            Log.d(TAG,"createNewTracker() TrackerID can't be null");
            return false;
        }

        try {
            ContentValues contentValues = tracker.as_content_values();
            sqLiteDatabase.insertOrThrow("Trackers", null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"createNewTracker() Exception: " + e.getMessage());
            return false;
        } finally {
            sqLiteDatabase.close();
        }

        return true;
    }

    public boolean deleteTracker(String trackerID){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String DELETE_TRACKERS = String.format("DELETE FROM Trackers WHERE TrackerID=\"%s\";", trackerID);
        String DELETE_GPSREADING = String.format("DELETE FROM GPSReadings WHERE TrackerID=\"%s\";", trackerID);

        try {
            sqLiteDatabase.execSQL(DELETE_TRACKERS);
            sqLiteDatabase.execSQL(DELETE_GPSREADING);
        } catch (SQLiteException e){
            Log.d(TAG,"deleteTracker() Exception: " + e.getMessage());
            return false;
        } finally {
            sqLiteDatabase.close();
        }

        return true;
    }
    // Gets the tracker using the id, returns null if tracker is not in the db
    public Tracker getTrackerByID(String TrackerID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String SELECT_QUERY = String.format("SELECT * FROM Trackers WHERE TrackerID=\"%s\";", TrackerID);
        Cursor cursor = null;
        Tracker tracker = null;

        try {
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
            if(cursor.moveToFirst()){
                tracker = Tracker.read_from_dbcursor(cursor);
            }
        } catch (Exception e){
            Log.d(TAG,"getTrackerByID() Exception: " + e.getMessage());
            return null;
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return tracker;
    }
    // returns true if tracker with TrackerID exists in the db
    public boolean hasTrackerID(String TrackerID) {
        return getTrackerByID(TrackerID) != null;
    }

    // return the list of trackers in the db
    public ArrayList<Tracker> getAllTrackers(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String SELECT_QUERY = String.format("SELECT * FROM Trackers;");
        Cursor cursor = null;
        ArrayList<Tracker> trackersArray = new ArrayList<>();

        try {
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
            if(cursor.moveToFirst()){
                do {
                    Tracker tracker  = Tracker.read_from_dbcursor(cursor);
                    trackersArray.add(tracker);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG,"getAllTrackers() Exception: " + e.getMessage());
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return trackersArray;
    }
    // adds a new gps reading to the db and returns its id
    // the id of the gpsReading passed as the parameter is ignored
    // returns 0 if fails
    public long addGPSReading(GPSReading gpsReading){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long gpsReadingID = 0;
        ContentValues contentValues = gpsReading.as_content_values(false);

        try {
            gpsReadingID = sqLiteDatabase.insertOrThrow("GPSReadings", null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"addGPSReading() Exception: " + e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }

        return gpsReadingID;
    }
    // returns a gps reading by id, returns null if reading with given id doesn't exist
    public GPSReading getGPSReadingByID(long gpsReadingID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String SELECT_QUERY = String.format("SELECT * FROM GPSReadings WHERE GPSReadingID=%d;", gpsReadingID);
        Cursor cursor = null;
        GPSReading gpsReading = null;
        try {
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
            if(cursor.moveToFirst()){
                gpsReading = GPSReading.read_from_dbcursor(cursor);
            }
        } catch (Exception e){
            Log.d(TAG,"getGPSReadingByID() Exception: " + e.getMessage());
            return null;
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return gpsReading;
    }

    // get the list of GPS reading for a particular tracker
    // the list is ordered by the anroidTimestamp parameter, newest readings first
    //      trackerID - id of the tracker (optional, returns readings for all trackers if null)
    //      limit - max number of gps reading that will be retrieved from the db
    public ArrayList<GPSReading> getGPSReadings(String trackerID, int limit) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String SELECT_QUERY = (trackerID != null)
                ? String.format("SELECT * FROM GPSReadings WHERE TrackerID=\"%s\" ORDER BY androidTimestamp DESC LIMIT %d;", trackerID, limit)
                : String.format("SELECT * FROM GPSReadings ORDER BY androidTimestamp DESC LIMIT %d;", limit);
        Cursor cursor = null;
        ArrayList<GPSReading> gpsReadingsArray = new ArrayList<>();

        try {
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
            if(cursor.moveToFirst()){
                do {
                    GPSReading reading = GPSReading.read_from_dbcursor(cursor);
                    gpsReadingsArray.add(reading);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG,"getGPSReadings() Exception: " + e.getMessage());
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return gpsReadingsArray;
    }
    // return the most recent GPS reading of the Tracker in the db
    // if tracker doesn't have any gps readings returns null
    //      trackerID - id of the tracker (optional, if null returns the most recent GPS reading of any tracker)
    GPSReading getLatestGPSReading(String trackerID) {
        ArrayList<GPSReading> gpsReadingsArray = getGPSReadings(trackerID, 1);

        if (gpsReadingsArray.size() > 0)
            return gpsReadingsArray.get(0);

        return null;
    }
    // sets new information for the tracker
    // returns true if the tracker is updated (even with equal parameters)
    public boolean updateTracker(Tracker tracker) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (tracker.TrackerID == null) {
            Log.d(TAG,"updateTracker() TrackerID can't be null");
            return false;
        }

        try {
            ContentValues contentValues = tracker.as_content_values();
            int numAffected = sqLiteDatabase.update("Trackers", contentValues, "TrackerID=\""+tracker.TrackerID+"\"", null);
            if (numAffected == 0) {
                Log.d(TAG,"updateTracker() update had no effect");
                return false;
            }
        } catch (SQLiteException e){
            Log.d(TAG,"updateTracker() Exception: " + e.getMessage());
            return false;
        } finally {
            sqLiteDatabase.close();
        }

        return true;
    }
}
