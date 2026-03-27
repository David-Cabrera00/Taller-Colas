import java.util.Objects;

// ===== Productos =====
interface Mold { String description(); }
interface Decoration { String description(); }
interface Packaging { String description(); }

// ===== Abstract Factory =====
interface BakingKitFactory {
    Mold createMold();
    Decoration createDecoration();
    Packaging createPackaging();
}

// ===== Familias concretas =====
final class GourmetFactory implements BakingKitFactory {
    public Mold createMold() { return () -> "Molde premium (silicona grado alimenticio)"; }
    public Decoration createDecoration() { return () -> "Decoración: perlas comestibles + chocolate belga"; }
    public Packaging createPackaging() { return () -> "Empaque: caja rígida con cinta elegante"; }
}

final class KidsFactory implements BakingKitFactory {
    public Mold createMold() { return () -> "Molde infantil (formas: estrellas y dinosaurios)"; }
    public Decoration createDecoration() { return () -> "Decoración: sprinkles de colores + mini toppers"; }
    public Packaging createPackaging() { return () -> "Empaque: bolsa con stickers y diseño divertido"; }
}

final class FitFactory implements BakingKitFactory {
    public Mold createMold() { return () -> "Molde fit (porciones individuales)"; }
    public Decoration createDecoration() { return () -> "Decoración: cacao puro + frutos secos"; }
    public Packaging createPackaging() { return () -> "Empaque: biodegradable con sello eco"; }
}

// ===== Cliente =====
enum KitStyle { GOURMET, INFANTIL, FIT }

final class BakingShop {
    private final BakingKitFactory factory;

    public BakingShop(BakingKitFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public void showKit() {
        Mold mold = factory.createMold();
        Decoration decoration = factory.createDecoration();
        Packaging packaging = factory.createPackaging();

        System.out.println("KIT DE REPOSTERÍA");
        System.out.println("- " + mold.description());
        System.out.println("- " + decoration.description());
        System.out.println("- " + packaging.description());
    }
}

// ===== Main =====
public class Main {

    public static void main(String[] args) {
        KitStyle style = parseStyle(args);
        BakingKitFactory factory = factoryFor(style);

        System.out.println("Estilo seleccionado: " + style);
        new BakingShop(factory).showKit();
    }

    private static KitStyle parseStyle(String[] args) {
        if (args.length == 0) return KitStyle.GOURMET;

        return switch (args[0].toLowerCase()) {
            case "gourmet" -> KitStyle.GOURMET;
            case "infantil", "kids" -> KitStyle.INFANTIL;
            case "fit", "healthy" -> KitStyle.FIT;
            default -> {
                System.out.println("Estilo no reconocido. Usa: gourmet | infantil | fit");
                yield KitStyle.GOURMET;
            }
        };
    }

    private static BakingKitFactory factoryFor(KitStyle style) {
        return switch (style) {
            case GOURMET -> new GourmetFactory();
            case INFANTIL -> new KidsFactory();
            case FIT -> new FitFactory();
        };
    }
}