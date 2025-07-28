package net.redsierra.Spacio;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.redsierra.Spacio.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.List;

public class Spacio {

    private static Spacio instance;

    private final Logger logger = LoggerFactory.getLogger(Spacio.class);
    private final BotConfig botConfig;
    private final ShardManager shardManager;

    private Spacio() throws LoginException {
        this.botConfig = new BotConfig();
        String token = botConfig.getBotToken();
        List<ListenerAdapter> listeners = ListenerLoader.loadListeners("net.redsierra.Spacio.events");

        this.shardManager = DefaultShardManagerBuilder.createDefault(token)
                .setShardsTotal(botConfig.getShardCount())
                .enableIntents(EnumSet.of(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT
                ))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.listening("DROGA - Mora & C. Tangana"))
                .addEventListeners(listeners.toArray())
                .build();
    }

    public static void main(String[] args) {
        try {
            instance = new Spacio();
        } catch (LoginException e) {
            LoggerFactory.getLogger(Spacio.class).error("Failed to login bot", e);
            System.exit(1);
        }
    }

    public static Spacio getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Spacio has not been initialized yet. Call main() first.");
        }
        return instance;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public BotConfig getBotConfig() {
        return botConfig;
    }

    public Logger getLogger() {
        return logger;
    }
}
