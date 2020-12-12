package mcl;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

public class Main extends JavaPlugin implements Listener
{

    ArrayList<City> cityList = new ArrayList<>();

    @Override
    public void onEnable()
    {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand(Commands.FOUND_CITY).setExecutor(new CommandHandler());
        this.getCommand(Commands.CLAIM_LAND).setExecutor(new CommandHandler());
        this.getCommand(Commands.UNCLAIM_LAND).setExecutor(new CommandHandler());
        this.getCommand(Commands.SHOW_LAND).setExecutor(new CommandHandler());
        this.getCommand(Commands.CLEAR_DATA).setExecutor(new CommandHandler());
        this.getCommand(Commands.ADD_CITIZEN).setExecutor(new CommandHandler());
        this.getCommand(Commands.LIST_CITIZENS).setExecutor(new CommandHandler());


        cityList = SaveData.loadAllCities();
    }

    @Override
    public void onDisable()
    {
        for (City c: cityList) SaveData.writeToData(c);
        super.onDisable();
    }


    /**
     * Checks if player is a Citizen of the city when trying to break a block within city limits.
     * If they are not then cancel the breaking event.
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        event.setCancelled(!canBuild(event.getPlayer(), event.getBlock()));
    }

    /**
     * Checks if player is a citizen when trying to place a block.
     * If they are not then cancel the placing block event.
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        event.setCancelled(!canBuild(event.getPlayer(), event.getBlock()));
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event)
    {
        event.setCancelled(!canBuild(event.getPlayer(), event.getClickedBlock()));
    }

    boolean isCitizen(Player _p, City _city)
    {
        boolean output = false;
        ArrayList<CivPlayer> citizens = _city.copyCitizens();
        for (CivPlayer p : citizens)
        {
            if (p.getUUID().equals(_p.getUniqueId().toString())) output = true;
        }
        return output;
    }

    boolean canBuild(Player _p, Block _b)
    {
        boolean output = true;
        Chunk c = _b.getChunk();
        City chunkCity = getOwnerOfChunk(c);
        if (chunkCity != null)
        {
            if (!isCitizen(_p, chunkCity)) output = false;
        }
        return output;
    }

    String getcityListStr()
    {
        String output = "";
        for (City n: cityList) output += n.getName()+"("+n.getMayor().getName()+") , ";
        return output;
    }

    City getCityFromName(String _CityName)
    {
        City output = null;
        for (City n: cityList)
        {
            if (n.getName().equals(_CityName)) output = n;
        }
        return output;
    }

    City getOwnerOfChunk(Chunk _chunk)
    {
        City output = null;
        for (City c: cityList)
        {
            if (c.hasClaimed(_chunk)) output = c;
        }
        return output;
    }

    City getMayorCity(Player _p)
    {
        City output = null;
        for (City n: cityList)
        {
            if (n.getMayor().getUUID().equals(_p.getUniqueId().toString())) output = n;
        }
        return output;
    }

    int scheduleTask(Plugin _context, Runnable _func, long _delay)
    {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(_context, _func, _delay);
    }


    void highlightChunk(Player _p, Chunk _c, Material _borderMaterial)
    {
        ArrayList<Location> locList = new ArrayList<>();
        for (int i = 0; i < 16; i++)
        {
            locList.add(_c.getBlock(i, 0, 0).getLocation());
            locList.add(_c.getBlock(i, 0, 15).getLocation());
            locList.add(_c.getBlock(0, 0, i).getLocation());
            locList.add(_c.getBlock(15, 0, i).getLocation());
        }
        ArrayList<Location> surfaceLocList = new ArrayList<>();
        for (Location l : locList) surfaceLocList.add(l.getWorld().getHighestBlockAt(l).getLocation());

        BlockData blokDat = _borderMaterial.createBlockData();

        for (Location l : surfaceLocList) _p.sendBlockChange(l, blokDat);

        Runnable clearHighlight = () ->
        {
            for (Location l : surfaceLocList) _p.sendBlockChange(l, l.getWorld().getBlockAt(l).getBlockData());
        };
        scheduleTask(this, clearHighlight, 100);
    }


    void highlightAllTerritories(Player _p)
    {
        for (City c : cityList)
        {
            Material border = Material.BLACK_CONCRETE;
            if (isCitizen(_p, c)) border = Material.CYAN_CONCRETE;
            highlightTerritory(_p, c, border);
        }
    }

    void highlightTerritory(Player _p, City _city)
    {
        highlightTerritory(_p, _city, Material.RED_CONCRETE);
    }
    void highlightTerritory(Player _p, City _city, Material _borderMaterial)
    {
        ArrayList<Chunk> landTmp = _city.copyLand();
        for (Chunk c: landTmp) highlightChunk(_p, c, _borderMaterial);
    }

    class CommandHandler implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            String command = cmd.getName();

            if (command.equals(Commands.FOUND_CITY))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    if (args.length == 1)
                    {
                        cmd_foundCity(p, args[0]);
                    }
                    else p.sendMessage("Invalid!");
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.CLAIM_LAND))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_claimLand(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.UNCLAIM_LAND))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_unclaimLand(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.ADD_CITIZEN))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    if (args.length == 1)
                    {
                        String citizenName = args[0];
                        Player citizen = Bukkit.getPlayer(citizenName);
                        if (citizen != null)  cmd_addCitizen(p, citizen);
                        else sender.sendMessage("Player '"+citizenName+"' not found!");
                    }
                    else sender.sendMessage("Invalid!");
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.LIST_CITIZENS))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_citizenList(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.LIST_CITIES))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_cityList(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.SHOW_LAND))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player)sender;
                    cmd_showLand(p);
                }
                else sender.sendMessage("Only players can use this command!");
            }
            else if (command.equals(Commands.CLEAR_DATA))
            {
               if (sender.isOp())
               {
                   cityList.clear();
                   sender.sendMessage("CivCarft data has been cleared");
               }
               else sender.sendMessage("You are not OP.");
            }
            else sender.sendMessage("ERROR");

            return true;
        }
    }

    void cmd_cityList(Player _p)
    {
        String reply = "Cities: ";
        for (City c : cityList) reply += c.getName() + ", ";
        _p.sendMessage(reply);
    }
    void cmd_citizenList(Player _p)
    {
        City n = getMayorCity(_p);
        if (n != null)
        {
            String reply = "Citizens of '"+n.getName()+"': ";
            ArrayList<CivPlayer> citizensList = n.copyCitizens();
            for (CivPlayer citizen : citizensList) reply += citizen.getName() + ", ";
            _p.sendMessage(reply);
        }
        else _p.sendMessage("You are not the mayor of any city.");
    }
    void cmd_addCitizen(Player _mayor, Player _citizen)
    {
        City n = getMayorCity(_mayor);
        if (n != null)
        {
            n.addCitizen(new CivPlayer(_citizen.getUniqueId().toString(), _citizen.getName()));
            _mayor.sendMessage("'"+_citizen.getName()+"' is now a citizen of '"+n.getName()+"'");
            _citizen.sendMessage("You are now a citizen of '"+n.getName()+"'");
        }
        else _mayor.sendMessage("You are not the mayor of any city.");
    }
    void cmd_foundCity(Player _p, String _name)
    {
        City n = getMayorCity(_p);
        if (n == null)
        {
            if (getCityFromName(_name) == null)
            {
                cityList.add(new City(new CivPlayer(_p.getUniqueId().toString(), _p.getName()), _name));
                cmd_claimLand(_p);
                _p.sendMessage("You are now the mayor of '" + _name + "'");
            }
            else _p.sendMessage("City already exists!");
        }
        else _p.sendMessage("You are already the mayor of '" + n.getName() + "'");
    }
    void cmd_unclaimLand(Player _p)
    {
        City n = getMayorCity(_p);
        if (n != null)
        {
            Chunk c = _p.getLocation().getChunk();
            if (n.hasClaimed(c))
            {
                n.delLand(c);
                _p.sendMessage("'" + n.getName() + "' has relinquished control of chunk (" + c.getX() + "," + c.getZ() + ")");
            }
            else _p.sendMessage("Your city does not own this chunk!");
            highlightAllTerritories(_p);
        }
        else _p.sendMessage("You are not the mayor of any city.");
    }
    void cmd_claimLand(Player _p)
    {
        City n = getMayorCity(_p);
        if (n != null)
        {
            Chunk c = _p.getLocation().getChunk();
            if (!n.hasClaimed(c))
            {
                City owner = getOwnerOfChunk(c);
                if (owner == null)
                {
                    int pop = n.getPopulation();
                    int territories = n.getTerritoryCount();
                    int maxTerritories = pop * 10;
                    if (territories < maxTerritories)
                    {
                        n.addLand(c);
                        _p.sendMessage("'" + n.getName() + "' has claimed chunk (" + c.getX() + "," + c.getZ() + ")");
                    }
                    else _p.sendMessage("You need a larger population to claim more land!");
                }
                else _p.sendMessage("This chunk is owned by '"+owner.getName()+"'");
            }
            else _p.sendMessage("'"+n.getName()+"' has already claimed this chunk.");
            highlightAllTerritories(_p);
        }
        else _p.sendMessage("You are not the mayor of any city.");
    }
    void cmd_showLand(Player _p)
    {
        highlightAllTerritories(_p);
    }


}