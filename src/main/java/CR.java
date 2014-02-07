import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;

/**
 * Created with IntelliJ IDEA.
 * User: freyr
 * Date: 12/9/13
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CR {

    public static void main(String[] args) throws Exception {

        String json = readUrl("http://stats.nba.com/js/data/sportvu/reboundingData.js").replace("var reboundingData = {\"parameters\":{},\"resource\":\"playertrackingrebounding\",\"resultSets\":[{\"name\":\"PlayerTrackingReboundingStats\",\"headers\":[\"PLAYER_ID\",\"PLAYER\",\"FIRST_NAME\",\"LAST_NAME\",\"TEAM_ABBREVIATION\",\"GP\",\"MIN\",\"REB\",\"REB_CHANCE\",\"REB_COL_PCT\",\"REB_CONTESTED\",\"REB_UNCONTESTED\",\"REB_UNCONTESTED_PCT\",\"REB_TOT\",\"OREB\",\"OREB_CHANCE\",\"OREB_COL_PCT\",\"OREB_CONTESTED\",\"OREB_UNCONTESTED\",\"OREB_UNCONTESTED_PCT\",\"DREB\",\"DREB_CHANCE\",\"DREB_COL_PCT\",\"DREB_CONTESTED\",\"DREB_UNCONTESTED\",\"DREB_UNCONTESTED_PCT\"],\"rowSet\":[[", "");
        json = json.replace("]]}]};", "");
        List<player> playerList = toListOfPlayers(json);
        Collections.sort(playerList);
        DecimalFormat df = new DecimalFormat("#.#");
        int counter = 1;
        for (player p : playerList){
            System.out.println(counter + "\t" + p.name.replace("\"", "") + "\t" + df.format(p.percentage*100));
            counter++;
        }
    }

    private static List<player> toListOfPlayers(String s){
        String[] array = s.split("\\],\\[");
        List<player> playerList = new ArrayList<player>();
        for(String a : array){
            if(isValid(a))
                playerList.add(createPlayerFromRaw(a));
        }


        return playerList;
    }

    private static boolean isValid(String s){
        String[] array = s.split(",");
        Double games = Double.parseDouble(array[5]);
        Double minutes = Double.parseDouble(array[6]);
        return games > 5 && minutes > 10;
    }

    private static player createPlayerFromRaw(String s){
        String[] array = s.split(",");

        return new player(array[1],  (Double.parseDouble(array[10])/(Double.parseDouble(array[8]) - Double.parseDouble(array[11]))));
    }

    private static class player implements Comparable<player>{
        String name;
        double percentage;

        private player(String name, double percentage) {
            this.name = name;
            this.percentage = percentage;
        }

        public int compareTo(player comparePlayer) {

            if(Double.isNaN(comparePlayer.percentage)){
                return -1;
            }

            if(Double.isNaN(this.percentage)){
                return 1;
            }

            if(comparePlayer.percentage > this.percentage)
                return 1;
            else
                return -1;

        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}

