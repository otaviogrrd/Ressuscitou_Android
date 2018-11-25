package br.org.cn.ressuscitou;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Base64;

public class CantosClass extends Application {

	public ArrayList<Canto> listCantos = new ArrayList<Canto>();
	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		in = null;
		out.flush();
		out.close();
		out = null;
	}

	public void popular(Context context) {

		Gson gson = new Gson();
		String json = null;
		try {

			InputStream in = null;
			OutputStream out = null;

			AssetManager assetManager = getAssets();
			in = assetManager.open("styles.css");
			out = openFileOutput("styles.css", Context.MODE_PRIVATE);
			copyFile(in, out);

			in = context.getAssets().open("cantos.json");

			settings = context.getSharedPreferences(PREFS_NAME, 0);
			if (settings.getInt("cantosVersao", 0) > 0) {
				in = context.openFileInput("cantos.json");
			}

			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();

			json = new String(buffer, "UTF-8");

			listCantos.clear();
			listCantos = gson.fromJson(json, new TypeToken<ArrayList<Canto>>() {
					}.getType());

			for (int i = 0; i < listCantos.size(); i++) {
				byte[] decodedByteArray = Base64.decode(listCantos.get(i).getExt_base64(), Base64.DEFAULT);
				FileOutputStream fos = openFileOutput("EXT_" + listCantos.get(i).getHtml() + ".HTML",
						Context.MODE_PRIVATE);
				fos.write(decodedByteArray);
				fos.close();
			}
			for (int i = 0; i < listCantos.size(); i++) {
				byte[] decodedByteArray = Base64.decode(listCantos.get(i).getHtml_base64(), Base64.DEFAULT);
				FileOutputStream fos = openFileOutput(listCantos.get(i).getHtml() + ".HTML", Context.MODE_PRIVATE);
				fos.write(decodedByteArray);
				fos.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
