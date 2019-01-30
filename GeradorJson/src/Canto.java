public class Canto {
	String titulo;
	String html;
	String url;
	int categoria;
	String numero;
	boolean adve;
	boolean laud;
	boolean entr;
	boolean nata;
	boolean quar;
	boolean pasc;
	boolean pent;
	boolean virg;
	boolean cria;
	boolean cpaz;
	boolean fpao;
	boolean comu;
	boolean cfin;
	String conteudo;
	String html_base64;
	String ext_base64;

	public Canto() {

	}

	public Canto(String titulo, String html, String url, int categoria, String numero, boolean adve, boolean laud,
			boolean entr, boolean nata, boolean quar, boolean pasc, boolean pent, boolean virg, boolean cria,
			boolean cpaz, boolean fpao, boolean comu, boolean cfin, String conteudo) {
		super();
		this.titulo = titulo;
		this.html = html;
		this.url = url;
		this.categoria = categoria;
		this.numero = numero;
		this.adve = adve;
		this.laud = laud;
		this.entr = entr;
		this.nata = nata;
		this.quar = quar;
		this.pasc = pasc;
		this.pent = pent;
		this.virg = virg;
		this.cria = cria;
		this.cpaz = cpaz;
		this.fpao = fpao;
		this.comu = comu;
		this.cfin = cfin;
		this.conteudo = conteudo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public boolean isAdve() {
		return adve;
	}

	public void setAdve(boolean adve) {
		this.adve = adve;
	}

	public boolean isLaud() {
		return laud;
	}

	public void setLaud(boolean laud) {
		this.laud = laud;
	}

	public boolean isEntr() {
		return entr;
	}

	public void setEntr(boolean entr) {
		this.entr = entr;
	}

	public boolean isNata() {
		return nata;
	}

	public void setNata(boolean nata) {
		this.nata = nata;
	}

	public boolean isQuar() {
		return quar;
	}

	public void setQuar(boolean quar) {
		this.quar = quar;
	}

	public boolean isPasc() {
		return pasc;
	}

	public void setPasc(boolean pasc) {
		this.pasc = pasc;
	}

	public boolean isPent() {
		return pent;
	}

	public void setPent(boolean pent) {
		this.pent = pent;
	}

	public boolean isVirg() {
		return virg;
	}

	public void setVirg(boolean virg) {
		this.virg = virg;
	}

	public boolean isCria() {
		return cria;
	}

	public void setCria(boolean cria) {
		this.cria = cria;
	}

	public boolean isCpaz() {
		return cpaz;
	}

	public void setCpaz(boolean cpaz) {
		this.cpaz = cpaz;
	}

	public boolean isFpao() {
		return fpao;
	}

	public void setFpao(boolean fpao) {
		this.fpao = fpao;
	}

	public boolean isComu() {
		return comu;
	}

	public void setComu(boolean comu) {
		this.comu = comu;
	}

	public boolean isCfin() {
		return cfin;
	}

	public void setCfin(boolean cfin) {
		this.cfin = cfin;
	}

	public String getHtml_base64() {
		return html_base64;
	}

	public void setHtml_base64(String html_base64) {
		this.html_base64 = html_base64;
	}

	public String getExt_base64() {
		return ext_base64;
	}

	public void setExt_base64(String ext_base64) {
		this.ext_base64 = ext_base64;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

}
