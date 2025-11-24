import java.util.Arrays;

public class Student {
    private long number_;
    private char[] name_ = new char[10];
    private int group_;
    private double grade_;

    public Student(long number, char[] name, int group, double grade) {
        setNumber(number);
        setName(name);
        setGroup(group);
        setGrade(grade);
    }

    public long getNumber() {
        return number_;
    }

    public void setNumber(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Номер зачётки должен быть >= 0");
        }

        this.number_ = number;
    }

    public char[] getName() {
        return name_;
    }

    public void setName(char[] name) {
        if (name == null) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        int len = Math.min(name.length, this.name_.length);
        System.arraycopy(name, 0, this.name_, 0, len);

        if (len < this.name_.length) {
            Arrays.fill(this.name_, len, this.name_.length, ' ');
        }
    }

    public int getGroup() {
        return group_;
    }

    public void setGroup(int group) {
        if (group <= 0) {
            throw new IllegalArgumentException("Номер группы должен быть больше нуля > 0");
        }

        this.group_ = group;
    }

    public double getGrade() {
        return grade_;
    }

    public void setGrade(double grade) {
        if (grade < 0) {
            throw new IllegalArgumentException("Средний балл должен быть >= 0");
        }

        this.grade_ = grade;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Student)) {
            return false;
        }

        Student other = (Student) o;
        return number_ == other.number_
                && group_ == other.group_
                && Double.compare(other.grade_, grade_) == 0
                && Arrays.equals(name_, other.name_);
    }
}
