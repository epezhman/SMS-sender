package com.aps.safirsms;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "SMSStatManager";

	private static final String TABLE_SMSStats = "SMSstats";

	private static final String KEY_ID = "id";
	private static final String KEY_SMSId = "smsId";
	private static final String KEY_Stat = "stat";

	// private static final String Key_Date = "sen";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SMSStat_TABLE = "CREATE TABLE " + TABLE_SMSStats + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SMSId + " TEXT,"
				+ KEY_Stat + " TEXT" + ")";
		db.execSQL(CREATE_SMSStat_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMSStats);
		onCreate(db);
	}

	public void addSmsStat(String SmsId, int stat) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SMSId, SmsId);
		values.put(KEY_Stat, stat);

		db.insert(TABLE_SMSStats, null, values);
		// db.close();
	}

	public void addSmsStatIfNoExist(String SmsId, int stat) {
		SQLiteDatabase db = this.getWritableDatabase();

		SMSStat temp = getSmsStatWithSMSId(SmsId);
		if (temp != null)
			return ;
		
		ContentValues values = new ContentValues();
		values.put(KEY_SMSId, SmsId);
		values.put(KEY_Stat, stat);

		db.insert(TABLE_SMSStats, null, values);
		// db.close();
	}

	
	
	public SMSStat getSmsStat(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SMSStats, new String[] { KEY_ID,
				KEY_SMSId, KEY_Stat }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		SMSStat sms = new SMSStat();
		sms.Id = Integer.parseInt(cursor.getString(0));
		sms.SMSId = cursor.getString(1);
		sms.Status = Integer.parseInt(cursor.getString(2));
		// db.close();
		return sms;
	}

	public SMSStat getSmsStatWithSMSId(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

//		Cursor cursor = db.query(TABLE_SMSStats, new String[] { KEY_ID,
//				KEY_SMSId, KEY_Stat }, KEY_SMSId + "=?",
//				new String[] { String.valueOf(id) }, null, null, null, null);
		
		try {
			String selectQuery = "SELECT  * FROM " + TABLE_SMSStats + " WHERE " + KEY_SMSId + " = " + id;
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			if (cursor != null)
			{
				cursor.moveToFirst();
				
			}
			else
			{
					

				return null;
			}

			SMSStat sms = new SMSStat();
			sms.Id = Integer.parseInt(cursor.getString(0));
			sms.SMSId = cursor.getString(1);
			sms.Status = Integer.parseInt(cursor.getString(2));
			// db.close();
			return sms;
			
		} catch (Exception e) {
			return null;
		}
		
	}

	public List<SMSStat> getAllSmsStats(int count) {
		List<SMSStat> smsStats = new ArrayList<SMSStat>();

		String selectQuery = "SELECT  * FROM " + TABLE_SMSStats + " LIMIT "
				+ count;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				SMSStat stat = new SMSStat();
				stat.Id = Integer.parseInt(cursor.getString(0));
				stat.SMSId = cursor.getString(1);
				stat.Status = Integer.parseInt(cursor.getString(2));
				smsStats.add(stat);
			} while (cursor.moveToNext());
		}
		// db.close();
		return smsStats;
	}
	
	public List<SMSStat> getAllSmsStatsWithID(String id) {
		List<SMSStat> smsStats = new ArrayList<SMSStat>();

		String selectQuery = "SELECT  * FROM " + TABLE_SMSStats + " WHERE " + KEY_SMSId + " = " +  id;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				SMSStat stat = new SMSStat();
				stat.Id = Integer.parseInt(cursor.getString(0));
				stat.SMSId = cursor.getString(1);
				stat.Status = Integer.parseInt(cursor.getString(2));
				smsStats.add(stat);
			} while (cursor.moveToNext());
		}
		// db.close();
		return smsStats;
	}

	public int getSmsStatsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SMSStats;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// cursor.close();
		// db.close();
		return cursor.getCount();
	}

	public int getSmsStatsCountWhereNotDeleivered() {
		String countQuery = "SELECT  * FROM  " + TABLE_SMSStats + " WHERE "
				+ KEY_Stat + " = 6 ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// cursor.close();
		// db.close();
		return cursor.getCount();
	}

	public int updateSmsStat(int id, String SmsId, int stat) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SMSId, SmsId);
		values.put(KEY_Stat, stat);
		// db.close();
		return db.update(TABLE_SMSStats, values, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public int updateSmsStatWithoutId(String SmsId, int stat) {
		SQLiteDatabase db = this.getWritableDatabase();

		try {
			SMSStat temp = getSmsStatWithSMSId(SmsId);
			if (temp == null)
				return 0;
			ContentValues values = new ContentValues();
			values.put(KEY_SMSId, SmsId);
			values.put(KEY_Stat, stat);
			// db.close();
			 db.update(TABLE_SMSStats, values, KEY_ID + " = ?",
					new String[] { String.valueOf(temp.Id) });
			
			
			List<SMSStat> ttemp =   getAllSmsStatsWithID(SmsId);
			for(SMSStat tt : ttemp)
			{
				
				 values = new ContentValues();
				values.put(KEY_SMSId, tt.SMSId);
				values.put(KEY_Stat, tt.Status);
				// db.close();
				 db.update(TABLE_SMSStats, values, KEY_ID + " = ?",
						new String[] { String.valueOf(temp.Id) });
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return 0 ;
		
	}

	public void deleteSmsStat(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SMSStats, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		// db.close();
	}

	public void deleteAll() {

		String selectQuery = "SELECT  * FROM " + TABLE_SMSStats;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				db.delete(TABLE_SMSStats, KEY_ID + " = ?",
						new String[] { String.valueOf(Integer.parseInt(cursor
								.getString(0))) });
			} while (cursor.moveToNext());
		}
	}
}
