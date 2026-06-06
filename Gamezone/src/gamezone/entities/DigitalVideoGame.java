package gamezone.entities;

public class DigitalVideoGame extends VideoGame implements Sellable, Catalogable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform,
                            int stock, String genre,
                            double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB           = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB()               { return sizeGB; }
    public String getDownloadPlatform()     { return downloadPlatform; }
    public void setSizeGB(double sizeGB)    { this.sizeGB = sizeGB; }
    public void setDownloadPlatform(String p){ this.downloadPlatform = p; }

    @Override
    public double calculateFinalPrice() {
        return sizeGB > 50 ? price + 5000 : price;
    }

    @Override
    public double sell(int qty) {
        if (qty > stock) return -1;
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return "🎮 [DIGITAL] " + title + " | Precio final: $"
                + calculateFinalPrice() + " | Stock: " + stock
                + " | Tamaño: " + sizeGB + " GB";
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, "Digital", platform, genre,
                "$" + calculateFinalPrice(), stock,
                sizeGB + " GB", downloadPlatform};
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Tamaño: " + sizeGB + "GB"
                + " | Plataforma descarga: " + downloadPlatform
                + " | Precio final: $" + calculateFinalPrice();
    }
}