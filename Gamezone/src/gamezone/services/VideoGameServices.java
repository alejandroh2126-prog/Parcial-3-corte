package gamezone.services;

import gamezone.entities.*;
import gamezone.repository.VideoGameRepository;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class VideoGameService {

    private final VideoGameRepository repo = new VideoGameRepository();
    private final List<Sale>          sales = new ArrayList<>();

    // ── AGREGAR ───────────────────────────────────────────────────────────────
    public boolean addVideoGame(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (game.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (game.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        try {
            boolean added = repo.add(game);
            if (!added) {
                showAlert(Alert.AlertType.WARNING,
                        "Catálogo GameZone",
                        "El videojuego ya existe en el catálogo.");
            }
            return added;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar: " + e.getMessage());
        }
    }

    // ── LISTAR ────────────────────────────────────────────────────────────────
    public List<VideoGame> getAllGames() {
        return repo.getAll();
    }

    // ── BUSCAR POR TÍTULO ─────────────────────────────────────────────────────
    public VideoGame findByTitle(String title) {
        return repo.findByTitle(title);
    }

    // ── BUSCAR POR PLATAFORMA ─────────────────────────────────────────────────
    public List<VideoGame> findByPlatform(String platform) {
        return repo.findByPlatform(platform);
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────
    public boolean updateVideoGame(String title, VideoGame updated) {
        try {
            return repo.update(title, updated);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage());
        }
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────
    public boolean deleteVideoGame(String title) {
        try {
            return repo.delete(title);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar: " + e.getMessage());
        }
    }

    // ── VENDER ───────────────────────────────────────────────────────────────
    public double sellVideoGame(String title, int qty) {
        VideoGame game = repo.findByTitle(title);

        if (game == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Venta fallida",
                    "El videojuego '" + title + "' no existe en el catálogo.");
            return -1;
        }
        if (game.getStock() < qty) {
            showAlert(Alert.AlertType.ERROR,
                    "Venta fallida",
                    "Stock insuficiente. Disponible: " + game.getStock());
            return -1;
        }

        Sellable sellable = (Sellable) game;
        double total = sellable.sell(qty);

        // Persiste el stock reducido
        try {
            repo.update(game.getTitle(), game);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Registra la venta
        String saleId = "VTA-" + (sales.size() + 1);
        sales.add(new Sale(saleId, game, qty, game.calculateFinalPrice()));

        return total;
    }

    // ── VENTAS ────────────────────────────────────────────────────────────────
    public List<Sale> getSales() {
        return sales;
    }

    // ── ALERTA UI ─────────────────────────────────────────────────────────────
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}