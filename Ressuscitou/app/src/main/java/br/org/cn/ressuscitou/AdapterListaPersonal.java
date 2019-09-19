package br.org.cn.ressuscitou;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("InflateParams")
public class AdapterListaPersonal extends BaseAdapter {
	private Context context;
	private ArrayList<CantoList> listData;
	private LayoutInflater layoutInflater;
	ArrayList<String> selected = new ArrayList<String>();

	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	public ArrayList<String> getSelected() {
		return selected;
	}

	public AdapterListaPersonal() {

	}

	public AdapterListaPersonal(Context context, ArrayList<CantoList> listData) {
		this.setListData(listData);
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return getListData().size();
	}


	@Override
	public Object getItem(int position) {
		return getListData().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_lista_personal, null);
			holder = new ViewHolder();

			holder.titulo = convertView.findViewById(R.id.titulo);
			holder.delete = convertView.findViewById(R.id.delete);
			holder.list   = convertView.findViewById(R.id.cantos_list_view);

			holder.titulo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, ActivityIndiceAlfabetico.class);
					intent.putExtra("listaPersonalizada", holder.titulo.getText());
					context.startActivity(intent);
				}
			});
			String titulo = holder.titulo.getText().toString();
			settings = context.getSharedPreferences(PREFS_NAME, 0);
			String listaJson = settings.getString(titulo, "" );

			Gson gson = new Gson();
			CantoList listCantos = gson.fromJson(listaJson, new TypeToken<CantoList>() {}.getType());

			if (listCantos != null) {
				ArrayList<Integer> cantos = listCantos.getCantos();
				ArrayAdapter listadapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, cantos);
				holder.list.setAdapter(listadapter);
			}

			holder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					try{
						AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullHeightDialog);
						builder.setTitle(context.getString(R.string.confirmacao));

						ArrayList<CantoList> data = getListData();
						String titulo = data.get(position).getTitulo();

						final TextView texto = new TextView(context);
						texto.setText(context.getString(R.string.confirm_dele_list) + titulo + " ?");
						builder.setView(texto);

						builder.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ArrayList<CantoList> data = getListData();
								String titulo = data.get(position).getTitulo();
								data.remove(position);
								settings = context.getSharedPreferences(PREFS_NAME, 0);
								Gson gson = new Gson();
								String listas = gson.toJson(data);
								editor = settings.edit();
								editor.putString("Listas",listas);
								editor.putString(titulo,"");
								editor.apply();

								parent.removeViewAt( position );
							}
						});
						builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.show();
					}catch (IndexOutOfBoundsException e){

					}
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.titulo.setText(getListData().get(position).getTitulo());
		return convertView;
	}

	static class ViewHolder {
		TextView titulo;
		ImageButton delete;
		ListView list;
	}

	public ArrayList<CantoList> getListData() {
		return listData;
	}

	public void setListData(ArrayList<CantoList> listData) {
		this.listData = listData;
	}
}
