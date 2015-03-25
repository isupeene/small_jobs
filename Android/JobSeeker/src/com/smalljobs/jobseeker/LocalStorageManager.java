package com.smalljobs.jobseeker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalStorageManager {
	private FileOutputStream os;
	private Gson gson;
	
	public LocalStorageManager() {
		this.gson=new Gson();
	}
	
	public void saveId(Context context, String id, String filename) {
		try {
			ArrayList<String> ids=getIds(context, filename);
			ids.add(id);
			deleteFile(context, filename);
			OutputStreamWriter osw = new OutputStreamWriter(
					context.openFileOutput(filename, Context.MODE_PRIVATE));
			JsonWriter jw=new JsonWriter(osw);
			gson.toJson(ids,new TypeToken<ArrayList<String>>(){}.getType(),jw);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getIds(Context context, String filename) {
		InputStreamReader in;
		try {
			in = new InputStreamReader(context.openFileInput(filename));
			JsonReader reader=new JsonReader(in);
			ArrayList<String> ids=gson.fromJson(reader, new TypeToken<ArrayList<String>>(){}.getType());
			in.close();
			return ids;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public boolean deleteFile(Context context, String filename) {
		String dir=context.getFilesDir().getAbsolutePath();
		File f=new File(dir,filename);
		return f.delete();
	}
}
