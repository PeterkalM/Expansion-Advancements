package ee.blacky.papi.advancements;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Advancements extends PlaceholderExpansion {
    public boolean canRegister() {
        return true;
    }

    public String getAuthor() {
        return "matahombress";
    }

    public String getName() {
        return "Advancements";
    }

    public String getIdentifier() {
        return "Advancements";
    }

    public String getVersion() {
        return "1.2";
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        try {
            if (identifier.startsWith("playerList_")) {
                String i_split = identifier.split("playerList_")[1];
                String playerName = i_split;
                String command = null;

                if (i_split.contains(",")) {
                    String[] i = i_split.split(",");
                    playerName = i[0].trim();
                    command = i[1].trim();
                }

                OfflinePlayer p = checkPlayer(player, playerName);
                if (p == null) return "PLAYER_NOT_FOUND";

                String adv = listAdvancements(Bukkit.getPlayer(p.getUniqueId()));
                if (command == null) {
                    return adv;
                } else {
                    ((Player) player).chat(command + " " + adv);
                    return "";
                }
            }
            if (identifier.startsWith("playerListFormat_")) {
                String plName = identifier.split("playerListFormat_")[1].trim();
                OfflinePlayer p = checkPlayer(player, plName);
                return p == null ? "PLAYER_NOT_FOUND" : formatting(listAdvancements(Bukkit.getPlayer(p.getUniqueId())));
            }
            if (identifier.startsWith("player_")) {
                String[] args = identifier.split("player_")[1].split(";");
                String plName = args[0];

                OfflinePlayer p = checkPlayer(player, plName);
                if (p == null) return "PLAYER_NOT_FOUND";

                StringBuilder id = new StringBuilder();
                int i = 0;
                for (String id_2 : args) {
                    if (i != 0) {
                        if (i == 1) {
                            id.append(id_2);
                        } else {
                            id.append("_").append(id_2);
                        }
                    }
                    i++;
                }
                boolean hadv = hasAdvancement(Bukkit.getPlayer(p.getUniqueId()), id.toString());
                if (!hadv) {
                    return "NO_EXIST_ADVANCEMENT";
                }
                return "true";
            }
            if (identifier.startsWith("count_done")) {
                int done = 0;
                if (player == null) return "PLAYER_NOT_FOUND";
                for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
                    Advancement it = iter.next();
                    if (it.getKey().getNamespace().equals("minecraft") && !it.getKey().getKey().startsWith("recipes") && ((Player) player).getAdvancementProgress(it).isDone())
                        done++;
                }
                return String.valueOf(done);
            }
            if (identifier.startsWith("count_all")) {
                int all = 0;
                if (player == null) return "PLAYER_NOT_FOUND";
                for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
                    Advancement it = iter.next();
                    if (it.getKey().getNamespace().equals("minecraft") && !it.getKey().getKey().startsWith("recipes")) all++;
                }
                return String.valueOf(all);
            }
            boolean hadv = hasAdvancement((Player) player, identifier);
            if (!hadv) {
                return "NO_EXIST_ADVANCEMENT";
            }
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO_WORKING";
    }

    private static OfflinePlayer checkPlayer(OfflinePlayer executor, String name) {
        if (executor != null) return executor;
        return getOfflinePlayer(name, false);
    }

    public static String listAdvancements(Player player) {
        List<String> ach = new ArrayList<>();
        List<String> cantidad = new ArrayList<>();
        for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
            Advancement adv = iter.next();
            String key = adv.getKey().getKey();
            if (!key.startsWith("recipes")) {
                String parent = key.split("/")[0];
                if (!cantidad.contains(parent)) {
                    cantidad.add(parent);
                }
            }
        }
        for (String s : cantidad) {
            for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
                Advancement adv = iter.next();
                String key = adv.getKey().getKey();
                if (key.startsWith(s)) {
                    ach.add(key);
                }
            }
        }
        StringBuilder def = new StringBuilder();
        for (String ah : ach) {
            def.append(ah).append(";").append(hasAdvancement(player, ah)).append(",");
        }
        return def.toString();
    }

    public static String formatting(String listAdv) {
        return listAdv.replaceAll(",", "\n").replaceAll(";", ": ").replaceAll("true", ChatColor.GREEN + "true" + ChatColor.RESET).replaceAll("false", ChatColor.RED + "false");
    }

    public static boolean hasAdvancement(Player player, String name) {
        Advancement ach = null;
        for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
            Advancement it = iter.next();
            if (it.getKey().getKey().equalsIgnoreCase(name)) {
                ach = it;
                break;
            }
        }
        if (ach == null) return false; // Check if advancement is found
        AdvancementProgress progress;
        try {
            progress = player.getAdvancementProgress(ach);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return progress.isDone();
    }

    public static OfflinePlayer getOfflinePlayer(String playerStr, boolean isUUID) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if ((isUUID) && (player.getUniqueId().toString().equalsIgnoreCase(playerStr))) {
                return player;
            }
            if (player.getName() != null && player.getName().equalsIgnoreCase(playerStr)) {
                return player;
            }
        }
        return null;
    }
}

