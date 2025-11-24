package com.github.marlon2132.studentsorttask;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class StudentController {

    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, Long> numberCol;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, Integer> groupCol;
    @FXML private TableColumn<Student, Double> gradeCol;

    @FXML private TextField searchNumber;
    @FXML private TextField searchName;
    @FXML private TextField searchGroup;
    @FXML private TextField searchGrade;

    @FXML private ChoiceBox<String> sortChoice;
    @FXML private Button applySort;
    @FXML private Button loadFileButton;

    private final ObservableList<Student> masterData = FXCollections.observableArrayList();
    private FilteredList<Student> filtered;

    @FXML
    public void initialize() {
        numberCol.setCellValueFactory(cell -> Bindings.createObjectBinding(cell.getValue()::getNumber));
        nameCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNameString()));
        groupCol.setCellValueFactory(cell -> Bindings.createObjectBinding(cell.getValue()::getGroup));
        gradeCol.setCellValueFactory(cell -> Bindings.createObjectBinding(cell.getValue()::getGrade));

        filtered = new FilteredList<>(masterData, p -> true);
        SortedList<Student> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        sortChoice.getItems().addAll("По номеру", "По имени", "По группе", "По баллу");
        sortChoice.setValue("По номеру");

        searchNumber.textProperty().addListener((obs, o, n) -> updateFilter());
        searchName.textProperty().addListener((obs, o, n) -> updateFilter());
        searchGroup.textProperty().addListener((obs, o, n) -> updateFilter());
        searchGrade.textProperty().addListener((obs, o, n) -> updateFilter());

        // НИКОГДА не вызывать onLoadFile() без аргументов здесь.
        // Привяжем кнопку к FXML-методу через setOnAction с ActionEvent.
        loadFileButton.setOnAction(this::onLoadFile);
        applySort.setOnAction(this::onApplySort);
    }

    // FXML-обработчик — вызывается из FXML или из setOnAction
    @FXML
    private void onLoadFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл студентов");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv", "*.*"));
        Stage stage = (Stage) table.getScene().getWindow();

        // ВАЖНО: вызвать showOpenDialog один раз и сохранить результат
        java.io.File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return; // пользователь отменил выбор
        }

        Path path = file.toPath();
        try {
            ObservableList<Student> loaded = loadFromFile(path);
            masterData.clear();
            masterData.addAll(loaded);
        } catch (IOException ex) {
            showAlert("Ошибка чтения файла", ex.getMessage());
        }
    }


    // Если в FXML используется onAction="#onApplySort" — этот метод найдётся
    @FXML
    private void onApplySort(ActionEvent event) {
        applySortChoice();
    }

    // Общая логика загрузки — без ActionEvent, можно вызывать напрямую
    private void loadFromChooserAndPopulate() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл студентов");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv", "*.*"));
        Stage stage = (Stage) table.getScene().getWindow();
        Path path = chooser.showOpenDialog(stage) == null ? null : chooser.showOpenDialog(stage).toPath();
        if (path == null) return;

        try {
            ObservableList<Student> loaded = loadFromFile(path); // ваш метод парсинга
            masterData.clear();
            masterData.addAll(loaded);
        } catch (IOException ex) {
            showAlert("Ошибка чтения файла", ex.getMessage());
        }
    }

    // Существующий метод сортировки; оставляем приватным и вызываем из onApplySort
    private void applySortChoice() {
        String sel = sortChoice.getValue();
        if ("По номеру".equals(sel)) table.getSortOrder().setAll(numberCol);
        else if ("По имени".equals(sel)) table.getSortOrder().setAll(nameCol);
        else if ("По группе".equals(sel)) table.getSortOrder().setAll(groupCol);
        else if ("По баллу".equals(sel)) table.getSortOrder().setAll(gradeCol);
    }


    private void updateFilter() {
        final String numQ = searchNumber.getText().trim().toLowerCase();
        final String nameQ = searchName.getText().trim().toLowerCase();
        final String groupQ = searchGroup.getText().trim().toLowerCase();
        final String gradeQ = searchGrade.getText().trim().toLowerCase();

        Predicate<Student> predicate = student -> {
            if (!numQ.isEmpty()) {
                if (!String.valueOf(student.getNumber()).toLowerCase().contains(numQ)) return false;
            }
            if (!nameQ.isEmpty()) {
                if (!student.getNameString().toLowerCase().contains(nameQ)) return false;
            }
            if (!groupQ.isEmpty()) {
                if (!String.valueOf(student.getGroup()).toLowerCase().contains(groupQ)) return false;
            }
            if (!gradeQ.isEmpty()) {
                if (!String.valueOf(student.getGrade()).toLowerCase().contains(gradeQ)) return false;
            }
            return true;
        };

        filtered.setPredicate(predicate);
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

                // Первый вариант парсинга: поля через пробелы: number name group grade
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    try {
                        long number = Long.parseLong(parts[0]);
                        String name = parts[1];
                        int group = Integer.parseInt(parts[2]);
                        double grade = Double.parseDouble(parts[3]);
                        list.add(new Student(number, name.toCharArray(), group, grade));
                        continue;
                    } catch (NumberFormatException ignored) { }
                }

                // Альтернативный универсальный парсер: number first, grade last, group penultimate, всё между — имя
                String[] tokens = line.split("\\s+");
                if (tokens.length >= 4) {
                    try {
                        long number = Long.parseLong(tokens[0]);
                        double grade = Double.parseDouble(tokens[tokens.length - 1]);
                        int group = Integer.parseInt(tokens[tokens.length - 2]);
                        StringBuilder nameSb = new StringBuilder();
                        for (int i = 1; i <= tokens.length - 3; i++) {
                            if (i > 1) nameSb.append(' ');
                            nameSb.append(tokens[i]);
                        }
                        list.add(new Student(number, nameSb.toString().toCharArray(), group, grade));
                    } catch (NumberFormatException ex) {
                        System.err.println("Проблема парсинга в строке " + lineNo + ": " + line);
                    }
                } else {
                    System.err.println("Неправильный формат в строке " + lineNo + ": " + line);
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
}
