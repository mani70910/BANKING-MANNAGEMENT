import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/Create_Account")
public class Create_Account extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (isValidUser(username, email)) {
            if (updatePassword(username, email, password)) {
                sendSuccessMessage(response, "Password Updated Successfully");
            } else {
                sendErrorMessage(response, "Error in updating password");
            }
        } else {
            sendErrorMessage(response, "Invalid username or email");
        }
    }

    private boolean isValidUser(String username, String email) {
        String url = "jdbc:mysql://localhost:3306/bankingsystem";
        String user = "root";
        String password = "BankingSystem@12";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT * FROM password_management WHERE username = ? AND email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if there's a match, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updatePassword(String username, String email, String password) {
        String url = "jdbc:mysql://localhost:3306/bankingsystem";
        String user = "root";
        String dbPassword = "BankingSystem@12";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "UPDATE password_management SET password = ? WHERE username = ? AND email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, password);
            statement.setString(2, username);
            statement.setString(3, email);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Returns true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendSuccessMessage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head>");
        out.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
        out.println("<title>Success</title></head><body>");
        out.println("<script>");
        out.println("Swal.fire({");
        out.println("  title: 'Success',");
        out.println("  text: '" + message + "',");
        out.println("  icon: 'success'");
        out.println("}).then(() => { window.location.href='index.html'; });");
        out.println("</script>");
        out.println("</body></html>");
        out.close();
    }

    private void sendErrorMessage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head>");
        out.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
        out.println("<title>Error</title></head><body>");
        out.println("<script>");
        out.println("Swal.fire({");
        out.println("  title: 'Error',");
        out.println("  text: '" + message + "',");
        out.println("  icon: 'error'");
        out.println("}).then(() => { window.location.href='Create_Account.html'; });");
        out.println("</script>");
        out.println("</body></html>");
        out.close();
    }
}
