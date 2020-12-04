package pasta;

import org.bukkit.entity.Player;

public class Nation
{
    private Player leader;
    private String name;

    public Nation(Player _leader, String _name)
    {
        this.leader = _leader;
        this.name = _name;
    }

    public String getName() { return name; }
    public void setName(String _name) { this.name = _name; }
    public Player getLeader() { return this.leader; }
    public void setLeader(Player _leader) { this.leader = _leader; }
}
