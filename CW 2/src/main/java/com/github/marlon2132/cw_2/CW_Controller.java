package com.github.marlon2132.cw_2;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CW_Controller {

    @FXML
    private VBox congrBox;
    @FXML
    private ListView<String> giftList;
    @FXML
    private RadioButton concertYes;
    @FXML
    private RadioButton concertNo;
    @FXML
    private CheckBox loyalClient;
    @FXML
    private Label priceLabel;
    @FXML
    private TextArea orderArea;

    private double concertPrice = 0.0;
    private final Map<String, LinkedHashMap<String, Double>> data = new LinkedHashMap<>();
    private final Map<String, CheckBox> congrCheckBoxes = new LinkedHashMap<>();
    private final List<String> orderLines = new ArrayList<>();
    private double currentTotal = 0.0;

    @FXML
    public void initialize() {
        System.out.println("INIT: initialize() called");
        System.out.println("INIT: concertYes = " + (concertYes == null) + ", concertNo = " + (concertNo == null));
        System.out.println("INIT: congrBox = " + (congrBox == null) + ", giftList = " + (giftList == null));

        ToggleGroup concertGroup = new ToggleGroup();
        concertYes.setToggleGroup(concertGroup);
        concertNo.setToggleGroup(concertGroup);

        giftList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        System.out.println("INIT: before onLoadConfig");
        onLoadConfig();
        System.out.println("INIT: after onLoadConfig");
    }

    @FXML
    public void onLoadConfig() {
        loadConfigFromResource("/com/github/marlon2132/cw_2/config.txt");
    }

    private InputStream openConfigStream() {
        String resourcePath = "com/github/marlon2132/cw_2/config.txt";

        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        System.out.println("DEBUG: classpath resource stream is " + (is == null ? "null" : "available"));
        if (is != null){
            return is;
        }

        try {
            Path p = Paths.get("config.txt");
            System.out.println("DEBUG: cwd = " + System.getProperty("user.dir") + ", looking for " + p.toAbsolutePath());

            if (Files.exists(p)){
                return Files.newInputStream(p);
            }
        }
        catch (Exception ignored) {}

        try {
            Path p2 = Paths.get("src/main/resources/com/github/marlon2132/cw_2/config.txt");
            System.out.println("DEBUG: checking " + p2.toAbsolutePath());
            if (Files.exists(p2)){
                return Files.newInputStream(p2);
            }
        }
        catch (Exception ignored) {}

        return null;
    }

    private void loadConfigFromResource(String ignored) {
        System.out.println("LOADCFG: called");
        data.clear();
        congrBox.getChildren().clear();
        giftList.getItems().clear();
        congrCheckBoxes.clear();
        concertPrice = 0.0;

        InputStream is = openConfigStream();
        System.out.println("LOADCFG: openConfigStream returned: " + (is == null ? "null" : "NOT null"));

        if (is == null) {
            showError("Файл конфигурации не найден. Ищем: classpath com/github/marlon2132/cw_2/config.txt, ./config.txt, src/...");

            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            String currentCong = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")){
                    line = line.substring(1);
                }

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")){
                    continue;
                }

                // concert price
                if (line.toLowerCase().startsWith("concert_price=")) {
                    String v = line.substring("concert_price=".length()).trim();

                    try {
                        concertPrice = Double.parseDouble(v);
                    }
                    catch (NumberFormatException nfe) {
                        System.out.println("WARN: bad concert_price: " + v);
                    }

                    continue;
                }

                // section lines: [Congratulator:Name] OR [Company:Name]
                if (line.matches("(?i)^\\[(congratulator|company)\\s*:\\s*.+\\]$")) {
                int colon = line.indexOf(':');
                int end = line.lastIndexOf(']');
                currentCong = line.substring(colon + 1, end).trim();
                if (!currentCong.isEmpty()){
                    data.put(currentCong, new LinkedHashMap<>());
                }

                continue;
            }

            // gift line: Gift:Name=price
            if (line.toLowerCase().startsWith("gift:") && currentCong != null) {
                String rest = line.substring(5).trim(); // after "Gift:"
                String[] parts = rest.split("=", 2);

                if (parts.length == 2) {
                    String giftName = parts[0].trim();
                    String priceStr = parts[1].trim();

                    try {
                        double price = Double.parseDouble(priceStr);
                        data.get(currentCong).put(giftName, price);
                    }
                    catch (NumberFormatException nfe) {
                        System.out.println("WARN: cannot parse price '" + priceStr + "' for gift '" + giftName + "'");
                    }
                }
                else {
                    System.out.println("WARN: bad gift line: " + line);
                }
            }
        }
    }
        catch (Exception ex) {
        showError("Ошибка чтения конфигурации: " + ex.getMessage());

        return;
    }

    // Diagnostics: what parsed
    System.out.println("LOADCFG: parsed companies = " + data.size() + ", concertPrice = " + concertPrice);
    for (Map.Entry<String, LinkedHashMap<String, Double>> e : data.entrySet()) {
        System.out.println("  company: " + e.getKey() + ", gifts: " + e.getValue().keySet());
    }

    // create checkboxes for companies
    for (String name : data.keySet()) {
        CheckBox cb = new CheckBox(name);
        cb.selectedProperty().addListener((obs, oldV, newV) -> updateGiftListForSelectedCongratulators());
        congrBox.getChildren().add(cb);
        congrCheckBoxes.put(name, cb);
    }

    System.out.println("LOADCFG: congrBox child count = " + congrBox.getChildren().size());

    updatePriceLabel();
}

