package eu.codlab.network.inspect.library.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context) 
	{
		super(context, SGBD.DATABASE_NAME, null, SGBD.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(SGBD.CREATE_INTERFACES);
		db.execSQL(SGBD.CREATE_INTERFACES_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, 
			int newVersion) 
	{
		onCreate(db);
		try{
			if(newVersion == 2){
				db.execSQL(SGBD.ADD_INTERFACES_ADDRESS);
			}
		}catch(Exception e){ }
		try{
			if(newVersion == 2){
				db.execSQL(SGBD.ADD_INTERFACES_FLAG);
			}
		}catch(Exception e){ }
		try{
			if(newVersion == 2){
				db.execSQL(SGBD.ADD_INTERFACES_DATA_ADDRESS);
			}
		}catch(Exception e){ }
		try{
			if(newVersion == 2){
				db.execSQL(SGBD.ADD_INTERFACES_DATA_FLAG);
			}
		}catch(Exception e){ }
	}
}
