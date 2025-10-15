package nl.openminetopia.modules.color.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "hometowns")
public class HometownModel extends StormModel {

    @Column(name = "name", notNull = true, unique = true)
    private String name;

    @Column(name = "color_id")
    private String colorId;

}
