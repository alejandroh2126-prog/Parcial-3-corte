package gamezone.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import gamezone.entities.*;
import gamezone.services.VideoGameService;

import java.util.List;

public class MainApp extends Application {

    private final VideoGameService service = new VideoGameService();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("🎮 GameZone — Sistema de Gestión");
        stage.setScene(buildMainMenu());
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildMainMenu() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("🎮 SISTEMA DE GESTIÓN — GAMEZONE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#e94560"));

        Label sub = new Label("════════════════════════════════════");
        sub.setTextFill(Color.web("#16213e"));

        Button btnAdd    = menuButton("1.  Agregar videojuego");
        Button btnList   = menuButton("2.  Listar todos los videojuegos");
        Button btnSearch = menuButton("3.  Buscar por título");
        Button btnPlat   = menuButton("4.  Buscar por plataforma");
        Button btnSell   = menuButton("5.  Realizar venta");
        Button btnSales  = menuButton("6.  Mostrar ventas");
        Button btnExit   = menuButton("7.  Salir");
        btnExit.setStyle(btnExit.getStyle() + "-fx-background-color: #c0392b;");

        btnAdd.setOnAction    (e -> primaryStage.setScene(buildAddScene()));
        btnList.setOnAction   (e -> primaryStage.setScene(buildListScene(service.getAllGames(), "Todos los videojuegos")));
        btnSearch.setOnAction (e -> primaryStage.setScene(buildSearchByTitleScene()));
        btnPlat.setOnAction   (e -> primaryStage.setScene(buildSearchByPlatformScene()));
        btnSell.setOnAction   (e -> primaryStage.setScene(buildSellScene()));
        btnSales.setOnAction  (e -> primaryStage.setScene(buildSalesScene()));
        btnExit.setOnAction   (e -> primaryStage.close());

        root.getChildren().addAll(title, sub,
                btnAdd, btnList, btnSearch, btnPlat, btnSell, btnSales, btnExit);
        return new Scene(root, 700, 560);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  AGREGAR VIDEOJUEGO
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildAddScene() {
        VBox root = darkBox();

        Label header = sectionLabel("➕ Agregar Videojuego");

        // Tipo
        Label lblTipo = fieldLabel("Tipo de videojuego:");
        ToggleGroup tg = new ToggleGroup();
        RadioButton rbDigital  = new RadioButton("Digital");
        RadioButton rbPhysical = new RadioButton("Físico");
        rbDigital.setToggleGroup(tg); rbDigital.setSelected(true);
        rbPhysical.setToggleGroup(tg);
        styleRadio(rbDigital); styleRadio(rbPhysical);
        HBox tipoBox = new HBox(20, rbDigital, rbPhysical);

        // Campos comunes
        TextField tfTitle    = styledField("Título");
        TextField tfPrice    = styledField("Precio");
        TextField tfPlatform = styledField("Plataforma (PC, PS5, Xbox...)");
        TextField tfStock    = styledField("Stock");
        TextField tfGenre    = styledField("Género");

        // Campos digital
        TextField tfSizeGB     = styledField("Tamaño en GB");
        TextField tfDownloadPl = styledField("Plataforma de descarga (Steam, Epic...)");
        VBox digitalFields = new VBox(8, fieldLabel("Tamaño (GB):"), tfSizeGB,
                fieldLabel("Plataforma de descarga:"), tfDownloadPl);

        // Campos físico
        TextField tfCondition   = styledField("Condición (nuevo/usado)");
        TextField tfDistributor = styledField("Distribuidor");
        VBox physicalFields = new VBox(8, fieldLabel("Condición:"), tfCondition,
                fieldLabel("Distribuidor:"), tfDistributor);
        physicalFields.setVisible(false);
        physicalFields.setManaged(false);

        rbPhysical.setOnAction(e -> { digitalFields.setVisible(false);  digitalFields.setManaged(false);
            physicalFields.setVisible(true);   physicalFields.setManaged(true); });
        rbDigital.setOnAction (e -> { physicalFields.setVisible(false); physicalFields.setManaged(false);
            digitalFields.setVisible(true);    digitalFields.setManaged(true); });

        Button btnSave = actionButton("💾 Guardar");
        Button btnBack = backButton();

        btnSave.setOnAction(e -> {
            try {
                String title    = tfTitle.getText().trim();
                double price    = Double.parseDouble(tfPrice.getText().trim());
                String platform = tfPlatform.getText().trim();
                int    stock    = Integer.parseInt(tfStock.getText().trim());
                String genre    = tfGenre.getText().trim();

                VideoGame game;
                if (rbDigital.isSelected()) {
                    double sizeGB    = Double.parseDouble(tfSizeGB.getText().trim());
                    String downPl    = tfDownloadPl.getText().trim();
                    game = new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, downPl);
                } else {
                    String cond = tfCondition.getText().trim();
                    String dist = tfDistributor.getText().trim();
                    game = new PhysicalVideoGame(title, price, platform, stock, genre, cond, dist);
                }

                boolean added = service.addVideoGame(game);
                if (added) {
                    showInfo("Videojuego agregado",
                            "'" + game.getTitle() + "' fue agregado al catálogo exitosamente.");
                    primaryStage.setScene(buildMainMenu());
                }
            } catch (NumberFormatException ex) {
                showError("Error de formato", "Verifica que precio, stock y tamaño sean números válidos.");
            } catch (IllegalArgumentException ex) {
                showError("Validación", ex.getMessage());
            }
        });

        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        root.getChildren().addAll(header, lblTipo, tipoBox,
                fieldLabel("Título:"),    tfTitle,
                fieldLabel("Precio:"),    tfPrice,
                fieldLabel("Plataforma:"),tfPlatform,
                fieldLabel("Stock:"),     tfStock,
                fieldLabel("Género:"),    tfGenre,
                digitalFields, physicalFields,
                new HBox(10, btnSave, btnBack));

        ScrollPane sp = new ScrollPane(root);
        sp.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        sp.setFitToWidth(true);
        return new Scene(sp, 700, 560);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LISTAR / TABLA
    // ══════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private Scene buildListScene(List<VideoGame> games, String headerText) {
        VBox root = darkBox();
        Label header = sectionLabel("📋 " + headerText);

        TableView<VideoGame> table = new TableView<>();
        table.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");
        table.setPrefHeight(380);

        TableColumn<VideoGame, String> colTitle    = new TableColumn<>("Título");
        TableColumn<VideoGame, String> colType     = new TableColumn<>("Tipo");
        TableColumn<VideoGame, String> colPlat     = new TableColumn<>("Plataforma");
        TableColumn<VideoGame, String> colGenre    = new TableColumn<>("Género");
        TableColumn<VideoGame, String> colPrice    = new TableColumn<>("Precio Final");
        TableColumn<VideoGame, String> colStock    = new TableColumn<>("Stock");
        TableColumn<VideoGame, String> colExtra    = new TableColumn<>("Extra");

        colTitle.setCellValueFactory (d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        colType.setCellValueFactory  (d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue() instanceof DigitalVideoGame ? "Digital" : "Físico"));
        colPlat.setCellValueFactory  (d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPlatform()));
        colGenre.setCellValueFactory (d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGenre()));
        colPrice.setCellValueFactory (d -> new javafx.beans.property.SimpleStringProperty(
                "$" + d.getValue().calculateFinalPrice()));
        colStock.setCellValueFactory (d -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(d.getValue().getStock())));
        colExtra.setCellValueFactory (d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue() instanceof DigitalVideoGame dg
                        ? dg.getSizeGB() + " GB"
                        : ((PhysicalVideoGame) d.getValue()).getCondition()));

        table.getColumns().addAll(colTitle, colType, colPlat, colGenre, colPrice, colStock, colExtra);
        table.getItems().addAll(games);

        // Botones CRUD debajo de la tabla
        Button btnDelete = actionButton("🗑 Eliminar seleccionado");
        Button btnEdit   = actionButton("✏ Editar seleccionado");
        Button btnBack   = backButton();

        btnDelete.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showError("Selección", "Selecciona un videojuego de la tabla."); return; }
            boolean ok = service.deleteVideoGame(selected.getTitle());
            if (ok) {
                table.getItems().remove(selected);
                showInfo("Eliminado", "'" + selected.getTitle() + "' fue eliminado del catálogo.");
            }
        });

        btnEdit.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showError("Selección", "Selecciona un videojuego de la tabla."); return; }
            primaryStage.setScene(buildEditScene(selected));
        });

        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        if (games.isEmpty()) {
            root.getChildren().addAll(header, new Label("No hay videojuegos registrados."), btnBack);
        } else {
            root.getChildren().addAll(header, table, new HBox(10, btnDelete, btnEdit, btnBack));
        }

        ScrollPane sp = new ScrollPane(root);
        sp.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        sp.setFitToWidth(true);
        return new Scene(sp, 800, 560);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EDITAR VIDEOJUEGO
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildEditScene(VideoGame game) {
        VBox root = darkBox();
        Label header = sectionLabel("✏ Editar: " + game.getTitle());

        TextField tfTitle    = styledField(game.getTitle());
        TextField tfPrice    = styledField(String.valueOf(game.getPrice()));
        TextField tfPlatform = styledField(game.getPlatform());
        TextField tfStock    = styledField(String.valueOf(game.getStock()));
        TextField tfGenre    = styledField(game.getGenre());

        Button btnSave = actionButton("💾 Guardar cambios");
        Button btnBack = backButton();

        btnSave.setOnAction(e -> {
            try {
                String oldTitle = game.getTitle();
                game.setTitle(tfTitle.getText().trim());
                game.setPrice(Double.parseDouble(tfPrice.getText().trim()));
                game.setPlatform(tfPlatform.getText().trim());
                game.setStock(Integer.parseInt(tfStock.getText().trim()));
                game.setGenre(tfGenre.getText().trim());

                service.updateVideoGame(oldTitle, game);
                showInfo("Actualizado", "Videojuego actualizado correctamente.");
                primaryStage.setScene(buildMainMenu());
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });
        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        root.getChildren().addAll(header,
                fieldLabel("Título:"),    tfTitle,
                fieldLabel("Precio:"),    tfPrice,
                fieldLabel("Plataforma:"),tfPlatform,
                fieldLabel("Stock:"),     tfStock,
                fieldLabel("Género:"),    tfGenre,
                new HBox(10, btnSave, btnBack));

        return new Scene(root, 700, 460);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUSCAR POR TÍTULO
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildSearchByTitleScene() {
        VBox root = darkBox();
        Label header = sectionLabel("🔍 Buscar por Título");

        TextField tfSearch = styledField("Escribe el título...");
        TextArea  taResult = new TextArea();
        taResult.setEditable(false);
        taResult.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #e0e0e0;");
        taResult.setPrefHeight(200);

        Button btnSearch = actionButton("🔎 Buscar");
        Button btnBack   = backButton();

        btnSearch.setOnAction(e -> {
            VideoGame found = service.findByTitle(tfSearch.getText().trim());
            taResult.setText(found != null
                    ? found.toString()
                    : "❌ No se encontró ningún videojuego con ese título.");
        });
        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        root.getChildren().addAll(header, fieldLabel("Título:"), tfSearch,
                new HBox(10, btnSearch, btnBack), taResult);
        return new Scene(root, 700, 420);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUSCAR POR PLATAFORMA
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildSearchByPlatformScene() {
        VBox root = darkBox();
        Label header = sectionLabel("🕹 Buscar por Plataforma");

        TextField tfPlatform = styledField("PC, PS5, Xbox, Nintendo Switch...");
        Button btnSearch = actionButton("🔎 Buscar");
        Button btnBack   = backButton();

        btnSearch.setOnAction(e -> {
            List<VideoGame> results = service.findByPlatform(tfPlatform.getText().trim());
            if (results.isEmpty()) {
                showInfo("Sin resultados", "No se encontraron juegos para esa plataforma.");
            } else {
                primaryStage.setScene(buildListScene(results, "Resultados: " + tfPlatform.getText()));
            }
        });
        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        root.getChildren().addAll(header, fieldLabel("Plataforma:"), tfPlatform,
                new HBox(10, btnSearch, btnBack));
        return new Scene(root, 700, 300);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REALIZAR VENTA
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildSellScene() {
        VBox root = darkBox();
        Label header = sectionLabel("💰 Realizar Venta");

        TextField tfTitle = styledField("Título del videojuego");
        TextField tfQty   = styledField("Cantidad");
        Label     lblInfo = new Label("");
        lblInfo.setTextFill(Color.LIGHTGREEN);
        lblInfo.setFont(Font.font("Arial", 14));

        Button btnSell = actionButton("✅ Confirmar Venta");
        Button btnBack = backButton();

        btnSell.setOnAction(e -> {
            try {
                String title = tfTitle.getText().trim();
                int    qty   = Integer.parseInt(tfQty.getText().trim());
                double total = service.sellVideoGame(title, qty);
                if (total >= 0) {
                    lblInfo.setText("✔ Venta realizada. Total: $" + total);
                    showInfo("Venta exitosa",
                            "Juego: " + title + "\nCantidad: " + qty + "\nTotal: $" + total);
                }
            } catch (NumberFormatException ex) {
                showError("Error", "La cantidad debe ser un número entero.");
            }
        });
        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));

        root.getChildren().addAll(header,
                fieldLabel("Título del juego:"), tfTitle,
                fieldLabel("Cantidad:"), tfQty,
                new HBox(10, btnSell, btnBack), lblInfo);
        return new Scene(root, 700, 380);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MOSTRAR VENTAS
    // ══════════════════════════════════════════════════════════════════════════
    private Scene buildSalesScene() {
        VBox root = darkBox();
        Label header = sectionLabel("📊 Ventas Realizadas");

        List<Sale> sales = service.getSales();
        if (sales.isEmpty()) {
            root.getChildren().add(new Label("No hay ventas registradas en esta sesión."));
        } else {
            for (Sale s : sales) {
                Label lbl = new Label(s.toString());
                lbl.setTextFill(Color.LIGHTCYAN);
                lbl.setFont(Font.font("Courier New", 12));
                lbl.setWrapText(true);
                root.getChildren().add(lbl);
            }
        }

        Button btnBack = backButton();
        btnBack.setOnAction(e -> primaryStage.setScene(buildMainMenu()));
        root.getChildren().add(btnBack);

        ScrollPane sp = new ScrollPane(root);
        sp.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        sp.setFitToWidth(true);
        return new Scene(sp, 700, 500);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════════════════
    private VBox darkBox() {
        VBox b = new VBox(12);
        b.setPadding(new Insets(25));
        b.setStyle("-fx-background-color: #1a1a2e;");
        return b;
    }

    private Button menuButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(420);
        b.setPrefHeight(42);
        b.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e0e0e0; "
                + "-fx-font-size: 14px; -fx-font-family: Arial; "
                + "-fx-border-color: #e94560; -fx-border-radius: 5; -fx-background-radius: 5;");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle()
                .replace("-fx-background-color: #16213e", "-fx-background-color: #e94560")));
        b.setOnMouseExited (e -> b.setStyle(b.getStyle()
                .replace("-fx-background-color: #e94560", "-fx-background-color: #16213e")));
        return b;
    }

    private Button actionButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; "
                + "-fx-font-size: 13px; -fx-background-radius: 5;");
        return b;
    }

    private Button backButton() {
        Button b = new Button("⬅ Volver al menú");
        b.setStyle("-fx-background-color: #444; -fx-text-fill: white; "
                + "-fx-font-size: 13px; -fx-background-radius: 5;");
        return b;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e0e0e0; "
                + "-fx-prompt-text-fill: #888; -fx-border-color: #e94560; "
                + "-fx-border-radius: 4; -fx-background-radius: 4;");
        return tf;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#e0e0e0"));
        l.setFont(Font.font("Arial", 13));
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#e94560"));
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return l;
    }

    private void styleRadio(RadioButton rb) {
        rb.setTextFill(Color.web("#e0e0e0"));
        rb.setFont(Font.font("Arial", 13));
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}