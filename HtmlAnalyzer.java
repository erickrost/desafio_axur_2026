import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("URL connection error");
            return;
        }

        String urlString = args[0];
        Stack<String> stack = new Stack<>();

        int currentDepth = 0;
        int maxDepth = 0;
        String deepestText = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                // Tag de abertura
                if (line.matches("<[^/][^>]*>")) {
                    String tag = line.substring(1, line.length() - 1);
                    stack.push(tag);
                    currentDepth++;

                    // Tag de fechamento
                } else if (line.matches("</[^>]+>")) {
                    if (stack.isEmpty()) {
                        System.out.println("malformed HTML");
                        return;
                    }
                    String tag = line.substring(2, line.length() - 1);
                    String openTag = stack.pop();
                    if (!tag.equals(openTag)) {
                        System.out.println("malformed HTML");
                        return;
                    }
                    currentDepth--;

                    // Texto
                } else {
                    if (currentDepth > maxDepth) {
                        maxDepth = currentDepth;
                        deepestText = line;
                    }
                }
            }

            if (!stack.isEmpty()) {
                System.out.println("malformed HTML");
                return;
            }

            if (deepestText != null) {
                System.out.println(deepestText);
            }

        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }
}