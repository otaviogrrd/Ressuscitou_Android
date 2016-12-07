package br.org.cn.ressuscitou;

public class Audio {
	String titulo;
	String tamanho;
	String html;

	public Audio(String titulo, String tamanho, String html) {
		super();
		this.titulo = titulo;
		this.tamanho = tamanho;
		this.html = html;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTamanho() {
		return tamanho;
	}

	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}


}
