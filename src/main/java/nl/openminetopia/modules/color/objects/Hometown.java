package nl.openminetopia.modules.color.objects;

import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.configuration.ColorsConfiguration;
import nl.openminetopia.modules.color.configuration.components.ColorComponent;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Getter
@Setter
public abstract class Hometown {

    public String name;
    public String colorId;

    public Hometown(String name, String colorId) {
        this.name = name;
        this.colorId = colorId;
    }

    public String displayName() {
        return name;
    }

    public String color() {
        ColorComponent component = config().color(colorId);
        if (component == null) component = new ColorComponent(null, "<gray>None", DailyLife.getDefaultConfiguration().getDefaultNameColor());

        return component.colorPrefix();
    }

    private ColorsConfiguration config() {
        return DailyLife.getModuleManager().get(ColorModule.class).getConfiguration();
    }

}
