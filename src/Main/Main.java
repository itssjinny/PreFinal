package Main;

import config.dbConnect;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static void consumeNewline(Scanner sc) {
        if (sc.hasNextLine()) {
            sc.nextLine();
        }
    }
    public static void viewUsers() {
        System.out.println("\n--- Viewing All Users ---");
        String Query = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_user";
        String[] votersHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] votersColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, votersHeaders, votersColumns);
        System.out.println("--------------------------");
    }

    public static void viewSubjects() {
        System.out.println("\n--- Viewing All Subjects ---");
        String Query = "SELECT subject_id, subject_code, subject_desc, units, year_level, semester, status FROM subject";
        String[] subjHeaders = {"Subject ID", "Code", "Description", "Units", "Year", "Sem", "Status"};
        String[] subjColumns = {"subject_id", "subject_code", "subject_desc", "units", "year_level", "semester", "status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, subjHeaders, subjColumns);
        System.out.println("----------------------------");
    }

    public static void viewStudents() {
        System.out.println("\n--- Viewing All Students ---");
        String Query = "SELECT s_id, s_no, s_fname, s_lname, s_email, s_yl, s_status FROM tbl_student";
        String[] headers = {"ID", "Student No", "First Name", "Last Name", "Email", "Year Level", "Status"};
        String[] cols = {"s_id", "s_no", "s_fname", "s_lname", "s_email", "s_yl", "s_status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, cols);
        System.out.println("----------------------------");
    }

    public static void viewTeachers() {
        System.out.println("\n--- Viewing All Teachers ---");
        String Query = "SELECT t_id, t_fname, t_lname, t_email, t_status FROM tbl_teacher";
        String[] headers = {"ID", "First Name", "Last Name", "Email", "Status"};
        String[] cols = {"t_id", "t_fname", "t_lname", "t_email", "t_status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, cols);
        System.out.println("----------------------------");
    }

    
    public static void viewGrades() {
        System.out.println("\n--- Viewing All Grades ---");
        String Query = "SELECT g.grade_id, s.s_no,(s.s_fname||' '||s.s_lname) AS s_name, "
                + "sub.subject_code, sub.subject_desc,(t.t_fname||' '||t.t_lname) AS t_name, "
                + "g.prelim, g.midterm, g.prefinal, g.final, g.remarks "
                + "FROM grades g "
                + "JOIN tbl_student s ON g.s_id = s.s_id "
                + "JOIN subject sub ON g.subject_id = sub.subject_id "
                + "JOIN tbl_teacher t ON g.t_id = t.t_id";
        String[] headers = {"GID", "Student No", "Student", "Subject Code", "Subject", "Teacher", "Prelim", "Midterm", "Prefinal", "Final", "Remarks"};
        String[] cols = {"grade_id", "s_no", "s_name", "subject_code", "subject_desc", "t_name", "prelim", "midterm", "prefinal", "final", "remarks"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, cols);
        System.out.println("----------------------------");
    }

    public static void viewSchoolYears() {
        System.out.println("\n--- Viewing All School Years ---");
        String Query = "SELECT sy_id, school_year, status FROM schoolyear";
        String[] headers = {"SY ID", "School Year", "Status"};
        String[] cols = {"sy_id", "school_year", "status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, cols);
        System.out.println("--------------------------------");
    }
    

    public static void viewSemesters() {
        System.out.println("\n--- Viewing All Semesters ---");
        String Query = "SELECT sem_id, sem_name, status FROM semester";
        String[] headers = {"Sem ID", "Semester Name", "Status"};
        String[] cols = {"sem_id", "sem_name", "status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, cols);
        System.out.println("-----------------------------");
    }
    
 
    public static void addStudent(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Student No: ");
        String sNo = sc.next();
        System.out.print("First name: ");
        String fname = sc.next();
        System.out.print("Last name: ");
        String lname = sc.next();
        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Password: ");
        String pass = sc.next();
        System.out.print("Year Level: ");
        String year = sc.next();
        
        String sql = "INSERT INTO tbl_student(s_no, s_fname, s_lname, s_email, s_pass, s_yl, s_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        con.addRecord(sql, sNo, fname, lname, email, pass, year, "Active");
        System.out.println("✅ Student added successfully.");
    }

    public static void addTeacher(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add New Teacher ---");
        System.out.print("First name: ");
        String fname = sc.next();
        System.out.print("Last name: ");
        String lname = sc.next();
        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Password: ");
        String pass = sc.next();
        
        String sql = "INSERT INTO tbl_teacher(t_fname, t_lname, t_email, t_pass, t_status) VALUES (?, ?, ?, ?, ?)";
        con.addRecord(sql, fname, lname, email, pass, "Active");
        System.out.println("✅ Teacher added successfully.");
    }

    public static void addSubject(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add New Subject ---");
        System.out.print("Subject code: ");
        String code = sc.next();
        consumeNewline(sc); 
        System.out.print("Subject description: ");
        String desc = sc.nextLine(); 
        System.out.print("Units: ");
        int units = sc.nextInt();
        System.out.print("Year level: ");
        String year = sc.next();
        System.out.print("Semester: ");
        String sem = sc.next();
        
        String sql = "INSERT INTO subject(subject_code, subject_desc, units, year_level, semester, status) VALUES (?, ?, ?, ?, ?, ?)";
        con.addRecord(sql, code, desc, units, year, sem, "Active");
        System.out.println("✅ Subject added successfully.");
    }

    public static void addGrade(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add New Grade Record ---");
        System.out.print("Student ID: ");
        int sid = sc.nextInt();
        System.out.print("Subject ID: ");
        int subid = sc.nextInt();
        System.out.print("Teacher ID: ");
        int tid = sc.nextInt();
        System.out.print("Semester ID:");
        int semid = sc.nextInt();
        System.out.print("Prelim: ");
        double prelim = sc.nextDouble();
        System.out.print("Midterm: ");
        double mid = sc.nextDouble();
        System.out.print("Prefinal: ");
        double pre = sc.nextDouble();
        System.out.print("Final: ");
        double fin = sc.nextDouble();

        String remarks = (fin < 3.0) ? "Pass" : "Fail";

        String sql = "INSERT INTO grades(s_id, subject_id, t_id, sem_id, prelim, midterm, prefinal, final, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        con.addRecord(sql, sid, subid, tid, semid, prelim, mid, pre, fin, remarks);
        System.out.println("✅ Grade record added. Remarks: " + remarks);
    }
    
   
    public static void updateGrade(dbConnect con, Scanner sc) {
        System.out.println("\n--- Update Grade Record ---");
        viewGrades();
        System.out.print("Enter Grade ID to update: ");
        int gid = sc.nextInt();
        System.out.print("New Prelim: ");
        double prelim = sc.nextDouble();
        System.out.print("New Midterm: ");
        double mid = sc.nextDouble();
        System.out.print("New Prefinal: ");
        double pre = sc.nextDouble();
        System.out.print("New Final: ");
        double fin = sc.nextDouble();
        
        String remarks = (fin >= 3.0) ? "Pass" : "Fail";
        
        String sql = "UPDATE grades SET prelim = ?, midterm = ?, prefinal = ?, final = ?, remarks = ? WHERE grade_id = ?";
        con.updateRecord(sql, prelim, mid, pre, fin, remarks, gid);
        System.out.println("✅ Grade updated. New Remarks: " + remarks);
    }
    
    public static void deleteStudent(dbConnect con, Scanner sc) {
        System.out.println("\n--- Delete Student Record ---");
        viewStudents();
        System.out.print("Enter Student ID to delete: ");
        int id = sc.nextInt();
      
        String sql = "DELETE FROM tbl_student WHERE s_id = ?";
        con.updateRecord(sql, id);
        System.out.println("✅ Student deleted (if existed).");
    }
    
 
    public static void updateStudent(dbConnect con, Scanner sc) {
        System.out.println("\n--- Update Student Record ---");
        viewStudents();
        System.out.print("Enter Student ID to update: ");
        int id = sc.nextInt();
        System.out.print("New Student No: ");
        String sno = sc.next();
        System.out.print("New First name: ");
        String fn = sc.next();
        System.out.print("New Last name: ");
        String ln = sc.next();
        System.out.print("New Email: ");
        String em = sc.next();
        System.out.print("New Year Level: ");
        String yl = sc.next();
        
        String sql = "UPDATE tbl_student SET s_no = ?, s_fname = ?, s_lname = ?, s_email = ?, s_yl = ? WHERE s_id = ?";
        con.updateRecord(sql, sno, fn, ln, em, yl, id);
        System.out.println("Student updated."); 
    }

   
    public static void addSchoolYear(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add School Year ---");
        consumeNewline(sc);
        System.out.print("School Year (e.g. 2024-2025): ");
        String sy = sc.nextLine();
        
        String sql = "INSERT INTO schoolyear(school_year, status) VALUES (?, ?)";
        con.addRecord(sql, sy, "Active");
        System.out.println("✅ School year added.");
    }

    public static void addSemester(dbConnect con, Scanner sc) {
        System.out.println("\n--- Add Semester ---");
        consumeNewline(sc);
        System.out.print("Semester Name (e.g. 1st Sem): ");
        String sem = sc.nextLine();
        
        String sql = "INSERT INTO semester(sem_name, status) VALUES (?, ?)";
        con.addRecord(sql, sem, "Active");
        System.out.println("✅ Semester added.");
    }
 
    private static void pause(Scanner sc) {
        System.out.println("\nPress Enter to continue...");
        try {
         
            sc.nextLine(); 
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        dbConnect con = new dbConnect();
        con.connectDB();
        int choice;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("\n\n===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); 
                continue;
            }
            
            choice = sc.nextInt();
            consumeNewline(sc); 

            switch (choice) {
                case 1:
                    System.out.print("Enter email: ");
                    String em = sc.next();
                    System.out.print("Enter Password: ");
                    String pas = sc.next();

                    String qry = "SELECT u_status, u_type FROM tbl_user WHERE u_email = ? AND u_pass = ?";
                    List<Map<String, Object>> result = con.fetchRecords(qry, em, pas);

                    if (result.isEmpty()) {
                        System.out.println("❌ INVALID CREDENTIALS. Please try again.");
                    } else {
                        Map<String, Object> user = result.get(0);
                        String stat = user.get("u_status").toString();
                        String type = user.get("u_type").toString();
                        
                        if (stat.equals("Pending")) {
                            System.out.println("⚠️ Account is Pending. Contact the Admin for approval!");
                        } else {
                            System.out.println("✅ LOGIN SUCCESS! Welcome " + type + ".");
                            
                            if (type.equals("Admin")) {
                                adminDashboard(con, sc);
                            } else if (type.equals("Teacher")) {
                                teacherDashboard(con, sc);
                            }
                        }
                    }
                    break;

                case 2:
                    registerUser(con, sc);
                    break;

                case 3:
                    System.out.println("👋 Thank you! Program ended.");
                    sc.close();
                    con.connectDB(); 
                    System.exit(0);
                    break;

                default:
                    System.out.println("⚠️ Invalid choice. Please select 1, 2, or 3.");
            }

        } while (true); 
    }
    
    private static void registerUser(dbConnect con, Scanner sc) {
        System.out.println("\n===== NEW USER REGISTRATION =====");
        System.out.print("Enter user name: ");
        String name = sc.next();
        System.out.print("Enter user email: ");
        String email = sc.next();

        
        while (true) {
            String qry = "SELECT u_email FROM tbl_user WHERE u_email = ?";
            List<Map<String, Object>> result = con.fetchRecords(qry, email);

            if (result.isEmpty()) {
                break;
            } else {
                System.out.print("Email already exists. Enter a different Email: ");
                email = sc.next();
            }
        }

        System.out.print("Enter user Type (1 - Admin / 2 - Teacher): ");
        int typeNum;
        
        while (true) {
            if (sc.hasNextInt()) {
                typeNum = sc.nextInt();
                if (typeNum == 1 || typeNum == 2) {
                    break;
                }
            } else {
                sc.next();
            }
            System.out.print("⚠️ Invalid, choose between 1 (Admin) & 2 (Teacher) only: ");
        }

        String tp = (typeNum == 1) ? "Admin" : "Teacher";

        System.out.print("Enter Password: ");
        String pass = sc.next();
        
        String sql = "INSERT INTO tbl_user(u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
        con.addRecord(sql, name, email, tp, "Pending", pass);
        System.out.println("✅ Registration submitted - pending admin approval.");
    }

    private static void adminDashboard(dbConnect con, Scanner sc) {
        boolean adminLoop = true;
        while (adminLoop) {
            System.out.println("\n\n===== ADMIN DASHBOARD =====");
            System.out.println("1. View Users");
            System.out.println("2. Manage Students");
            System.out.println("3. Manage Teachers");
            System.out.println("4. Manage Subjects");
            System.out.println("5. Manage School Year & Semester");
            System.out.println("6. View All Grades");
            System.out.println("7. Approve Accounts");
            System.out.println("8. Logout");
            System.out.print("Choice: ");
            
            if (!sc.hasNextInt()) {
                System.out.println("⚠️ Invalid input. Please enter a number.");
                sc.next();
                continue;
            }
            int respo = sc.nextInt();
            consumeNewline(sc);

            switch (respo) {
                case 1:
                    viewUsers();
                    pause(sc);
                    break;
                case 2:
                    System.out.println("\n--- Manage Students ---");
                    System.out.println("a) Add Student");
                    System.out.println("b) Update Student");
                    System.out.println("c) Delete Student");
                    System.out.println("d) View Students");
                    System.out.print("Choose: ");
                    String c = sc.next();
                    if (c.equalsIgnoreCase("a")) addStudent(con, sc);
                    else if (c.equalsIgnoreCase("b")) updateStudent(con, sc);
                    else if (c.equalsIgnoreCase("c")) deleteStudent(con, sc);
                    else viewStudents();
                    pause(sc);
                    break;
                case 3:
                    System.out.println("\n--- Manage Teachers ---");
                    System.out.println("a) Add Teacher");
                    System.out.println("b) View Teachers");
                    System.out.print("Choose: ");
                    String t = sc.next();
                    if (t.equalsIgnoreCase("a")) addTeacher(con, sc);
                    else viewTeachers();
                    pause(sc);
                    break;
                case 4:
                    System.out.println("\n--- Manage Subjects ---");
                    System.out.println("a) Add Subject");
                    System.out.println("b) View Subjects");
                    System.out.print("Choose: ");
                    String s = sc.next();
                    if (s.equalsIgnoreCase("a")) addSubject(con, sc);
                    else viewSubjects();
                    pause(sc);
                    break;
                case 5:
                    System.out.println("\n--- Manage SY/Sem ---");
                    System.out.println("a) Add School Year");
                    System.out.println("b) View School Years");
                    System.out.println("c) Add Semester");
                    System.out.println("d) View Semesters");
                    System.out.print("Choose: ");
                    String syc = sc.next();
                    if (syc.equalsIgnoreCase("a")) addSchoolYear(con, sc);
                    else if (syc.equalsIgnoreCase("b")) viewSchoolYears();
                    else if (syc.equalsIgnoreCase("c")) addSemester(con, sc);
                    else viewSemesters();
                    pause(sc);
                    break;
                case 6:
                    viewGrades();
                    pause(sc);
                    break;
                case 7:
                    System.out.println("\n--- Approve Accounts ---");
                    viewUsers();
                    System.out.print("Enter User ID to Approve: ");
                    int ids = sc.nextInt();
                    String sql = "UPDATE tbl_user SET u_status = ? WHERE u_id = ?";
                    con.updateRecord(sql, "Approved", ids);
                    System.out.println("✅ Account approved.");
                    pause(sc);
                    break;
                case 8:
                    adminLoop = false;
                    System.out.println("Logging out from Admin Dashboard...");
                    break;
                default:
                    System.out.println("⚠️ Invalid choice.");
                    pause(sc);
            }
        }
    }
    
    private static void teacherDashboard(dbConnect con, Scanner sc) {
        boolean teacherLoop = true;
        while (teacherLoop) {
            System.out.println("\n\n===== TEACHER DASHBOARD =====");
            System.out.println("1. View Students");
            System.out.println("2. View Subjects");
            System.out.println("3. Add Grade");
            System.out.println("4. Update Grade");
            System.out.println("5. View Grades");
            System.out.println("6. Logout");
            System.out.print("Choice: ");
            
            if (!sc.hasNextInt()) {
                System.out.println("⚠️ Invalid input. Please enter a number.");
                sc.next();
                continue;
            }
            int tch = sc.nextInt();
            consumeNewline(sc);

            switch (tch) {
                case 1:
                    viewStudents();
                    pause(sc);
                    break;
                case 2:
                    viewSubjects();
                    pause(sc);
                    break;
                case 3:
                    addGrade(con, sc);
                    pause(sc);
                    break;
                case 4:
                    updateGrade(con, sc);
                    pause(sc);
                    break;
                case 5:
                    viewGrades();
                    pause(sc);
                    break;
                case 6:
                    teacherLoop = false;
                    System.out.println("Logging out from Teacher Dashboard...");
                    break;
                default:
                    System.out.println("⚠️ Invalid choice.");
                    pause(sc);
            }
        }
    }
    
}