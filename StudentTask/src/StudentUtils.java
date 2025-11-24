import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

public class StudentUtils {
    private List<Student> l1_;
    private List<Student> l2_;
    private List<Student> outp_;

    public StudentUtils(List<Student> l1, List<Student> l2, List<Student> outp) {
        setL1(l1);
        setL2(l2);
        setOutp(outp);
    }

    public List<Student> getL1() {
        return Collections.unmodifiableList(l1_);
    }

    public void setL1(List<Student> l1) {
        if (l1 == null) {
            throw new IllegalArgumentException("l1 не может быть пустым");
        }

        this.l1_ = l1;
    }

    public List<Student> getL2() {
        return Collections.unmodifiableList(l2_);
    }

    public void setL2(List<Student> l2) {
        if (l2 == null) {
            throw new IllegalArgumentException("l1 не может быть пустым");
        }

        this.l2_ = l2;
    }

    public List<Student> getOutp() {
        return Collections.unmodifiableList(outp_);
    }

    public void setOutp(List<Student> outp) {
        this.outp_ = outp;
    }

    public void union() {
        outp_.clear();

        for (Student s : l1_) {
            outp_.add(s);
        }

        for (Student s : l2_) {
            if (!containsSame(outp_, s)) {
                outp_.add(s);
            }
        }
    }

    public void intersection() {
        outp_.clear();

        for (Student s : l1_) {
            if (containsSame(l2_, s)) {
                outp_.add(s);
            }
        }
    }

    public void difference() {
        outp_.clear();

        for (Student s : l1_) {
            if (!containsSame(l2_, s) && !containsSame(outp_, s)) {
                outp_.add(s);
            }
        }
    }

    private boolean containsSame(List<Student> list, Student target) {
        for (Student s : list) {
            if (s.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
