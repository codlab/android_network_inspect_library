package eu.codlab.network.inspect.library.bdd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class SGBD 
{    
	static final String DATABASE_NAME = "data";
	static final String TABLE_INTERFACES = "interfaces";
	static final String TABLE_INTERFACES_DATA = "interface_data";
	static final String TABLE_INTERFACES_DATA_INDEX = "interface_data_index";
	static final int DATABASE_VERSION = 2;//1 : original

	/* 
	 * EXTENSION
	 * id INT primary key
	 * nom VARCHAR 30
	 * 
	 * CARTE
	 * 
	 */
	static final String CREATE_INTERFACES = "create table if not exists "+TABLE_INTERFACES+" (id integer primary key autoincrement, name text, up integer, flag text, address text)";
	static final String CREATE_INTERFACES_DATA = "create table if not exists "+TABLE_INTERFACES_DATA+" (id integer primary key autoincrement, interface_id integer, timestamp integer, up integer, down integer, flag text, address text)";
	static final String CREATE_INTERFACES_DATA_INDEX = "create index if not exists in "+TABLE_INTERFACES_DATA_INDEX+" ON "+TABLE_INTERFACES_DATA+" (interface_id)";
	static final String ADD_INTERFACES_FLAG = "ALTER TABLE "+TABLE_INTERFACES+" ADD COLUMN flag TEXT";
	static final String ADD_INTERFACES_DATA_FLAG = "ALTER TABLE "+TABLE_INTERFACES_DATA+" ADD COLUMN flag TEXT";
	static final String ADD_INTERFACES_ADDRESS = "ALTER TABLE "+TABLE_INTERFACES+" ADD COLUMN address TEXT";
	static final String ADD_INTERFACES_DATA_ADDRESS = "ALTER TABLE "+TABLE_INTERFACES_DATA+" ADD COLUMN address TEXT";
	//private static final String CREATE_POSSESSION_HOLO = "create table if not exists "+TABLE_POSSESSIONS_HOLO+" (_id integer primary key autoincrement,extension integer, carte integer, quantite integer)";
	//private static final String CREATE_POSSESSION_HOLO = "create table if not exists "+TABLE_POSSESSIONS_HOLO+" (_id integer primary key autoincrement,extension integer, carte integer, quantite integer)";


	private final Context context; 

	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase db;

	public SGBD(Context ctx) 
	{
		this.context = ctx;
		if(DBHelper == null)
			DBHelper = new DatabaseHelper(context);
	}

	//---opens the database---
	public SGBD open() throws SQLException{
		if(db == null || !db.isOpen()){
			db = DBHelper.getWritableDatabase();
			db.execSQL(CREATE_INTERFACES);
			db.execSQL(CREATE_INTERFACES_DATA);
		}

		return this;
	}

	//---closes the database---    
	public void close() 
	{
		DBHelper.close();
		db = null;
	}

	public long addInterfaces(String name, boolean up, String flag, String address){
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", name);
		initialValues.put("up", up);
		initialValues.put("flag", flag);
		initialValues.put("address", address);
		return db.insert(TABLE_INTERFACES, null, initialValues);
	}

	public long addInterfaces(String name){
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", name);
		return db.insert(TABLE_INTERFACES, null, initialValues);
	}

	public void updateInterface(long id, String name){
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", name);
		db.update(TABLE_INTERFACES, initialValues, "id="+id, null);
	}

	public void updateInterface(long id, String flag, boolean param){
		ContentValues initialValues = new ContentValues();
		if(param)
			initialValues.put("flag", flag);
		else
			initialValues.put("address", flag);
		db.update(TABLE_INTERFACES, initialValues, "id="+id, null);
	}

	public void updateInterface(long id, boolean up){
		ContentValues initialValues = new ContentValues();
		initialValues.put("up", up);
		db.update(TABLE_INTERFACES, initialValues, "id="+id, null);
	}


	public boolean isUp(long id) throws SQLException{ 
		Cursor mCursor = null;
		Data val = null;
		mCursor = db.query(true, TABLE_INTERFACES, new String[] {
				"up as up",
		}, 
		"id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		boolean up = false;
		if(mCursor.getCount()>0){
			up =mCursor.getInt(mCursor.getColumnIndex("up")) > 0;
		}
		mCursor.close();
		return up;
	}

	public Interface [] getInterfaces(){
		Cursor mCursor = db.query(true, TABLE_INTERFACES, new String[] {
				"id",
				"flag",
				"address",
				"name", "up"},
				null, 
				null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		Interface [] names = null;
		if(mCursor.getCount()>0){
			names = new Interface[mCursor.getCount()];
			String name = "";
			int index = 0;
			boolean up = false;
			long id;
			String flag = "";
			String address = "";
			while(!mCursor.isAfterLast()){
				id = mCursor.getLong(mCursor.getColumnIndex("id"));
				name =mCursor.getString(mCursor.getColumnIndex("name"));
				up =mCursor.getInt(mCursor.getColumnIndex("up")) > 0;
				address =mCursor.getString(mCursor.getColumnIndex("address"));
				flag =mCursor.getString(mCursor.getColumnIndex("flag"));
				mCursor.moveToNext();
				names[index] = new Interface(id, name, up, flag, address);
				index++;
			}
		}
		mCursor.close();


		return names;
	}
	public void deleteInterface(long id){
		db.delete(TABLE_INTERFACES, "id="+id, null);
		deleteInterfaceData(id);
	}

	public void deleteInterfaceData(long interface_id){
		db.delete(TABLE_INTERFACES_DATA, "interface_id="+interface_id, null);
	}


	public long addData(long interface_id, long up, long down, long timestamp, String flag, String address){
		ContentValues initialValues = new ContentValues();
		initialValues.put("interface_id", interface_id);
		initialValues.put("up", up);
		initialValues.put("down", down);
		initialValues.put("flag", flag);
		initialValues.put("address", address);
		initialValues.put("timestamp", timestamp);
		return db.insert(TABLE_INTERFACES_DATA, null, initialValues);
	}


	public DataUpDown  getInterfaceDataSuperior(long id, long max) throws SQLException{ 
		Cursor mCursor = null;
		DataUpDown val = new DataUpDown();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"up as up",
				"down as down",
				"timestamp"
		}, 
		"timestamp > "+max+" and interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._up= new long[mCursor.getCount()];
			val._down= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._up[i] = mCursor.getLong(mCursor.getColumnIndex("up"));
				val._down[i] = mCursor.getLong(mCursor.getColumnIndex("down"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}
	public DataUpDown  getInterfaceData(long id) throws SQLException{ 
		Cursor mCursor = null;
		DataUpDown val = new DataUpDown();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"up as up",
				"down as down",
				"timestamp"
		}, 
		"interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._up= new long[mCursor.getCount()];
			val._down= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._up[i] = mCursor.getLong(mCursor.getColumnIndex("up"));
				val._down[i] = mCursor.getLong(mCursor.getColumnIndex("down"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}

	public Data  getInterfaceDataUpSuperior(long id, long max) throws SQLException{ 
		Cursor mCursor = null;
		Data val = new Data();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"up as up",
				"timestamp"
		}, 
		"timestamp > "+max+" and interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._data= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._data[i] = mCursor.getLong(mCursor.getColumnIndex("up"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}
	public Data  getInterfaceDataUp(long id) throws SQLException{ 
		Cursor mCursor = null;
		Data val = new Data();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"up as up",
				"timestamp"
		}, 
		"interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._data= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._data[i] = mCursor.getLong(mCursor.getColumnIndex("up"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}

	public Data getInterfaceDataDownSuperior(long id , long max) throws SQLException{ 
		Cursor mCursor = null;
		Data val = new Data();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"down as down",
				"timestamp"
		}, 
		"timestamp > "+max+" and interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._data= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._data[i] = mCursor.getLong(mCursor.getColumnIndex("down"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}

	public Data getInterfaceDataDown(long id) throws SQLException{ 
		Cursor mCursor = null;
		Data val = new Data();
		mCursor = db.query(true, TABLE_INTERFACES_DATA, new String[] {
				"down as down",
				"timestamp"
		}, 
		"interface_id=" + id, 
		null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if(mCursor.getCount()>0){
			val._timestamps = new long[mCursor.getCount()];
			val._data= new long[mCursor.getCount()];
			for(int i=0;i<val._timestamps.length;i++){
				val._data[i] = mCursor.getLong(mCursor.getColumnIndex("down"));
				val._timestamps[i] = mCursor.getLong(mCursor.getColumnIndex("timestamp"));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return val;
	}


	/*
	private void writeInterfaces(OutputStreamWriter output, Output mode) throws IOException{
		if(output != null){
			if(mode == Output.XML){
				output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
				output.write("<interfaces>\n");
			}else if(mode == Output.CSV){
				output.write("interface"+
						";id"+
						";up"+
						";down\n");
			}else if(mode == Output.JSON){
				output.write("{interfaces:[");
			}

			//TODO implement FR,ES,IT...
			Cursor cursor = getInterfaces();
			boolean f=false;
			if(cursor != null){
				switch(mode){
				case XML:
					output.write("	<carte e=\""+cursor.getColumnIndex("e")+
							"\" id=\""+cursor.getColumnIndex("c")+
							"\" qn=\""+cursor.getColumnIndex("q")+
							"\" qh=\""+cursor.getColumnIndex("qh")+
							"\" qr=\""+cursor.getColumnIndex("qr")+
							"\" />\n");
					break;
				case JSON:
					if(f)
						output.write(",\n");
					output.write("{" +
							"carte:{" +
							"e:'"+cursor.getColumnIndex("e")+"',"+
							"id:'"+cursor.getColumnIndex("c")+"',"+
							"qn:'"+cursor.getColumnIndex("q")+"',"+
							"qh:'"+cursor.getColumnIndex("qh")+"',"+
							"qr:'"+cursor.getColumnIndex("qr")+"'"+
							"}");
					f=true;
					break;
				case CSV:
					output.write(cursor.getColumnIndex("e")+
							";"+cursor.getColumnIndex("c")+
							";"+cursor.getColumnIndex("q")+
							";"+cursor.getColumnIndex("qh")+
							";"+cursor.getColumnIndex("qr")+"\n");
					break;
				default:

				}
				cursor.moveToNext();
			}
			cursor.close();
			if(mode == Output.XML)
				output.write("</possessions>\n");
			else if(mode == Output.JSON){
				if(f)
					output.write("\n");
				output.write("]}");
			}
		}
	}

	private String getEncodedPossessionsSubLanguage(Language lang){
		String json_envoi="";
		switch(lang){
		case FR:
			json_envoi="data_fr:[";break;
		case DE:
			json_envoi="data_de:[";break;
		case ES:
			json_envoi="data_es:[";break;
		case IT:
			json_envoi="data_it:[";break;
		default:
			json_envoi="data:[";break;
		}
		short last = 0;
		int nb_extension = 0;
		int nb_carte = 0;
		int nb_cartes =0;
		Cursor cursor = getPossessions(lang);
		if(cursor != null){
			while(!cursor.isAfterLast()){
				nb_carte = 0;
				last = cursor.getShort(cursor.getColumnIndex("e"));
				if(nb_extension != 0)
					json_envoi+=",";
				nb_extension++;

				json_envoi+="{e:'"+last+"',c:[";
				while(!cursor.isAfterLast() && cursor.getShort(cursor.getColumnIndex("e")) == last){
					if(cursor.getShort(cursor.getColumnIndex("c")) >= 0){
						if(nb_carte != 0)
							json_envoi+=",";
						nb_carte++;
						json_envoi+="{c:'"+cursor.getShort(cursor.getColumnIndex("c"));
						if(cursor.getShort(cursor.getColumnIndex("q")) > 0)
							json_envoi+="',q:'"+cursor.getShort(cursor.getColumnIndex("q"));
						if(cursor.getShort(cursor.getColumnIndex("qh")) > 0)
							json_envoi+="',qh:'"+cursor.getShort(cursor.getColumnIndex("qh"));
						if(cursor.getShort(cursor.getColumnIndex("qr")) > 0)
							json_envoi+="',qr:'"+cursor.getShort(cursor.getColumnIndex("qr"));
						json_envoi+="'}";
						Log.d("trouve carte", "e: "+cursor.getShort(cursor.getColumnIndex("e"))+" c:"+cursor.getShort(cursor.getColumnIndex("c"))
								+" q:"+cursor.getShort(cursor.getColumnIndex("q"))+
								" qh:"+cursor.getShort(cursor.getColumnIndex("qh"))+
								" qr:"+cursor.getShort(cursor.getColumnIndex("qr")));
						nb_cartes++;
					}
					cursor.moveToNext();
				}
				json_envoi+="]}";
			}
			cursor.close();
		}
		switch(lang){
		case FR:
			json_envoi+="],nb_fr:'"+nb_cartes+"'";break;
		case DE:
			json_envoi+="],nb_de:'"+nb_cartes+"'";break;
		case ES:
			json_envoi+="],nb_es:'"+nb_cartes+"'";break;
		case IT:
			json_envoi+="],nb_it:'"+nb_cartes+"'";break;
		default:
			json_envoi+="],nb:'"+nb_cartes+"'";break;
		}
		return json_envoi;
	}

	public void writePossessionJSON(OutputStreamWriter output) throws IOException{
		writePossessions(output, Output.JSON);
	}
	public void writePossessionXML(OutputStreamWriter output) throws IOException{
		writePossessions(output, Output.XML);
	}
	public void writePossessionCSV(OutputStreamWriter output) throws IOException{
		writePossessions(output, Output.CSV);
	}
	private String readPossessionToString(InputStreamReader input) throws IOException{
		StringWriter res = new StringWriter();
		BufferedReader buffer=new BufferedReader(input);
		String line="";
		while ( null!=(line=buffer.readLine())){
			res.write(line); 
		}
		return res.toString();
	}
	private void readPossession(InputStreamReader input, Output mode) throws IOException, JSONException, XmlPullParserException{
		if(mode == Output.JSON){
			String res = readPossessionToString(input);			
			JSONObject obj = new JSONObject(res);
		}else if(mode == Output.XML){
			XmlPullParser p = XmlPullParserFactory.newInstance().newPullParser();
			p.setInput(input);
		}
	}
	 */
}
