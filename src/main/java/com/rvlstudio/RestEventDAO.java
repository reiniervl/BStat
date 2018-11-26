package com.rvlstudio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.UUID;

import org.json.*;

public class RestEventDAO implements EventDAO {
    private URL url = null;
    private String user = "";
    private String password = "";

    private Set<Event<?>> parseJSONArray(JSONArray array) {
        Set<Event<?>> set = new HashSet<>();
        for(Object o : array) {
            if(o instanceof JSONObject) {
                JSONObject jo = (JSONObject)o;
                Unit unit = Unit.fromString(jo.getString("unit"));
                String descr = jo.getString("description");
                UUID uuid = UUID.fromString(jo.getString("uuid"));
                String value = jo.getString("value");
                Calendar cal = null;
                try {
                    cal = Calendar.getInstance();
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo.getString("calendar")));
                } catch(ParseException e) {
                    cal = Calendar.getInstance(); 
                    cal.setTimeInMillis(1L);
                    System.out.format("Unable to parse calendar\n");
                }
                
                if(unit.equals(Unit.MILILITER) || unit.equals(Unit.GRAM)) set.add(new Event<Integer>(Integer.parseInt(value), descr, cal, unit, uuid));
                else if(unit.equals(Unit.LITER) || unit.equals(Unit.KILO)) set.add(new Event<Double>(Double.parseDouble(value), descr, cal, unit, uuid));
                else set.add(new Event<String>(value, descr, cal, unit, uuid));
            }
        }
        return set;
    }

    public RestEventDAO(ResourceBundle bundle) {
        String url = bundle.getString("url");
        this.user = bundle.getString("user");
        this.password = bundle.getString("password");
        try {
            this.url = new URL(url);
        } catch(MalformedURLException e) {
            System.out.format("Error in url \"%s\" -> %s\n", url, e.getMessage());
        }
    }

	@Override
	public Event<?> getEvent(UUID id) {
        StringBuilder response = new StringBuilder();
        try {
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String params = "action=READ_ONE&user="+ user + "&password=" + password + "&e_uuid=" + id.toString();

            con.setDoOutput(true);
            try(DataOutputStream oStream = new DataOutputStream(con.getOutputStream())) {
                oStream.writeBytes(params);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) response.append(line);

            JSONObject jsonObject = new JSONObject(new JSONTokener(response.toString()));
            JSONArray events = jsonObject.getJSONArray("events");
            if(events.get(0) instanceof JSONObject) {
                JSONObject jo = (JSONObject)events.get(0);
                Unit unit = Unit.fromString(jo.getString("unit"));
                String descr = jo.getString("description");
                UUID uuid = UUID.fromString(jo.getString("uuid"));
                String value = jo.getString("value");
                Calendar cal = null;
                try {
                    cal = Calendar.getInstance();
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo.getString("calendar")));
                } catch(ParseException e) {
                    cal = Calendar.getInstance(); 
                    cal.setTimeInMillis(1L);
                    System.out.format("Unable to parse calendar\n");
                }
                
                if(unit.equals(Unit.MILILITER) || unit.equals(Unit.GRAM)) return new Event<Integer>(Integer.parseInt(value), descr, cal, unit, uuid);
                else if(unit.equals(Unit.LITER) || unit.equals(Unit.KILO)) return new Event<Double>(Double.parseDouble(value), descr, cal, unit, uuid);
                else return new Event<String>(value, descr, cal, unit, uuid);
            }
        } catch(JSONException e) {
            System.out.println(response);
        } catch(IOException e) {
            System.out.format("Error establishing connection -> %s", e.getMessage());
        }
        return null;
	}

	@Override
	public Set<Event<?>> getEvents(Calendar start, Calendar end) {
        String sz_start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start.getTime());
        String sz_end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end.getTime());
        try {
            StringBuilder params = new StringBuilder();
            params.append("&action=");
            params.append("READ_RANGE");
            params.append("&user=");
            params.append(user);
            params.append("&password=");
            params.append(password);
            params.append("&e_start=");
            params.append(sz_start);
            params.append("&e_end=");
            params.append(sz_end);

            byte[] encodedParams = params.toString().getBytes(StandardCharsets.UTF_8);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.addRequestProperty("Content-Length", String.valueOf(encodedParams.length));

            con.setDoOutput(true);
            try(DataOutputStream writer = new DataOutputStream(con.getOutputStream())) {
                writer.write(encodedParams);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) response.append(line);

            JSONObject jsonObject = new JSONObject(new JSONTokener(response.toString()));
            if(jsonObject.getInt("count") > 0) {
                JSONArray events = jsonObject.getJSONArray("events");
                return parseJSONArray(events);
            }
        } catch(IOException | JSONException e) {
            System.out.println("Error retreiving events: " + e.getMessage());
        }
		return new HashSet<Event<?>>();
	}

	@Override
	public Set<Event<?>> getEvents(String description) {
        try {
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String params = "action=READ_ALL&user="+ user + "&password=" + password;

            con.setDoOutput(true);
            DataOutputStream oStream = new DataOutputStream(con.getOutputStream());
            oStream.writeBytes(params);
            oStream.flush();
            oStream.close();

            JSONObject jsonObject = new JSONObject(new JSONTokener(con.getInputStream()));
            JSONArray events = jsonObject.getJSONArray("events");
            return parseJSONArray(events);
        } catch(IOException e) {
            System.out.format("Error establishing connection -> %s", e.getMessage());
        }
        return null;
	}

	@Override
	public void addEvent(Event<?> event) {
        StringBuilder response = new StringBuilder();
        try {
            StringBuilder params = new StringBuilder();
            params.append("action=");
            params.append("ADD");
            params.append("&user=");
            params.append(user);
            params.append("&password=");
            params.append(password);
            params.append("&e_uuid=");
            params.append(event.getId().toString());
            params.append("&e_description=");
            params.append(event.getDescription());
            params.append("&e_value=");
            params.append(event.getValue().toString());
            params.append("&e_calendar=");
            params.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getCalendar().getTime()));
            params.append("&e_unit=");
            params.append(event.getUnit().toString());
            byte[] encodedParams = params.toString().getBytes("UTF-8");

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.addRequestProperty("Content-Length", String.valueOf(encodedParams.length));

            con.setDoOutput(true);
			con.getOutputStream().write(encodedParams);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) response.append(line);

			JSONObject jsonObject = new JSONObject(new JSONTokener(response.toString()));
			String res = jsonObject.getString("success");
			if(res.equals("true")) System.out.println("Addition successful");
		} catch(JSONException e) {
			System.out.println("Error ADD response: " + e.getMessage());
			System.out.println(response);
        } catch(IOException e) {
            System.out.format("Error establishing connection: %s\n", e.getMessage());
            for(StackTraceElement el : e.getStackTrace()) {
                System.out.println(el);
            }
        }
	}

	@Override
	public void updateEvent(Event<?> event) {
        try {
            StringBuilder params = new StringBuilder();
            params.append("action=");
            params.append("UPDATE");
            params.append("&user=");
            params.append(user);
            params.append("&password=");
            params.append(password);
            params.append("&e_uuid=");
            params.append(event.getId().toString());
            params.append("&e_description=");
            params.append(event.getDescription());
            params.append("&e_value=");
            params.append(event.getValue().toString());
            params.append("&e_calendar=");
            params.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getCalendar().getTime()));
            params.append("&e_unit=");
            params.append(event.getUnit().toString());
            byte[] encodedParams = params.toString().getBytes("UTF-8");

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.addRequestProperty("Content-Length", String.valueOf(encodedParams.length));

            con.setDoOutput(true);
            con.getOutputStream().write(encodedParams);
            try {
                JSONObject jsonObject = new JSONObject(new JSONTokener(con.getInputStream()));
                String response = jsonObject.getString("success");
                if(response.equals("true")) System.out.println("Update successful");
            } catch(JSONException e) {
                System.out.println("Error UPDATE response: " + e.getMessage());
            }
        } catch(IOException e) {
            System.out.format("Error establishing connection -> %s", e.getMessage());
        }
	}

	@Override
	public void deleteEvent(Event<?> event) {
        try {
            StringBuilder params = new StringBuilder();
            params.append("action=");
            params.append("DELETE");
            params.append("&user=");
            params.append(user);
            params.append("&password=");
            params.append(password);
            params.append("&e_uuid=");
            params.append(event.getId().toString());
            byte[] encodedParams = params.toString().getBytes("UTF-8");

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.addRequestProperty("Content-Length", String.valueOf(encodedParams.length));

            con.setDoOutput(true);
            con.getOutputStream().write(encodedParams);
            try {
                JSONObject jsonObject = new JSONObject(new JSONTokener(con.getInputStream()));
                String response = jsonObject.getString("success");
                if(response.equals("true")) System.out.println("Deletion successful");
            } catch(JSONException e) {
                System.out.println("Error DELETE response: " + e.getMessage());
            }
        } catch(IOException e) {
            System.out.format("Error establishing connection -> %s", e.getMessage());
        }
	}

}
