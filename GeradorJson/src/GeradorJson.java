import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

public class GeradorJson {

	public static void main(String[] args) throws IOException {
		Gson gson = new Gson();

		CantosClass cantosClass = new CantosClass();
		cantosClass.popular();

		for (int i = 0; i < cantosClass.listCantos.size(); i++) {

			String path = "./../html/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			String base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setHtml_base64(base64);

			path = "./../html/ext/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setExt_base64(base64);

			String conteudo = getStrigao(path, cantosClass.listCantos.get(i).getNumero());
			cantosClass.listCantos.get(i).setConteudo(conteudo);

		}

		PrintStream out = new PrintStream(new FileOutputStream("./../cantos.json"));
		out.println(gson.toJson(cantosClass.listCantos));
		out.close();

		PrintStream out2 = new PrintStream(new FileOutputStream("./../Ressuscitou/app/src/main/assets/cantos.json"));
		out2.println(gson.toJson(cantosClass.listCantos));
		out2.close();

		System.out.println("Finalizado");
		;
	}

	public static String getStrigao(String path, String numero) {
		StringBuilder textBuilder = new StringBuilder();
		String line;
		InputStream in;
		try {
			in = new FileInputStream(path);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			textBuilder.append(numero);
			while ((line = bufferedReader.readLine()) != null) {

				if (line.contains("html"))
					continue;
				if (line.contains("body"))
					continue;
				if (line.contains("@capot@"))
					continue;
				if (line.contains("@transp@"))
					continue;
				if (line.contains("#FF0000")) {
					if (!line.contains("#000000") && !line.contains("H2"))
						continue;
				}
				line = line.replaceAll("<FONT COLOR=\"#FF0000\">", "");
				line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
				line = line.replaceAll("<b>", "");
				line = line.replaceAll("</b>", "");
				line = line.replaceAll("<H2>", "");
				line = line.replaceAll("</H2>", "");
				line = line.replaceAll("</FONT>", "");
				line = line.replaceAll("\\*", "");
				line = line.replaceAll("_", "");

				line = line.replaceAll("S.A.  ", "");
				line = line.replaceAll("S.  ", "");
				line = line.replaceAll("A.  ", "");
				line = line.replaceAll("P.  ", "");
				line = line.replaceAll("C.  ", "");
				line = line.replaceAll("S1. ", "");
				line = line.replaceAll("S2. ", "");
				line = line.replaceAll("S3. ", "");
				line = line.replaceAll("A1. ", "");
				line = line.replaceAll("A2. ", "");
				line = line.replaceAll("A3. ", "");
				line = line.replaceAll("melisma", "");

				line = line.replaceAll("Crianças  ", "");
				line = line.replaceAll("   Magos  ", "");
				line = line.replaceAll("Melchior  ", "");
				line = line.replaceAll("  Gaspar  ", "");
				line = line.replaceAll("Baltazar  ", "");

				line = Normalizer.normalize(line, Normalizer.Form.NFD);
				line = line.replaceAll("[^\\p{ASCII}]", "");
				line = line.replaceAll("[-,.;!?\'\":\\(\\)]", "");
				line = line.replaceAll("[ ]", "");
				line = line.toLowerCase();

				textBuilder.append(line);
			}
			in.close();
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return textBuilder.toString();
	}

}
