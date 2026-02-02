package fun.eqad.ponyparkourapi_example;

import fun.eqad.ponyparkour.api.PonyParkourAPI;
import fun.eqad.ponyparkour.arena.ParkourArena;
import fun.eqad.ponyparkour.arena.ParkourSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PonyParkourAPI_Example extends JavaPlugin {

    private PonyParkourAPI ponyParkourAPI;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PonyParkour") == null) {
            getLogger().severe("PonyParkour未安装");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // 获取PonyParkour API
        ponyParkourAPI = ((fun.eqad.ponyparkour.PonyParkour) Bukkit.getPluginManager().getPlugin("PonyParkour")).getAPI();

        getLogger().info("PonyParkourAPI_Example已成功加载");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("pptest")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info":
                testGetInfo(player);
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage("用法: /pptest join <场地名称>");
                    return true;
                }
                testJoinArena(player, args[1]);
                break;
            case "leave":
                testLeaveArena(player);
                break;
            case "list":
                testListArenas(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.YELLOW + "/pptest info - 显示当前跑酷状态");
        player.sendMessage(ChatColor.YELLOW + "/pptest join <场地> - 加入跑酷场地");
        player.sendMessage(ChatColor.YELLOW + "/pptest leave - 离开当前跑酷");
        player.sendMessage(ChatColor.YELLOW + "/pptest list - 列出所有场地");
    }

    private void testGetInfo(Player player) {
        // 返回玩家是否正在跑酷
        boolean isPlaying = ponyParkourAPI.isPlaying(player);
        player.sendMessage(ChatColor.YELLOW + "正在跑酷: " + (isPlaying ? ChatColor.GREEN + "是" : ChatColor.RED + "否"));

        if (isPlaying) {
            // 获取玩家会话
            ParkourSession session = ponyParkourAPI.getSession(player);
            if (session != null) {
                // 返回当前会话的场地
                player.sendMessage(ChatColor.AQUA + "当前场地: " + session.getArena().getName());
                // 返回当前会话的检查点
                player.sendMessage(ChatColor.AQUA + "当前检查点: " + session.getCurrentCheckpointIndex());
                // 返回当前会话的已用时间
                long elapsed = (System.currentTimeMillis() - session.getStartTime()) / 1000;
                player.sendMessage(ChatColor.AQUA + "已用时间: " + elapsed + "秒");
            }
        }

        player.sendMessage(ChatColor.YELLOW + "PonyParkour版本: " + ponyParkourAPI.getPlugin().getDescription().getVersion());
    }

    // 加入跑酷场地
    private void testJoinArena(Player player, String arenaName) {
        if (ponyParkourAPI.joinArena(player, arenaName)) {
            player.sendMessage(ChatColor.GREEN + "成功加入跑酷场地: " + arenaName);
        } else {
            player.sendMessage(ChatColor.RED + "加入跑酷场地失败！场地可能不存在。");
        }
    }

    // 离开跑酷场地
    private void testLeaveArena(Player player) {
        if (ponyParkourAPI.leaveArena(player)) {
            player.sendMessage(ChatColor.GREEN + "成功离开跑酷");
        } else {
            player.sendMessage(ChatColor.RED + "离开失败！你可能不在跑酷中。");
        }
    }

    // 返回所有跑酷场地
    private void testListArenas(Player player) {
        Map<String, ParkourArena> arenas = ponyParkourAPI.getArenas();
        player.sendMessage(ChatColor.YELLOW + "可用场地 (" + arenas.size() + "):");
        for (String name : arenas.keySet()) {
            player.sendMessage(ChatColor.GRAY + "- " + name);
        }
    }
}
