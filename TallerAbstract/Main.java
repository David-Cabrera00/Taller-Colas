import java.util.Objects;

// ===== Productos =====
interface Chair { String description(); }
interface Sofa { String description(); }
interface CoffeeTable { String description(); }

// ===== Abstract Factory =====
interface FurnitureFactory {
    Chair createChair();
    Sofa createSofa();
    CoffeeTable createCoffeeTable();
}

// ===== Familias concretas =====
final class ModernFactory implements FurnitureFactory {
    public Chair createChair() { return () -> "Silla Moderna (metal + cuero)"; }
    public Sofa createSofa() { return () -> "Sofá Moderno (líneas rectas)"; }
    public CoffeeTable createCoffeeTable() { return () -> "Mesilla Moderna (vidrio templado)"; }
}

final class VictorianFactory implements FurnitureFactory {
    public Chair createChair() { return () -> "Silla Victoriana (madera tallada)"; }
    public Sofa createSofa() { return () -> "Sofá Victoriano (tapizado capitoné)"; }
    public CoffeeTable createCoffeeTable() { return () -> "Mesilla Victoriana (barniz oscuro)"; }
}

final class ArtDecoFactory implements FurnitureFactory {
    public Chair createChair() { return () -> "Silla ArtDeco (geometría + dorado)"; }
    public Sofa createSofa() { return () -> "Sofá ArtDeco (curvas elegantes)"; }
    public CoffeeTable createCoffeeTable() { return () -> "Mesilla ArtDeco (mármol + latón)"; }
}

// ===== Cliente =====
enum Style { MODERNA, VICTORIANA, ARTDECO }

final class FurnitureShop {
    private final FurnitureFactory factory;

    public FurnitureShop(FurnitureFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public void showCatalog() {
        Chair chair = factory.createChair();
        Sofa sofa = factory.createSofa();
        CoffeeTable table = factory.createCoffeeTable();

        System.out.println("CATÁLOGO DE MUEBLES");
        System.out.println("- " + chair.description());
        System.out.println("- " + sofa.description());
        System.out.println("- " + table.description());
    }
}

// ===== Main =====
public class Main {

    public static void main(String[] args) {
        Style style = parseStyle(args);
        FurnitureFactory factory = factoryFor(style);

        System.out.println("Estilo seleccionado: " + style);
        new FurnitureShop(factory).showCatalog();
    }

    private static Style parseStyle(String[] args) {
        if (args.length == 0) return Style.MODERNA; // default

        return switch (args[0].toLowerCase()) {
            case "moderna", "modern" -> Style.MODERNA;
            case "victoriana", "victorian" -> Style.VICTORIANA;
            case "artdeco", "art-deco" -> Style.ARTDECO;
            default -> {
                System.out.println("Estilo no reconocido. Usa: moderna | victoriana | artdeco");
                yield Style.MODERNA;
            }
        };
    }

    private static FurnitureFactory factoryFor(Style style) {
        return switch (style) {
            case MODERNA -> new ModernFactory();
            case VICTORIANA -> new VictorianFactory();
            case ARTDECO -> new ArtDecoFactory();
        };
    }
}