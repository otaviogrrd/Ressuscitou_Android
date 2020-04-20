package br.org.cn.ressuscitou;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import android.app.Application;
import android.content.SharedPreferences;

public class CantosClass extends Application {

	public static ArrayList<Canto> listCantos = new ArrayList<Canto>();
	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;


	public int getCantoId( String html) {
		popular();
		for (int i = 0; i < listCantos.size(); i++) {
			if (listCantos.get(i).getHtml().equals(html)) {
				return listCantos.get(i).getId();
			}
		}
		return 0;
	}

	public Canto getCantoByName(String html) {
		popular();
		for (int i = 0; i < listCantos.size(); i++) {
			if (listCantos.get(i).getHtml().equals(html)) {
				return listCantos.get(i);
			}
		}
		return null;
	}


	public void popular() {

		Gson gson = new Gson();
		String json = null;
		try {

			InputStream in = null;

			in = getApplicationContext().getAssets().open("cantos.json");

			settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
			if (settings.getInt("cantosVersaoDown", 0) > settings.getInt("cantosVersaoAssets", 1)) {
				String path = getFilesDir().getAbsolutePath();
				File file = new File(path, "cantos.json");
				if (file.exists()) {
					in = getApplicationContext().openFileInput("cantos.json");
				} else {
					editor = settings.edit();
					editor.putInt("cantosVersaoDown", 0);
					editor.commit();
				}
			}

			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			in = null;

			json = new String(buffer, "UTF-8");

			listCantos.clear();

			JsonReader reader = new JsonReader(new StringReader(json));
			reader.setLenient(true);

			listCantos = gson.fromJson(reader, new TypeToken<ArrayList<Canto>>() {
			}.getType());

		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

}
