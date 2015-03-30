package com.smalljobs.jobseeker;

import java.io.File;
import java.io.FileNotFoundException;
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

public class NotificationsManager {
	private Gson gson;
	private Context context;
	
	private String filename;
	
	public NotificationsManager(Context context, String userName) {
		this.filename = userName + "notifications";
		this.gson=new Gson();
		this.context = context;
	}
	
	public void saveNotification(Notification notification) {
		try {
			ArrayList<Notification> notifications = getNotifications();
			if (notifications != null) {
				if (!notifications.contains(notification)) {
					notifications.add(notification);
				}
			} else {
				notifications = new ArrayList<Notification>();
				notifications.add(notification);
			}
			
			deleteFile();
			OutputStreamWriter osw = new OutputStreamWriter(
					context.openFileOutput(filename, Context.MODE_PRIVATE));
			JsonWriter jw=new JsonWriter(osw);
			gson.toJson(notifications,new TypeToken<ArrayList<Notification>>(){}.getType(),jw);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteNotification(Notification notification) {
		try {
			ArrayList<Notification> notifications=getNotifications();
			notifications.remove(notification);
			deleteFile();
			OutputStreamWriter osw = new OutputStreamWriter(
					context.openFileOutput(filename, Context.MODE_PRIVATE));
			JsonWriter jw=new JsonWriter(osw);
			gson.toJson(notifications,new TypeToken<ArrayList<Notification>>(){}.getType(),jw);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Notification> getNotifications() {
		InputStreamReader in;
		try {
			in = new InputStreamReader(context.openFileInput(filename));
			JsonReader reader=new JsonReader(in);
			ArrayList<Notification> notifications=gson.fromJson(reader, new TypeToken<ArrayList<Notification>>(){}.getType());
			in.close();
			System.out.println(notifications.isEmpty());
			return notifications;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Empty!!!");
		return new ArrayList<Notification>();
	}
	
	public boolean deleteFile() {
		String dir=context.getFilesDir().getAbsolutePath();
		File f=new File(dir,filename);
		return f.delete();
	}
}
