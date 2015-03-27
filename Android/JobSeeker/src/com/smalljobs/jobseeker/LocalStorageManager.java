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
import com.smalljobs.jobseeker.models.Notification;

public class LocalStorageManager {
	private FileOutputStream os;
	private Gson gson;
	
	public LocalStorageManager() {
		this.gson=new Gson();
	}
	
	public void saveNotification(Context context, Notification notification, String filename) {
		try {
			ArrayList<Notification> notifications=getNotifications(context, filename);
			notifications.add(notification);
			deleteFile(context, filename);
			OutputStreamWriter osw = new OutputStreamWriter(
					context.openFileOutput(filename, Context.MODE_PRIVATE));
			JsonWriter jw=new JsonWriter(osw);
			gson.toJson(notifications,new TypeToken<ArrayList<String>>(){}.getType(),jw);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Notification> getNotifications(Context context, String filename) {
		InputStreamReader in;
		try {
			in = new InputStreamReader(context.openFileInput(filename));
			JsonReader reader=new JsonReader(in);
			ArrayList<Notification> notifications=gson.fromJson(reader, new TypeToken<ArrayList<String>>(){}.getType());
			in.close();
			return notifications;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Notification>();
	}
	
	public boolean deleteFile(Context context, String filename) {
		String dir=context.getFilesDir().getAbsolutePath();
		File f=new File(dir,filename);
		return f.delete();
	}
}
