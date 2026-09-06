package hostel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AuthDatabase {
    private final String projectUrl;
    private final String publishableKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public AuthDatabase() {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Path.of("supabase.properties"))) {
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Create supabase.properties with your Supabase URL and publishable key.");
        }
        projectUrl = properties.getProperty("supabase.url", "").replaceAll("/rest/v1/?$", "");
        publishableKey = properties.getProperty("supabase.publishableKey", "");
        if (projectUrl.isBlank() || publishableKey.isBlank()) throw new IllegalStateException("Supabase configuration is incomplete.");
    }

    public UserAccount register(String name, String email, String password, String confirmation, String googleId) {
        validatePassword(password, confirmation);
        if (name.isBlank() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Enter a valid name and email address.");
        String body = "{\"email\":" + json(email.trim().toLowerCase()) + ",\"password\":" + json(password) + ",\"data\":{" +
                "\"full_name\":" + json(name.trim()) + ",\"google_id\":" + json(googleId.trim()) + "}}";
        HttpResponse<String> response = request("/auth/v1/signup", "POST", body);
        ensureSuccess(response);
        return new UserAccount(email.trim().toLowerCase(), name.trim(), googleId.trim(), new byte[0], new byte[0]);
    }

    public UserAccount login(String email, String password) {
        HttpResponse<String> response = request("/auth/v1/token?grant_type=password", "POST", "{\"email\":" + json(email.trim().toLowerCase()) + ",\"password\":" + json(password) + "}");
        ensureSuccess(response);
        String returnedEmail = value(response.body(), "email");
        return new UserAccount(returnedEmail.isBlank() ? email.trim().toLowerCase() : returnedEmail, "", "", new byte[0], new byte[0]);
    }

    public void sendResetEmail(String email) {
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Enter a valid email address.");
        HttpResponse<String> response = request("/auth/v1/recover", "POST", "{\"email\":" + json(email.trim().toLowerCase()) + "}");
        ensureSuccess(response);
    }

    private void validatePassword(String password, String confirmation) {
        if (password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters.");
        if (!password.equals(confirmation)) throw new IllegalArgumentException("Passwords do not match.");
    }

    private HttpResponse<String> request(String path, String method, String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(projectUrl + path)).header("apikey", publishableKey).header("Content-Type", "application/json").method(method, HttpRequest.BodyPublishers.ofString(body)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) { throw new IllegalStateException("Could not connect to Supabase. Check your internet connection.", exception); }
        catch (InterruptedException exception) { Thread.currentThread().interrupt(); throw new IllegalStateException("Supabase request was interrupted.", exception); }
    }

    private void ensureSuccess(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IllegalArgumentException(value(response.body(), "msg", "message", "error_description", "error"));
    }

    private String value(String body, String... keys) {
        for (String key : keys) {
            String marker = "\"" + key + "\":"; int start = body.indexOf(marker); if (start < 0) continue;
            start += marker.length(); while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
            if (start < body.length() && body.charAt(start) == '"') { int end = body.indexOf('"', start + 1); if (end > start) return body.substring(start + 1, end); }
        }
        return "Supabase request failed.";
    }

    private String json(String text) { return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""; }
}