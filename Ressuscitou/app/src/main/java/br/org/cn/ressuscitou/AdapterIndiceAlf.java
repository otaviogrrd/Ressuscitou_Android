package br.org.cn.ressuscitou;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AdapterIndiceAlf extends BaseAdapter implements Filterable {

	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private ArrayList<Canto> listData;
	private ArrayList<Canto> mOriginalValues; // Original Values
	private LayoutInflater layoutInflater;
	private Context context;
	private String listapersonal;

	public AdapterIndiceAlf() {

	}

	public AdapterIndiceAlf(Context aContext, ArrayList<Canto> listData, String listapersonal) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(aContext);
		this.context = aContext;
		this.listapersonal = listapersonal;
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_indice_alf, null);
			holder = new ViewHolder();
			holder.titulo = convertView.findViewById(R.id.title);
			holder.numero = convertView.findViewById(R.id.number);
			holder.img_mp3_avaiable = convertView.findViewById(R.id.img_mp3_avaiable);
			holder.trash = convertView.findViewById(R.id.trash);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.titulo.setText(listData.get(position).getTitulo());

		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		if ( settings.getBoolean("numeracao2015", false ))
			holder.numero.setText(listData.get(position).getNumero());
		else
			holder.numero.setText(listData.get(position).getNr2019());

		if (listData.get(position).getCategoria() == 1)
			holder.numero.setBackgroundResource(R.drawable.dotwhite);
		if (listData.get(position).getCategoria() == 2)
			holder.numero.setBackgroundResource(R.drawable.dotblue);
		if (listData.get(position).getCategoria() == 3)
			holder.numero.setBackgroundResource(R.drawable.dotgreen);
		if (listData.get(position).getCategoria() == 4)
			holder.numero.setBackgroundResource(R.drawable.dotbeige);

		if (listData.get(position).getUrl().isEmpty()) {
			holder.img_mp3_avaiable.setImageResource(R.drawable.aud_n);
		} else {
			Context context = parent.getContext();
			String path = context.getApplicationContext().getFilesDir().getAbsolutePath();
			File file = new File(path, listData.get(position).getHtml() + ".mp3");
			if (file.exists()) {
				holder.img_mp3_avaiable.setImageResource(R.drawable.aud_d);
			} else {
				holder.img_mp3_avaiable.setImageResource(R.drawable.aud_y);
			}
		}
		final int idCanto = listData.get(position).getId();

		if (listapersonal != null) {
			holder.trash.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeCanto(idCanto);
					Toast.makeText(context, "Canto removido da lista.", Toast.LENGTH_SHORT).show();
					listData.remove(position);
					notifyDataSetChanged();
				}
			});
		}else{
			holder.trash.setVisibility(View.GONE);
		}
			return convertView;
	}


	public void removeCanto(int idCanto){
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		String listaJson = settings.getString(listapersonal, "" );
		Gson gson = new Gson();
		CantoList listCanto = gson.fromJson(listaJson, new TypeToken<CantoList>() {}.getType());

		ArrayList<Integer> cantos = listCanto.getCantos();
		ArrayList<Integer> cantos_copy = new ArrayList<>();
		for (int j = 0; j < cantos.size(); j++) {
			if (cantos.get(j) != idCanto ) {
				cantos_copy.add(cantos.get(j));
			}
		}
		listCanto.setCantos(cantos_copy);
		String listas = gson.toJson(listCanto);
		editor.putString(listapersonal,listas);
		editor.apply();
	}

	static class ViewHolder {
		TextView titulo;
		TextView numero;
		ImageView img_mp3_avaiable;
		ImageView trash;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				listData = (ArrayList<Canto>) results.values; // has the filtered values
				notifyDataSetChanged(); // notifies the data with new filtered values
			}

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values
				List<Canto> FilteredArrList = new ArrayList<Canto>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<Canto>(listData); // saves the original data in mOriginalValues
				}

				if (constraint == null || constraint.length() == 0) {
					// set the Original result to return
					results.count = mOriginalValues.size();
					results.values = mOriginalValues;
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						Canto data = mOriginalValues.get(i);

						String strC = constraint.toString();
						strC = Normalizer.normalize(strC, Normalizer.Form.NFD);
						strC = strC.replaceAll("[^\\p{ASCII}]", "");
						strC = strC.replaceAll("[-,.;!?]", "");
						strC = strC.replaceAll("[ ]", "");

						if (data.getConteudo().contains(strC)) {
							FilteredArrList.add(data);
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}
				return results;
			}
		};
		return filter;
	}

}
