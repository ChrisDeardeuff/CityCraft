package pasta;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class City
{

    private String cityName;
    private Player mayor;
    private ChunkHashTable ownedLand;
    private Location TPP;
    ArrayList<Player> residents;


    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public Player getMayor()
    {
        return mayor;
    }

    public void setMayor(Player mayor)
    {
        this.mayor = mayor;
    }

    public ChunkHashTable getOwnedLand()
    {
        return ownedLand;
    }

    public void setOwnedLand(ChunkHashTable ownedLand)
    {
        this.ownedLand = ownedLand;
    }

    public Location getTPP()
    {
        return TPP;
    }

    public void setTPP(Location TPP)
    {
        this.TPP = TPP;
    }

    public ArrayList<Player> getResidents()
    {
        return residents;
    }

    public void setResidents(ArrayList<Player> residents)
    {
        this.residents = residents;
    }


    class ChunkHashTable
    {

        ArrayList[] harray = new ArrayList[500];

        public ChunkHashTable()
        {
            for (int i = 0; i < 500; i++)
            {

                harray[i] = new ArrayList<Chunk>();
            }

        }

        /**
         * Insert Chunk c into hashtable based on key.
         *
         * @param c Chunk to insert.
         */
        public void insert(Chunk c)
        {

            int key = hashFunction(c);

            harray[key].add(c);

        }

        /**
         * Removes Chunk from hash table
         *
         * @param c Chunk to be removed
         */
        public void remove(Chunk c)
        {

            harray[hashFunction(c)].remove(c);

        }

        /**
         * Searches hash table for Chunk c.
         *
         * @param c Chunk to look for
         * @return true if chunk is in table, false if not.
         */
        public boolean find(Chunk c)
        {
            int key = hashFunction(c);

            return harray[key].contains(c);
        }

        /**
         * Returns value of key for Chunk c.
         * Key is calculated by adding the Chunks z and x coords and modding by 500 (size of hash table).
         *
         * @param c chunk to find key for.
         * @return key of Chunk c.
         */
        public int hashFunction(Chunk c)
        {

            int key = (c.getX() + c.getZ()) % 500;

            return key;
        }

    }


}
