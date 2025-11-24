package com.github.marlon2132.studentsorttask;

import com.github.marlon2132.studentsorttask.Student;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class StudentTableApp extends Application {

    private final ObservableList<Student> masterData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, Long> numberCol = new TableColumn<>("Номер");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(cell -> Bindings.createStringBinding(cell.getValue()::getNameString));

        TableColumn<Student, Integer> groupCol = new TableColumn<>("Группа");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("group"));

        TableColumn<Student, Double> gradeCol = new TableColumn<>("Средний балл");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        table.getColumns().addAll(numberCol, nameCol, groupCol, gradeCol);

        // Поисковые поля
        TextField searchNumber = new TextField();
        searchNumber.setPromptText("По номеру");

        TextField searchName = new TextField();
        searchName.setPromptText("По имени");

        TextField searchGroup = new TextField();
        searchGroup.setPromptText("По группе");

        TextField searchGrade = new TextField();
        searchGrade.setPromptText("По баллу");

        // Кнопка загрузки файла
        Button loadBtn = new Button("Загрузить файл");
        loadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Выберите txt файл");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));
            Path path = fc.showOpenDialog(primaryStage) == null ? null : fc.showOpenDialog(primaryStage).toPath();
            if (path != null) {
                try {
                    masterData.clear();
                    masterData.addAll(loadFromFile(path));
                } catch (IOException ex) {
                    showAlert("Ошибка чтения файла", ex.getMessage());
                }
            }
        });

        // ComboBox для быстрой сортировки
        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("По номеру", "По имени", "По группе", "По баллу");
        sortBox.setValue("По номеру");
        Button applySort = new Button("Применить сортировку");
        applySort.setOnAction(e -> {
            String sel = sortBox.getValue();
            if ("По номеру".equals(sel)) {
                table.getSortOrder().setAll(numberCol);
            } else if ("По имени".equals(sel)) {
                table.getSortOrder().setAll(nameCol);
            } else if ("По группе".equals(sel)) {
                table.getSortOrder().setAll(groupCol);
            } else if ("По баллу".equals(sel)) {
                table.getSortOrder().setAll(gradeCol);
            }
        });

        // Фильтрация: FilteredList
        FilteredList<Student> filtered = new FilteredList<>(masterData, p -> true);

        // Обновляем предикат при вводе в поля
        Predicate<Student> combinedPredicate = s -> true; // placeholder

        searchNumber.textProperty().addListener((obs, oldV, newV) -> updateFilter(filtered, searchNumber, searchName, searchGroup, searchGrade));
        searchName.textProperty().addListener((obs, oldV, newV) -> updateFilter(filtered, searchNumber, searchName, searchGroup, searchGrade));
        searchGroup.textProperty().addListener((obs, oldV, newV) -> updateFilter(filtered, searchNumber, searchName, searchGroup, searchGrade));
        searchGrade.textProperty().addListener((obs, oldV, newV) -> updateFilter(filtered, searchNumber, searchName, searchGroup, searchGrade));

        // SortedList связывается с TableView
        SortedList<Student> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        // Layout
        GridPane searchPane = new GridPane();
        searchPane.setHgap(8);
        searchPane.setVgap(8);
        searchPane.add(new Label("Фильтры:"), 0, 0);
        searchPane.add(searchNumber, 0, 1);
        searchPane.add(searchName, 1, 1);
        searchPane.add(searchGroup, 2, 1);
        searchPane.add(searchGrade, 3, 1);

        HBox controls = new HBox(8, loadBtn, sortBox, applySort);
        controls.setPadding(new Insets(8));

        VBox root = new VBox(8, searchPane, controls, table);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Студенты");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Пример: можно загрузить заранее test.txt если он рядом
        // try { masterData.addAll(loadFromFile(Path.of("students.txt"))); } catch (Exception ignored) {}
    }

    private void updateFilter(FilteredList<Student> filtered,
                              TextField numberF, TextField nameF, TextField groupF, TextField gradeF) {
        String numQ = numberF.getText().trim().toLowerCase();
        String nameQ = nameF.getText().trim().toLowerCase();
        String groupQ = groupF.getText().trim().toLowerCase();
        String gradeQ = gradeF.getText().trim().toLowerCase();

        filtered.setPredicate(student -> {
            // По номеру: ищем подстроку в строковом представлении номера
            if (!numQ.isEmpty()) {
                if (!String.valueOf(student.getNumber()).toLowerCase().contains(numQ)) return false;
            }
            // По имени: ищем подстроку в имени
            if (!nameQ.isEmpty()) {
                if (!student.getNameString().toLowerCase().contains(nameQ)) return false;
            }
            // По группе: точное или частичное совпадение по строке
            if (!groupQ.isEmpty()) {
                if (!String.valueOf(student.getGroup()).toLowerCase().contains(groupQ)) return false;
            }
            // По баллу: ищем совпадение по строковому представлению (например "9.3" или "9")
            if (!gradeQ.isEmpty()) {
                if (!String.valueOf(student.getGrade()).toLowerCase().contains(gradeQ)) return false;
            }
            return true;
        });
    }

    private ObservableList<Student> loadFromFile(Path path) throws IOException {
        ObservableList<Student> list = FXCollections.observableArrayList();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) continue;
                // Разделяем по пробелам (несколько пробелов считаются разделителем)
                String[] parts = line.split("\\s+");
                if (parts.length < 4) {
                    // можно игнорировать или бросить исключение
                    System.err.println("Неправильный формат в строке " + lineNo + ": " + line);
                    continue;
                }
                try {
                    long number = Long.parseLong(parts[0]);
                    String name = parts[1];
                    int group = Integer.parseInt(parts[2]);
                    double grade = Double.parseDouble(parts[3]);
                    // Преобразуем имя в char[]
                    char[] nameChars = name.toCharArray();
                    list.add(new Student(number, nameChars, group, grade));
                } catch (NumberFormatException ex) {
                    System.err.println("Ошибка парсинга в строке " + lineNo + ": " + ex.getMessage());
                }
            }
        }
        return list;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
