package br.org.cn.ressuscitou;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class AdapterListaPersonal extends BaseAdapter {
	private ArrayList<Audio> listData;
	private LayoutInflater layoutInflater;
	private CheckBox checkBox;
	ArrayList<String> selected = new ArrayList<String>();

	public ArrayList<String> getSelected() {
		return selected;
	}

	public AdapterListaPersonal() {

	}

	public AdapterListaPersonal(Context aContext, ArrayList<Audio> listData) {
		this.setListData(listData);
		layoutInflater = LayoutInflater.from(aContext);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_lista_audio, null);
			holder = new ViewHolder();

			checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.titulo = (TextView) convertView.findViewById(R.id.titulo);
			holder.tamanho = (TextView) convertView.findViewById(R.id.tamanho);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selected.add(getListData().get(position).getHtml());
				} else {
					selected.remove(getListData().get(position).getHtml());
				}

			}
		});

		holder.titulo.setText(getListData().get(position).getTitulo());
		holder.tamanho.setText(getListData().get(position).getTamanho());

		return convertView;
	}

	static class ViewHolder {
		TextView titulo;
		TextView tamanho;
	}

	public ArrayList<Audio> getListData() {
		return listData;
	}

	public void setListData(ArrayList<Audio> listData) {
		this.listData = listData;
	}
}
