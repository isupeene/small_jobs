package com.smalljobs.jobseeker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.util.JsonReader;


public class DataManager {
	private String URL;
	
	public DataManager() {
		this.URL="http://162.157.47.189:8000/job_seeking/jobs/";
	}
	
	public ArrayList<JobPosting> loadPostings() throws IOException {
		System.out.println("aaaahh2");
		URL url = new URL(URL);
		HttpURLConnection conn =
				(HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		//BufferedReader rd = new BufferedReader(
		//    new InputStreamReader(conn.getInputStream()));

		ArrayList<JobPosting> postings = readJsonStream(conn.getInputStream());
		//		  StringBuilder sb = new StringBuilder();
		//		  String line;
		//		  while ((line = rd.readLine()) != null) {
		//		    sb.append(line);
		//		  }
		//		  rd.close();

		System.out.println("aaaahh");
		conn.disconnect();
		return postings;
	}

	public ArrayList<JobPosting> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readPostingsArray(reader);
		} finally {
			reader.close();
		}
	}

	public ArrayList<JobPosting> readPostingsArray(JsonReader reader) throws IOException {
		ArrayList<JobPosting> postings = new ArrayList<JobPosting>();

		reader.beginArray();
		while (reader.hasNext()) {
			postings.add(readPostings(reader));
		}
		reader.endArray();
		return postings;
	}

	public JobPosting readPostings(JsonReader reader) throws IOException {
		long id=-1;
		JobPosting posting = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("pk")) {
				id = reader.nextLong();
			} else if (name.equals("fields")) {
				posting = readPosting(id, reader);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return posting;
	}

	public JobPosting readPosting(long id, JsonReader reader) throws IOException {
		String title = null;
		String description = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("short_description")) {
				title = reader.nextString();
			} else if (name.equals("description")) {
				description = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new JobPosting(id,title,description);
	}
//	
//	public static void main(String [] args) throws IOException	{
//		DataManager dm = new DataManager();
//		String postings = dm.loadPostings();
//		System.out.println(postings);
//	}
}
