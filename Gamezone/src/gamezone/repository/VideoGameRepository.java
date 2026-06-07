package gamezone.repository;

import gamezone.entities.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";

    // ── CREATE ──────────────────────────────────────────────────────────────
    public boolean add(VideoGame game) throws Exception {
        List<VideoGame> all = getAll();
        for (VideoGame v : all) {
            if (v.getTitle().equalsIgnoreCase(game.getTitle())) {
                return false; // duplicado
            }
        }
        all.add(game);
        saveAll(all);
        return true;
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────
    public List<VideoGame> getAll() {
        List<VideoGame> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (FileReader reader = new FileReader(file)) {
            JSONParser parser = new JSONParser();
            JSONArray  array  = (JSONArray) parser.parse(reader);

            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                String type = (String) json.get("type");

                String title    = (String) json.get("title");
                double price    = toDouble(json.get("price"));
                String platform = (String) json.get("platform");
                int    stock    = (int)(long) json.get("stock");
                String genre    = (String) json.get("genre");

                if ("Digital".equals(type)) {
                    double sizeGB           = toDouble(json.get("sizeGB"));
                    String downloadPlatform = (String) json.get("downloadPlatform");
                    list.add(new DigitalVideoGame(title, price, platform,
                            stock, genre, sizeGB, downloadPlatform));
                } else {
                    String condition   = (String) json.get("condition");
                    String distributor = (String) json.get("distributor");
                    list.add(new PhysicalVideoGame(title, price, platform,
                            stock, genre, condition, distributor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── READ by TITLE ─────────────────────────────────────────────────────────
    public VideoGame findByTitle(String title) {
        for (VideoGame v : getAll()) {
            if (v.getTitle().equalsIgnoreCase(title)) return v;
        }
        return null;
    }

    // ── READ by PLATFORM ──────────────────────────────────────────────────────
    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame v : getAll()) {
            if (v.getPlatform().equalsIgnoreCase(platform)) result.add(v);
        }
        return result;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(String title, VideoGame updated) throws Exception {
        List<VideoGame> all     = getAll();
        boolean         changed = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getTitle().equalsIgnoreCase(title)) {
                all.set(i, updated);
                changed = true;
                break;
            }
        }
        if (changed) saveAll(all);
        return changed;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(String title) throws Exception {
        List<VideoGame> all     = getAll();
        boolean         removed = all.removeIf(v -> v.getTitle().equalsIgnoreCase(title));
        if (removed) saveAll(all);
        return removed;
    }

    // ── SAVE ALL (write JSON file) ────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void saveAll(List<VideoGame> list) throws Exception {
        new File("data").mkdirs();
        JSONArray array = new JSONArray();

        for (VideoGame v : list) {
            JSONObject obj = new JSONObject();
            obj.put("title",    v.getTitle());
            obj.put("price",    v.getPrice());
            obj.put("platform", v.getPlatform());
            obj.put("stock",    (long) v.getStock());
            obj.put("genre",    v.getGenre());

            if (v instanceof DigitalVideoGame dv) {
                obj.put("type",             "Digital");
                obj.put("sizeGB",           dv.getSizeGB());
                obj.put("downloadPlatform", dv.getDownloadPlatform());
            } else if (v instanceof PhysicalVideoGame pv) {
                obj.put("type",        "Físico");
                obj.put("condition",   pv.getCondition());
                obj.put("distributor", pv.getDistributor());
            }
            array.add(obj);
        }

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(array.toJSONString());
        }
    }

    private double toDouble(Object o) {
        if (o instanceof Long)   return ((Long) o).doubleValue();
        if (o instanceof Double) return (Double) o;
        return 0.0;
    }
}