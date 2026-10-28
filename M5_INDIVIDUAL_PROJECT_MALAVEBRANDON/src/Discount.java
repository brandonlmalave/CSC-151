/**
 * A discount that is either a percentage off or a flat dollar amount off.
 */
public class Discount {

    public enum Type { NONE, PERCENT, FIXED }

    private Type type;
    private double amount; // percent (e.g. 10 = 10%) or dollars

    public Discount(Type type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public static Discount none() {
        return new Discount(Type.NONE, 0);
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }

    /**
     * Returns how many dollars come off the subtotal.
     * PERCENT: subtotal x (amount / 100)
     * FIXED:   amount (but never more than the subtotal)
     * NONE:    0
     */
   public double calculateDiscount(double subtotal) {
    if (type == Type.PERCENT) {
        return subtotal * (amount / 100);
    } else if (type == Type.FIXED) {
        return Math.min(amount, subtotal);
    } else {
        return 0;
    }
}
}