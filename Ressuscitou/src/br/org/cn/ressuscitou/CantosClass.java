package br.org.cn.ressuscitou;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class CantosClass extends Application {

	public ArrayList<Canto> listCantos = new ArrayList<Canto>();
	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;


	public void popular() {

		Gson gson = new Gson();
		String json = null;
		try {

			InputStream in = null;

			in = getApplicationContext().getAssets().open("cantos.json");

			settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
			if (settings.getInt("cantosVersaoDown", 0) > settings.getInt("cantosVersaoAssets", 12)) {
				String path = getFilesDir().getAbsolutePath();
				File file = new File(path, "cantos.json");
				if (file.exists()) {
					in = getApplicationContext().openFileInput("cantos.json");
				} else {
					editor = settings.edit();
					editor.putInt("cantosVersaoDown",0);
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
			listCantos = gson.fromJson(json, new TypeToken<ArrayList<Canto>>() {
			}.getType());

			for (int i = 0; i < listCantos.size(); i++) {
				byte[] decodedByteArray = Base64.decode(listCantos.get(i).getExt_base64(), Base64.DEFAULT);
				FileOutputStream fos = getApplicationContext().openFileOutput("EXT_" + listCantos.get(i).getHtml() + ".HTML",
						Context.MODE_PRIVATE);
				fos.write(decodedByteArray);
				fos.flush();
				fos.close();
				fos = null;
			}
			for (int i = 0; i < listCantos.size(); i++) {
				byte[] decodedByteArray = Base64.decode(listCantos.get(i).getHtml_base64(), Base64.DEFAULT);
				FileOutputStream fos = getApplicationContext().openFileOutput(listCantos.get(i).getHtml() + ".HTML", Context.MODE_PRIVATE);
				fos.write(decodedByteArray);
				fos.flush();
				fos.close();
				fos = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
