package mcl;

import org.bukkit.entity.Player;

public class CivPlayer
{
    private String UUID;
    private String name;

    public CivPlayer(String _UUID, String _name)
    {
        this.UUID = _UUID;
        this.name = _name;
    }

    public String getName() { return name; }
    public String getUUID() { return UUID; }
}
