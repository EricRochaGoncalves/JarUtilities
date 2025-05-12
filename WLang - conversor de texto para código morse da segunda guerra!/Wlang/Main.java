import java.io.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        File[] arquivosWlang = localizarArquivosWlang();

        if (arquivosWlang == null || arquivosWlang.length == 0) {
            System.out.println("Nenhum arquivo .wlang encontrado no diretório.");
            return;
        }

        for (File arquivo : arquivosWlang) {
            System.out.println("Processando: " + arquivo.getName());

            String conteudo = lerArquivoWlang(arquivo.getPath());
            if (conteudo != null) {
                String mensagem = processarWlang(conteudo);
                if (!mensagem.isEmpty()) {
                    String morse = converterParaMorse(mensagem);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Morse: " + morse);
                } else {
                    System.out.println("Nenhuma mensagem encontrada no arquivo.");
                }
            }
        }
    }

    // Lê o conteúdo do arquivo .wlang
    public static String lerArquivoWlang(String nomeArquivo) {
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }
        return conteudo.toString();
    }

    // Processa as variáveis e a linha com IncMessage()
    public static String processarWlang(String conteudo) {
        HashMap<String, String> variaveis = new HashMap<>();
        String[] linhas = conteudo.split("\n");

        for (String linha : linhas) {
            linha = linha.trim();

            // Definição de variável: Wv nome = "Eric"
            if (linha.startsWith("Wv")) {
                String[] partes = linha.split("=", 2);
                if (partes.length == 2) {
                    String nomeVar = partes[0].replace("Wv", "").trim();
                    String valor = partes[1].trim().replace("\"", "");
                    variaveis.put(nomeVar, valor);
                }
            }

            // Interpretação da mensagem: IncMessage("Meu nome é " + nome + ...)
            if (linha.startsWith("IncMessage(")) {
                String dentroDosParenteses = linha.substring(linha.indexOf("(") + 1, linha.lastIndexOf(")"));
                String[] partes = dentroDosParenteses.split("\\+");

                StringBuilder resultado = new StringBuilder();
                for (String parte : partes) {
                    parte = parte.trim();
                    if (parte.startsWith("\"") && parte.endsWith("\"")) {
                        resultado.append(parte.substring(1, parte.length() - 1));
                    } else if (variaveis.containsKey(parte)) {
                        resultado.append(variaveis.get(parte));
                    } else {
                        resultado.append(parte); // fallback
                    }
                }
                return resultado.toString();
            }
        }
        return "";
    }

    // Converte uma string para código Morse
    public static String converterParaMorse(String texto) {
        HashMap<Character, String> tabelaMorse = new HashMap<>();
        tabelaMorse.put('A', ".-");    tabelaMorse.put('B', "-...");
        tabelaMorse.put('C', "-.-.");  tabelaMorse.put('D', "-..");
        tabelaMorse.put('E', ".");     tabelaMorse.put('F', "..-.");
        tabelaMorse.put('G', "--.");   tabelaMorse.put('H', "....");
        tabelaMorse.put('I', "..");    tabelaMorse.put('J', ".---");
        tabelaMorse.put('K', "-.-");   tabelaMorse.put('L', ".-..");
        tabelaMorse.put('M', "--");    tabelaMorse.put('N', "-.");
        tabelaMorse.put('O', "---");   tabelaMorse.put('P', ".--.");
        tabelaMorse.put('Q', "--.-");  tabelaMorse.put('R', ".-.");
        tabelaMorse.put('S', "...");   tabelaMorse.put('T', "-");
        tabelaMorse.put('U', "..-");   tabelaMorse.put('V', "...-");
        tabelaMorse.put('W', ".--");   tabelaMorse.put('X', "-..-");
        tabelaMorse.put('Y', "-.--");  tabelaMorse.put('Z', "--..");
        tabelaMorse.put('1', ".----"); tabelaMorse.put('2', "..---");
        tabelaMorse.put('3', "...--"); tabelaMorse.put('4', "....-");
        tabelaMorse.put('5', "....."); tabelaMorse.put('6', "-....");
        tabelaMorse.put('7', "--..."); tabelaMorse.put('8', "---..");
        tabelaMorse.put('9', "----."); tabelaMorse.put('0', "-----");
        tabelaMorse.put(' ', "/");

        StringBuilder morse = new StringBuilder();
        for (char c : texto.toUpperCase().toCharArray()) {
            if (tabelaMorse.containsKey(c)) {
                morse.append(tabelaMorse.get(c)).append(" ");
            }
        }
        return morse.toString().trim();
    }

    // Localiza todos os arquivos .wlang no mesmo diretório
    public static File[] localizarArquivosWlang() {
        File diretorioAtual = new File(".");
        return diretorioAtual.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wlang");
            }
        });
    }
}
