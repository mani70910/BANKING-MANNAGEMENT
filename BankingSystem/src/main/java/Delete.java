import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Delete")
public class Delete extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DELETE_ADMIN_QUERY = "DELETE FROM admin_dashboard WHERE full_name = ? AND mobile_no = ?";
    private static final String DELETE_PASSWORD_QUERY = "DELETE FROM password_management WHERE username = ?";

    public Delete() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        String fullName = request.getParameter("fullName");
        String mobileNo = request.getParameter("mobileNo");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            pw.println("ClassNotFoundException occurred: " + e.getMessage());
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");
             PreparedStatement psDeleteAdmin = con.prepareStatement(DELETE_ADMIN_QUERY);
             PreparedStatement psDeletePassword = con.prepareStatement(DELETE_PASSWORD_QUERY)) {

            // Delete record from admin_dashboard table
            psDeleteAdmin.setString(1, fullName);
            psDeleteAdmin.setString(2, mobileNo);
            int countAdmin = psDeleteAdmin.executeUpdate();

            // Delete record from password_management table
            psDeletePassword.setString(1, fullName);
            int countPassword = psDeletePassword.executeUpdate();

            if (countAdmin > 0 && countPassword > 0) {
                // Deletion from both tables successful
                pw.println("<html><head>");
                pw.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
                pw.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
                pw.println("</head><body>");
                pw.println("<script>");
                pw.println("Swal.fire({");
                pw.println("    title: 'Success!',");
                pw.println("    text: 'Account Data Deleted Successfully',");
                pw.println("    icon: 'success',");
                pw.println("    showConfirmButton: true,");
                pw.println("    allowOutsideClick: false");
                pw.println("}).then(() => { window.location.href='Admin.html'; });");
                pw.println("</script>");
                pw.println("</body></html>");
            } else {
                // Deletion failed
                pw.println("<html><head>");
                pw.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
                pw.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
                pw.println("</head><body>");
                pw.println("<script>");
                pw.println("Swal.fire({");
                pw.println("    title: 'Failed!',");
                pw.println("    text: 'Failed To Delete (possible reason wrong Credentials)',");
                pw.println("    icon: 'error',");
                pw.println("    showConfirmButton: true,");
                pw.println("    allowOutsideClick: false");
                pw.println("}).then(() => { window.location.href='Admin.html'; });");
                pw.println("</script>");
                pw.println("</body></html>");
            }


        } catch (SQLException se) {
            pw.println("SQL Exception occurred: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            pw.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