private void updateGiftListForSelectedCongratulators() {
        giftList.getItems().clear();
        Set<String> selected = getSelectedCongratulators();

        if (selected.isEmpty()){
            return;
        }

        for (String company : selected) {
            LinkedHashMap<String, Double> gifts = data.get(company);

            if (gifts == null){
                continue;
            }

            for (Map.Entry<String, Double> e : gifts.entrySet()) {
                String item = String.format("%s: %s — %.2f", company, e.getKey(), e.getValue());
                giftList.getItems().add(item);
            }
        }

        giftList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    private Set<String> getSelectedCongratulators() {
        Set<String> res = new LinkedHashSet<>();

        for (Map.Entry<String, CheckBox> e : congrCheckBoxes.entrySet()) {
            if (e.getValue().isSelected()){
                res.add(e.getKey());
            }
        }

        return res;
    }

    @FXML
    public void onAddToOrder() {
        Set<String> selectedCong = getSelectedCongratulators();

        if (selectedCong.isEmpty()) {
            showError("Не выбран ни один поздравитель");

            return;
        }

        ObservableList<String> selectedItems = giftList.getSelectionModel().getSelectedItems();

        if (selectedItems == null || selectedItems.isEmpty()) {
            showError("Не выбран ни один подарок");

            return;
        }

        boolean loyal = loyalClient.isSelected();
        boolean concert = concertYes.isSelected();

        for (String giftItem : selectedItems) {
            String priceStr = null;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("([0-9]+(?:[.,][0-9]+)?)\\s*$").matcher(giftItem);

            if (m.find()) {
                priceStr = m.group(1).replace(',', '.');
            }

            if (priceStr == null) {
                int idx = Math.max(giftItem.lastIndexOf('—'), Math.max(giftItem.lastIndexOf('–'), giftItem.lastIndexOf('-')));

                if (idx >= 0 && idx + 1 < giftItem.length()) {
                    priceStr = giftItem.substring(idx + 1).trim().replace(',', '.');
                }
            }

            if (priceStr == null) {
                System.out.println("WARN: cannot find price in item: '" + giftItem + "'");

                continue;
            }

            double giftPrice;

            try {
                giftPrice = Double.parseDouble(priceStr);
            }
            catch (NumberFormatException ex) {
                System.out.println("WARN: bad price '" + priceStr + "' in item: '" + giftItem + "'");

                continue;
            }

            String left = giftItem;

            int lastDash = Math.max(giftItem.lastIndexOf('—'), Math.max(giftItem.lastIndexOf('–'), giftItem.lastIndexOf('-')));

            if (lastDash > 0){
                left = giftItem.substring(0, lastDash).trim();
            }

            String company = "";
            String giftName = left;
            int colonIdx = left.indexOf(':');

            if (colonIdx >= 0) {
                company = left.substring(0, colonIdx).trim();
                giftName = left.substring(colonIdx + 1).trim();
            }

            double sum = giftPrice;
            StringBuilder line = new StringBuilder();
            line.append("Компания: ").append(company)
                    .append("; Подарок: ").append(giftName)
                    .append(String.format("; Цена подарка: %.2f", giftPrice));

            if (concert) {
                sum += concertPrice;
                line.append(String.format("; + концерт %.2f", concertPrice));
            }
            else {
                line.append("; концерт: нет");
            }

            if (loyal) {
                double discount = sum * 0.10;
                sum -= discount;
                line.append(String.format("; скидка 10%% (-%.2f)", discount));
            }

            currentTotal += sum;
            orderLines.add(line.toString());
        }

        refreshOrderArea();
        updatePriceLabel();
    }



    @FXML
    public void onClearOrder() {
        orderLines.clear();
        currentTotal = 0.0;
        refreshOrderArea();
        updatePriceLabel();
    }

    private void refreshOrderArea() {
        StringBuilder sb = new StringBuilder();

        for (String l : orderLines) {
            sb.append(l).append("\n");
        }
        
        orderArea.setText(sb.toString());
    }

    private void updatePriceLabel() {
        priceLabel.setText(String.format("%.2f", currentTotal));
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            a.setHeaderText("Ошибка");
            a.showAndWait();
        });
    }
}
