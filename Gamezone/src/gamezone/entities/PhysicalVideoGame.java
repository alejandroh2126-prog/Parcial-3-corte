package gamezone.entities;

public class PhysicalVideoGame extends VideoGame implements Sellable, Catalogable {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform,
                             int stock, String genre,
                             String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition   = condition;
        this.distributor = distributor;
    }

    public String getCondition()              { return condition; }
    public String getDistributor()            { return distributor; }
    public void setCondition(String condition){ this.condition   = condition; }
    public void setDistributor(String d)      { this.distributor = d; }

    @Override
    public double calculateFinalPrice() {
        if (condition != null && condition.equalsIgnoreCase("usado")) {
            return price * 0.75;
        }
        return price;
    }

    @Override
    public double sell(int qty) {
        if (qty > stock) return -1;
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return "📦 [FÍSICO] " + title + " | Precio final: $"
                + calculateFinalPrice() + " | Stock: " + stock
                + " | Condición: " + condition;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, "Físico", platform, genre,
                "$" + calculateFinalPrice(), stock,
                condition, distributor};
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Condición: " + condition
                + " | Distribuidor: " + distributor
                + " | Precio final: $" + calculateFinalPrice();
    }
}