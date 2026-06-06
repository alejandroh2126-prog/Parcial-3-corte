package gamezone.entities;

public interface Sellable {
    double sell(int qty);
    String getDisplayInfo();
}