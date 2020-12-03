package pasta;

import org.bukkit.Chunk;

import java.util.ArrayList;

public class City {




     public class ChunkHashTable{

         ArrayList[] harray = new ArrayList[500];

         public ChunkHashTable(){
             for (int i = 0; i < 500; i++) {

                 harray[i] = new ArrayList<Chunk>();
             }

         }

         /**
          * Insert Chunk c into hashtable based on key.
          * @param c Chunk to insert.
          */
         public void insert(Chunk c){

             int key = hashFunction(c);

             harray[key].add(c);

         }

         /**
          * Removes Chunk from hash table
          * @param c Chunk to be removed
          */
         public void remove(Chunk c){

             harray[hashFunction(c)].remove(c);

         }

         /**
          * Returns value of key for Chunk c.
          * Key is calculated by adding the Chunks z and x coords and modding by 500 (size of hash table).
          * @param c chunk to find key for.
          * @return key of Chunk c.
          */
         public int hashFunction(Chunk c){

             int key = (c.getX() + c.getZ()) % 500;

             return key;
         }

    }
}
