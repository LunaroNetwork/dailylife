package nl.openminetopia.modules.data.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.connection.sqlite.SqliteFileDriver;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.data.storm.StormDatabase;

import java.io.File;

public class SQLiteAdapter extends MySQLAdapter {

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            StormDatabase.getInstance().setStorm(new Storm(new SqliteFileDriver(new File(DailyLife.getInstance().getDataFolder(), "database.db"))));
            registerStormModels();
        } catch (Exception e) {
            DailyLife.getInstance().getLogger().severe("Failed to connect to SQLite database: " + e.getMessage());
            DailyLife.getInstance().getLogger().severe("Disabling the plugin...");
            DailyLife.getInstance().getServer().getPluginManager().disablePlugin(DailyLife.getInstance());
        }
    }
}
