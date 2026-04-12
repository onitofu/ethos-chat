package ru.nyansus.mc.ethos_chat.rpname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import ru.nyansus.mc.ethos_chat.color.PlayerColorManager;

public class NametagManager implements Listener {

    private static final String TEAM_NAME = "ethos_rp_hide";
    private static final String LEGACY_TEAM_NAME = "domya_rp_hide";
    private static final AxisAngle4f NO_ROTATION = new AxisAngle4f(0, 0, 0, 1);

    private final RpNameManager rpNameManager;
    private final PlayerColorManager colorManager;
    private final JavaPlugin plugin;
    private final Supplier<NametagConfig> configSupplier;
    private final Map<UUID, List<TextDisplay>> displays = new HashMap<>();

    public NametagManager(RpNameManager rpNameManager, PlayerColorManager colorManager,
                          JavaPlugin plugin, Supplier<NametagConfig> configSupplier) {
        this.rpNameManager = rpNameManager;
        this.colorManager = colorManager;
        this.plugin = plugin;
        this.configSupplier = configSupplier;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeNametag(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        scheduleRefresh(event.getPlayer());
    }

    private void scheduleRefresh(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> refreshNametag(player), 1L);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
            hideNametag(player);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncVisibility(player), 1L);
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            hideNametag(player);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> syncVisibility(player), 1L);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        PotionEffectType type = event.getModifiedType();
        if (type == null || !type.equals(PotionEffectType.INVISIBILITY)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> syncVisibility(player), 1L);
    }

    private boolean shouldHide(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR
                || player.hasPotionEffect(PotionEffectType.INVISIBILITY)
                || player.isSneaking();
    }

    private void syncVisibility(Player player) {
        setNametagVisible(player, !shouldHide(player));
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)
                || !(event.getMount() instanceof Player mount)) {
            return;
        }
        detachNametag(mount);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player)
                || !(event.getDismounted() instanceof Player mount)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> attachNametag(mount), 1L);
    }

    private void hideNametag(Player player) {
        setNametagVisible(player, false);
    }

    private void showNametag(Player player) {
        setNametagVisible(player, true);
    }

    private void detachNametag(Player player) {
        List<TextDisplay> list = displays.get(player.getUniqueId());
        if (list == null) {
            return;
        }
        for (TextDisplay display : list) {
            if (display != null && !display.isDead()) {
                display.setVisibleByDefault(false);
                player.removePassenger(display);
            }
        }
    }

    private void attachNametag(Player player) {
        if (!player.isOnline()) {
            return;
        }
        List<TextDisplay> list = displays.get(player.getUniqueId());
        if (list == null) {
            return;
        }
        for (TextDisplay display : list) {
            if (display != null && !display.isDead()) {
                player.addPassenger(display);
                display.setVisibleByDefault(true);
            }
        }
    }

    private void setNametagVisible(Player player, boolean visible) {
        List<TextDisplay> list = displays.get(player.getUniqueId());
        if (list == null) {
            return;
        }
        for (TextDisplay display : list) {
            if (display != null && !display.isDead()) {
                display.setVisibleByDefault(visible);
            }
        }
    }

    public void refreshNametag(Player player) {
        removeNametag(player);
        if (!player.isOnline()) {
            return;
        }
        if (!rpNameManager.hasNametag(player)) {
            removeFromTeam(player);
            return;
        }
        addToTeam(player);
        createNametag(player);
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshNametag(player);
        }
    }

    public void removeAll() {
        for (List<TextDisplay> list : displays.values()) {
            for (TextDisplay display : list) {
                if (display != null && !display.isDead()) {
                    display.remove();
                }
            }
        }
        displays.clear();
    }

    private void createNametag(Player player) {
        NametagConfig cfg = configSupplier.get();
        List<TextDisplay> entityList = new ArrayList<>();

        Component nameComponent = colorManager.renderGradient(
                rpNameManager.getDisplayName(player), player);

        TextDisplay nameDisplay = spawnDisplay(player, nameComponent,
                new Vector3f(0, cfg.nameOffset(), 0), new Vector3f(1, 1, 1));
        player.addPassenger(nameDisplay);
        entityList.add(nameDisplay);

        rpNameManager.getRace(player).ifPresent(race -> {
            String raceText = cfg.raceFormat().replace("{race}", race);
            Component raceComponent = Component.text(raceText, NamedTextColor.GRAY);
            float s = cfg.raceScale();

            TextDisplay raceDisplay = spawnDisplay(player, raceComponent,
                    new Vector3f(0, cfg.raceOffset(), 0), new Vector3f(s, s, s));
            player.addPassenger(raceDisplay);
            entityList.add(raceDisplay);
        });

        displays.put(player.getUniqueId(), entityList);
        if (shouldHide(player)) {
            setNametagVisible(player, false);
        }
    }

    private TextDisplay spawnDisplay(Player player, Component text,
                                      Vector3f translation, Vector3f scale) {
        return player.getWorld().spawn(player.getLocation(), TextDisplay.class, entity -> {
            entity.text(text);
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setPersistent(false);
            entity.setTransformation(new Transformation(
                    translation, NO_ROTATION, scale, NO_ROTATION));
        });
    }

    private void removeNametag(Player player) {
        List<TextDisplay> list = displays.remove(player.getUniqueId());
        if (list != null) {
            for (TextDisplay display : list) {
                if (display != null && !display.isDead()) {
                    display.remove();
                }
            }
        }
    }

    private void addToTeam(Player player) {
        Team team = getOrCreateTeam();
        team.addEntity(player);
    }

    private void removeFromTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team != null) {
            team.removeEntity(player);
        }
    }

    private Team getOrCreateTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team legacy = scoreboard.getTeam(LEGACY_TEAM_NAME);
        if (legacy != null) {
            legacy.unregister();
        }
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.registerNewTeam(TEAM_NAME);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        return team;
    }

    public record NametagConfig(float nameOffset, float raceOffset,
                                float raceScale, String raceFormat) {
    }
}
