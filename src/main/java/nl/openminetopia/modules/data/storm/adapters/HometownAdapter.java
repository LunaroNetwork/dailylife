package nl.openminetopia.modules.data.storm.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;
import nl.openminetopia.modules.color.models.HometownModel;
import nl.openminetopia.modules.color.objects.Hometown;
import nl.openminetopia.modules.data.storm.StormDatabase;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class HometownAdapter extends StormTypeAdapter<Hometown> {

    @Override
    public Hometown fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        String hometownName = sqlValue.toString();

        try {
            // Haal ALLE hometowns op en filter lokaal op naam
            Collection<HometownModel> results = StormDatabase.getInstance()
                    .getStorm()
                    .buildQuery(HometownModel.class)
                    .execute()
                    .get(); // CompletableFuture<Collection<HometownModel>> -> block tot resultaat

            HometownModel model = results.stream()
                    .filter(h -> h.getName() != null && h.getName().equalsIgnoreCase(hometownName))
                    .findFirst()
                    .orElse(null);

            if (model == null) return null;

            // Hometown is abstract, dus we maken een kleine concrete instance
            return new Hometown(model.getName(), model.getColorId()) {};
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object toSql(Storm storm, Hometown value) {
        if (value == null) return null;
        // Sla enkel de unieke naam op
        return value.getName();
    }

    @Override
    public String getSqlBaseType() {
        return "VARCHAR(255)";
    }

    @Override
    public boolean escapeAsString() {
        return true;
    }
}
