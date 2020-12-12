package mcl;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;

public class City
{
    private CivPlayer mayor;
    private ArrayList<Chunk> land;
    private String name;
    private ArrayList<CivPlayer> citizens;
    //private Location tpp;

    public City(CivPlayer _mayor, String _name)
    {
        citizens = new ArrayList<>();
        citizens.add(_mayor);
        land = new ArrayList<>();
        mayor = _mayor;
        name = _name;
    }

    public String getName() { return name; }
    public void setName(String _name) { name = _name; }
    public CivPlayer getMayor() { return mayor; }
    public void setMayor(CivPlayer _mayor) { mayor = _mayor; }
    public void addLand(Chunk _chunk) { land.add(_chunk); }
    public void addLand(ArrayList<Chunk> _chunks) { land.addAll(_chunks); }
    public void delLand(Chunk _chunk) { land.remove(_chunk); }
    public boolean hasClaimed(Chunk _chunk) { return land.contains(_chunk); }
    public ArrayList<Chunk> copyLand() { return new ArrayList<>(land); }
    public void addCitizen(CivPlayer _p) { citizens.add(_p); }
    public void addCitizen(ArrayList<CivPlayer> _players){ citizens.addAll(_players); }
    public ArrayList<CivPlayer> copyCitizens() { return new ArrayList<>(citizens); }
    public int getPopulation() { return citizens.size(); }
    public int getTerritoryCount() { return land.size(); }

    /**
     *  asString for easier file writing - chris
     */
    public String asString()
    {
        String chunksStr = "";
        String citizensStr = "";
        for (Chunk chunk:land)
        {
            chunksStr += (chunk.getX() + " " +chunk.getZ() + " " + chunk.getWorld().getUID().toString() + "\n");
        }
        for (CivPlayer p : citizens)
        {
            citizensStr += (p.getUUID() + " " + p.getName() + "\n");
        }
        citizensStr = citizensStr.substring(0, citizensStr.length()-1);

        return  name + "\n" + mayor.getUUID() + "\n" + mayor.getName() + "\n" + chunksStr + "$citizens:\n" + citizensStr;
    }


}
