package rpgdecorator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class HeroBuilder {

    private HeroBuilder() {
    }

    public static Hero build(List<String> options) {
        Set<String> selected = new HashSet<>(options);

        Hero hero = new BaseHero("Valen");

        if (selected.contains("steelArmor")) {
            hero = new SteelArmor(hero);
        }
        if (selected.contains("shadowBlade")) {
            hero = new ShadowBlade(hero);
        }
        if (selected.contains("firePower")) {
            hero = new FirePower(hero);
        }
        if (selected.contains("speedBuff")) {
            hero = new SpeedBuff(hero);
        }
        if (selected.contains("guardianBlessing")) {
            hero = new GuardianBlessing(hero);
        }

        return hero;
    }
}