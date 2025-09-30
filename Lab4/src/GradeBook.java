import java.util.*;

public class GradeBook {
    private String surname;
    private String name;
    private String middle_name;
    private byte course;
    private byte group;
    private double averageAll;
    private final List<Session> sessions = new ArrayList<>(9);

    public GradeBook (String surname,
                      String name,
                      String middle_name,
                      byte course,
                      byte group) {
        this.surname = surname;
        this.name = name;
        this.middle_name = middle_name;
        this.course = course;
        this.group = group;

        for (byte num = 1; num <= 9; num++) {
            sessions.add(new Session(num));
        }
    }

    public void calculateAllAverages() {
        double average = 0;
        int j = 0;

        for (byte i = 1; i <= 9; i++) {
            if (!this.getSession(i).isDisciplinesEmpty()) {
                double session_average = 0;
                int k = 0;

                for (GradeBook.Session.Discipline disc : this.getSession(i).getDisciplines()) {
                    if (disc.isExam()) {
                        average += disc.getMark();
                        session_average += disc.getMark();
                        j++;
                        k++;
                    }
                }

                this.getSession(i).setAverage(session_average / k);
            }
        }

        this.setAverageAll(average / j);
    }

    public void calculateAverageMarkAllSessions() {
        double average = 0;
        int j = 0;

        for (byte i = 1; i <= 9; i++) {
            if (!this.getSession(i).isDisciplinesEmpty()) {
                for (GradeBook.Session.Discipline disc : this.getSession(i).getDisciplines()) {
                    if (disc.isExam()) {
                        average += disc.getMark();
                        j++;
                    }
                }
            }
        }

        this.setAverageAll(average / j);
    }

    public void setAverageAll(double a) {
        if ((a <= 0) || (a >= 10)) {
            throw new IllegalArgumentException("Average mark should be >= 0 and <= 10");
        }

        this.averageAll = a;
    }

    public double getAverageAll() {
        return averageAll;
    }

    public boolean isExcellent() {
        for (byte i = 1; i <= 9; i++) {
            if (!this.getSession(i).isDisciplinesEmpty()) {
                for (GradeBook.Session.Discipline disc : this.getSession(i).getDisciplines()) {
                    if (!((disc.isExam() && disc.getMark() >= 9) || (!disc.isExam() && disc.getMark() == 1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void CalculateAverageMarkInSession(byte n) {
        if ((n < 1) || (n > 9)) {
            throw new IllegalArgumentException("Number of session should be >= 1 and <= 9");
        }
        else {
            double average = 0;
            int i = 0;

            if (!this.getSession(n).isDisciplinesEmpty()) {
                for (GradeBook.Session.Discipline disc : this.getSession(n).getDisciplines()) {
                    if (disc.isExam()) {
                        average += disc.getMark();
                        i++;
                    }
                }
            }

            this.getSession(n).setAverage(average / i);
        }
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public Session getSession(byte session_number) {
        if (session_number < 1 || session_number > 9) {
            throw new IllegalArgumentException("Number of session should be >=1 and <=9");
        }

        return sessions.get(session_number - 1);
    }

    public Session addSession(byte session_number){
        Session s = new Session(session_number);
        sessions.add(s);
        return s;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMiddleName() {
        return middle_name;
    }

    public byte getCourse() {
        return course;
    }

    public byte getGroup() {
        return group;
    }

    public String getFullName(){
        return (surname + " " + name + " " + middle_name);
    }

    public class Session {
       private byte session_number;
       private double average;
       private List<Discipline> disciplines = new ArrayList<>();

       private Session(byte session_number) {
           this.session_number = session_number;
       }

       public byte getSessionNumber() {
           return session_number;
       }

       public void setAverage(double a) {
           if ((a < 0) || (a > 10)) {
               throw new IllegalArgumentException("Average mark should be >= 0 and <= 10");
           }

           this.average = a;
       }

       public double getAverage() {
           return average;
       }

       public List<Discipline> getDisciplines() {
           return disciplines;
       }

       public Discipline addDiscipline(String discipline_name, boolean is_exam, byte mark) {
           Discipline d = new Discipline(discipline_name, is_exam, mark);
           disciplines.add(d);
           return d;
       }

       public boolean isDisciplinesEmpty() {
           return disciplines.isEmpty();
       }

       public class Discipline {
           private String discipline_name;
           private boolean is_exam; // 1 ~ exam, 0 ~ credit
           private byte mark;

           private Discipline(String discipline_name, boolean is_exam, byte mark) {
               this.discipline_name = discipline_name;
               this.is_exam = is_exam;
               this.mark = mark;
           }

           public String getNameOfDiscipline() {
               return discipline_name;
           }

           public boolean isExam() {
               return is_exam;
           }

           public byte getMark() {
               if (is_exam && (mark >= 0) && (mark <= 10)) {
                   return mark;
               }
               else if (!is_exam && ((mark == 0) || (mark == 1))) {
                   return mark;
               }
               else {
                   if (is_exam) {
                       throw new IllegalArgumentException("Mark of exam should be >= 0 and <= 10");
                   }
                   else {
                       throw new IllegalArgumentException("Mark of credit should be 0 or 1");
                   }
               }
           }
       }
    }
}
