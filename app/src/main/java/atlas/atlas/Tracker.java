package atlas.atlas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/* Tracker
 * Class used by the database helper to return information
 * Parameters are described in DatabaseHelper
 * */

public class Tracker {
    public String TrackerID;
    public String TrackerName;
    public String TrackerIcon;
    public String TrackerIconNum;
    public Double AllowedDistance;
    public String TrackerType;
    public Integer EnableNotification;

    Tracker(String id, String name, String icon, String iconNum,Double allowedDistance, String trackerType, Integer enableNotification) {
        this.TrackerID = id;
        this.TrackerName = name;
        this.TrackerIcon = icon;
        this.TrackerIconNum = iconNum;
        this.AllowedDistance = allowedDistance;
        this.TrackerType = trackerType;
        this.EnableNotification = enableNotification;
    }

    public static Tracker read_from_dbcursor(Cursor cursor) {
        if (cursor == null) return null;

        String TrackerID = cursor.getString(cursor.getColumnIndexOrThrow("TrackerID"));
        String TrackerName = cursor.getString(cursor.getColumnIndexOrThrow("TrackerName"));
        String TrackerIcon = cursor.getString(cursor.getColumnIndexOrThrow("TrackerIcon"));
        String TrackerIconNum = cursor.getString(cursor.getColumnIndexOrThrow("TrackerIconNum"));
        Double AllowedDistance = cursor.getDouble(cursor.getColumnIndexOrThrow("AllowedDistance"));
        String TrackerType = cursor.getString(cursor.getColumnIndexOrThrow("TrackerType"));
        Integer EnableNotification = cursor.getInt(cursor.getColumnIndexOrThrow("EnableNotification"));

        Tracker tracker = new Tracker(TrackerID, TrackerName, TrackerIcon, TrackerIconNum, AllowedDistance, TrackerType, EnableNotification);

        return tracker;
    }

    public ContentValues as_content_values() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TrackerID", TrackerID);
        contentValues.put("TrackerName", TrackerName);
        contentValues.put("TrackerIcon", TrackerIcon);
        contentValues.put("TrackerIconNum", TrackerIconNum);
        contentValues.put("AllowedDistance", AllowedDistance);
        contentValues.put("TrackerType", TrackerType);
        contentValues.put("EnableNotification", EnableNotification);
        return contentValues;
    }

    public String toString() {
        return String.format("Tracker(ID=\"%s\" Name=\"%s\" Icon=\"%s\" AllowedDistance=%3.1f)", TrackerID, TrackerName, TrackerIcon, TrackerIconNum,  AllowedDistance);
    }

    public static Bitmap getBitmapFromID(Context context, int imageId) {
        imageId = (imageId != 0) ? imageId : R.mipmap.ic_launcher;
        return drawableToBitmap(context.getDrawable(imageId));
    }

    // convert Drawable to bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}



