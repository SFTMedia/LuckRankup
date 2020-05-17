package com.blalp.luckrankup.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Util {

    public static int hours(String player, String backedName, String frontendName)
            throws NumberFormatException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(
                ("http://www.superfuntime.org/api/mc/pstats2/pstats.php?info1=info&info2=player&info3=" + backedName)
                        .replace("player", player))).openStream()));

        String line;
        do {
            if ((line = reader.readLine()) == null) {
                return 0;
            }

            line = line.replace("\u00a7c", "").replace("\u00a7e", "").replace("\u00c2", "");
        } while (!line.contains(frontendName + " uptime"));

        line = line.split("/")[1];
        return Integer.parseInt(line.split("h")[0].replace(" ", ""));
    }
}