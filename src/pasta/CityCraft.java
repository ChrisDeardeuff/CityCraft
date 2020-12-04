package pasta;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CityCraft extends JavaPlugin implements Listener
{
    ArrayList<Nation> nationsList = new ArrayList<>();

    @Override
    public void onEnable()
    {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand(Commands.CREATE_NATION).setExecutor(new CommandHandler());
        this.getCommand(Commands.LIST_NATIONS).setExecutor(new CommandHandler());
        this.getCommand(Commands.DELETE_NATION).setExecutor(new CommandHandler());
        this.getCommand(Commands.RENAME_NATION).setExecutor(new CommandHandler());
        this.getCommand(Commands.SET_LEADER).setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }


    String getNationsListStr()
    {
        String output = "";
        for (Nation n: nationsList) output += n.getName()+"("+n.getLeader().getName()+") , ";
        return output;
    }

    Nation getNationFromName(String _nationName)
    {
        Nation output = null;
        for (Nation n: nationsList)
        {
            if (n.getName().equals(_nationName)) output = n;
        }
        return output;
    }


    Nation getLeaderNation(Player _p)
    {
        Nation output = null;
        for (Nation n: nationsList)
        {
            if (n.getLeader().getUniqueId() == _p.getUniqueId()) output = n;
        }
        return output;
    }

    class CommandHandler implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            String command = cmd.getName();

            if (command.equals(Commands.CREATE_NATION))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    if (args.length == 1)
                    {
                        cmd_createNation(p, args[0]);
                    }
                    else p.sendMessage("Invalid!");
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.DELETE_NATION))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_deleteNation(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.RENAME_NATION))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    if (args.length == 1)
                    {
                        cmd_renameNation(p, args[0]);
                    }
                    else p.sendMessage("Invalid!");
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.SET_LEADER))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    if (args.length == 1)
                    {
                        cmd_setLeader(p, args[0]);
                    }
                    else p.sendMessage("Invalid!");
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.LIST_NATIONS))
            {
                cmd_listNations(sender);
            }

            else sender.sendMessage("ERROR");

            return true;
        }
    }


    void cmd_createNation(Player _p, String _name)
    {
        try
        {
            Nation n = getLeaderNation(_p);
            if (n == null)
            {
                if (getNationFromName(_name) == null)
                {
                    nationsList.add(new Nation(_p, _name));
                    _p.sendMessage("You are now the leader of '" + _name + "'");
                }
                else _p.sendMessage("Nation already exists!");
            }
            else _p.sendMessage("You are already the leader of '" + n.getName() + "'");
        }
        catch (Exception ex) { _p.sendMessage(ex.toString()); }
    }
    void cmd_deleteNation(Player _p)
    {
        Nation n = getLeaderNation(_p);
        if (n != null)
        {
            nationsList.remove(n);
            _p.sendMessage("Nation '"+n.getName()+"' has been deleted!");
        }
        else _p.sendMessage("You are not the leader of any nation!");
    }
    void cmd_renameNation(Player _p, String _newName)
    {
        Nation n = getLeaderNation(_p);
        if (n != null)
        {
            n.setName(_newName);
            _p.sendMessage("Your nation has been renamed to '"+n.getName()+"'");
        }
        else _p.sendMessage("You are not the leader of any nation!");
    }
    void cmd_setLeader(Player _p, String _leaderName)
    {
        Nation n = getLeaderNation(_p);
        if (n != null)
        {
            Player leader = Bukkit.getPlayer(_leaderName);
            if (leader != null)
            {
                n.setLeader(leader);
                _p.sendMessage(leader.getName()+" is now leader of '"+n.getName()+"'");
                leader.sendMessage("You are now the leader of '"+n.getName()+"'");
            }
            else _p.sendMessage("Player '"+_leaderName+"' not found!");
        }
        else _p.sendMessage("You are not the leader of any nation!");
    }
    void cmd_listNations(CommandSender _sender)
    {
        _sender.sendMessage("NATIONS: "+getNationsListStr());
    }

}
