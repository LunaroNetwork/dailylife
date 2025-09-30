package nl.openminetopia.modules.portal.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.portal.PortalModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

@CommandAlias("koppel|link")
public class LinkCommand extends BaseCommand {

    @Default
    public void verify(Player player, String token) {
        verifyPlayer(player, token);
    }

    @SuppressWarnings("unchecked")
    private void verifyPlayer(Player player, String token) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("token", token);
        requestBody.put("minecraft_username", player.getName());
        requestBody.put("minecraft_uuid", player.getUniqueId().toString());

        PortalModule portalModule = DailyLife.getModuleManager().get(PortalModule.class);
        WebClient webClient = WebClient.create(DailyLife.getInstance().getOrCreateVertx());

        webClient.postAbs(portalModule.getPortalApiUrl() + "/minecraft/verify")
                .putHeader("Content-Type", "application/json")
                .putHeader("X-API-Key", DailyLife.getDefaultConfiguration().getPortalToken())
                .sendBuffer(Buffer.buffer(requestBody.toString()))
                .onSuccess(response -> {
                    if (response.statusCode() != 200) {
                        ChatUtils.sendMessage(player, "<red>Er is iets fout gegaan bij het verifiëren van je account.");
                        DailyLife.getInstance().getLogger().warning(
                                "Response code " + response.statusCode() + " " + response.statusMessage()
                                        + " while trying to verify player " + player.getName()
                        );
                        return;
                    }
                    ChatUtils.sendMessage(player, "<green>Je account is succesvol gekoppeld!");
                })
                .onFailure(err -> {
                    ChatUtils.sendMessage(player, "<red>Er is iets fout gegaan bij het verifiëren van je account.");
                    DailyLife.getInstance().getLogger().severe(
                            "An error occurred while trying to verify player " + player.getName() + ": " + err.getMessage()
                    );
                });
    }
}
