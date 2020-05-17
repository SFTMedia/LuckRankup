package com.blalp.luckrankup;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.blalp.luckrankup.common.RankupCommand;
import com.google.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;

import net.luckperms.api.LuckPerms;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

@Plugin(id = "luckrankup", name = "Luck Rankup", version = "1.0", description = "Simple rank plugin for luckperms", authors = {
        "blalp" })
public class SpongeLuckRankup implements CommandExecutor {

    private LuckPerms luckPerms;
    private @NonNull ConfigurationNode config;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configFolder;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Optional<ProviderRegistration<LuckPerms>> provider = Sponge.getServiceManager()
                .getRegistration(LuckPerms.class);
        if (provider.isPresent()) {
            luckPerms = provider.get().getProvider();
        } else {
            System.out.println("[LuckRankup] Error. LuckPerms not found");
            return;
        }
        try {
            Sponge.getAssetManager().getAsset(this, "config.yml").get()
                    .copyToFile(Paths.get(configFolder.toAbsolutePath().toString(), "config.yml"));
            config = YAMLConfigurationLoader.builder()
                    .setPath(Paths.get(configFolder.toAbsolutePath().toString(), "config.yml")).build().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommandSpec myCommandSpec = CommandSpec.builder().description(Text.of("Rankup")).permission("luckrankup.rankup")
                .executor(this).build();
        Sponge.getCommandManager().register(this, myCommandSpec, "rankup");

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of(RankupCommand.doRankUp(config.getNode("backendName").getString(),
                config.getNode("frontendName").getString(), config.getNode("track").getString(), src.getIdentifier(),
                luckPerms, config.getNode("debug").getBoolean())));
        return null;
    }
}