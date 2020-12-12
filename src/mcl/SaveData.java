package mcl;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class SaveData
{
    public static final String _DATA_FOLDER_ = "./plugins/CivCraftData/";

    public static void writeToData(City saveCity)
    {
        try
        {
            File f = new File(_DATA_FOLDER_+saveCity.getName()+".city");
            f.getParentFile().mkdirs();
            f.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(f.getCanonicalPath()));
            writer.write(saveCity.asString());
            writer.close();
        }
        catch (IOException e)
        {
           e.printStackTrace();
           Bukkit.broadcastMessage("ERROR SAVING CITY DATA!");
        }
    }

    ///*
    public static City LoadFromData(String filename)
    {
        City city = null;
        try
        {
            File file = new File(filename); //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream

            String name = br.readLine();
            String mayorID = br.readLine();
            String mayorName = br.readLine();
            CivPlayer mayor = new CivPlayer(mayorID, mayorName);

            String line;
            ArrayList<Chunk> territory = new ArrayList<>();
            while(!(line = br.readLine()).equalsIgnoreCase("$citizens:"))
            {
                String[] parse = line.split(" ");
                int x = Integer.parseInt(parse[0]);
                int z = Integer.parseInt(parse[1]);
                UUID id = UUID.fromString(parse[2]);
                territory.add(Bukkit.getWorld(id).getChunkAt(x, z));
            }

            br.readLine();
            ArrayList<CivPlayer> citizens = new ArrayList<>();
            while((line = br.readLine()) != null)
            {
                String[] parse = line.split(" ");
                String id = parse[0];
                String pName = parse[1];
                citizens.add(new CivPlayer(id, pName));
            }

            br.close();

            city = new City(mayor, name);
            city.addLand(territory);
            city.addCitizen(citizens);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Bukkit.broadcastMessage("ERROR LOADING CITY DATA!");
        }

        return city;
    }
    //*/

    public static ArrayList<City> loadAllCities()
    {
        ArrayList<City> output = new ArrayList<>();
        File[] files = new File(SaveData._DATA_FOLDER_).listFiles();
        for (File f: files) output.add(LoadFromData(f.getAbsolutePath()));
        return output;
    }

}


/* .city format

    0 - [cityName]
    1 - [mayorUUID]
    2 - [x] [y] [UUID]
    3 - [x] [y] [UUID]
    4 - ....

 */


/*

    cityName
    Btopia-remix
    mayor
    b0590b4a-1edf-400b-88e9-99ae4e4dcb8d
    ownedLand
    -123
    -1118
    43ba5c47-99f8-4fcc-8a9d-7f038712f6b8

    tpp
    null

*/
