package com.blalp.luckrankup.common;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.PromotionResult;
import net.luckperms.api.track.Track;

public class RankupCommand {
    public static String doRankUp(String backendName, String frontendName, String trackName, String uniqueId,
            LuckPerms luckPerms, boolean debug) {
        Track track = luckPerms.getTrackManager().getTrack(trackName);
        if (debug) {
            System.out.println("found track? Requested track: " + trackName + " the object: " + track);
        }
        // https://github.com/lucko/LuckPerms/wiki/Developer-API:-Usage#checking-if-a-player-is-in-a-group
        User user = luckPerms.getUserManager().getUser(UUID.fromString(uniqueId));
        if (debug) {
            System.out.println("found user? Requested user: " + uniqueId + " the object: " + user);
        }
        // luckPerms.getUserManager().loadUser(UUID.fromString(uniqueId)).join(); This
        // forces a db reset. Dont think it is needed
        if (debug) {
            System.out.println("found user nodes?  " + user.getNodes());
        }
        String output = "";
        boolean sucsess = true;
        while (sucsess) {
            sucsess = false;
            List<String> groupsOnTrack = user.getNodes().stream().filter(NodeType.INHERITANCE::matches)
                    .map(NodeType.INHERITANCE::cast).map(InheritanceNode::getGroupName)
                    .filter((o) -> track.containsGroup(o))
                    .sorted((o1, o2) -> Integer.compare(track.getGroups().indexOf(o2), (track.getGroups().indexOf(o1))))
                    .collect(Collectors.toList());
            if (debug) {
                System.out.println("found groups? Found groups: ");
                for (String rank : groupsOnTrack) {
                    System.out.println("rank: " + rank);
                }
            }
            if (groupsOnTrack.size() > 0) {
                if (debug) {
                    System.out.println("found a rank!");
                }
                try {
                    int hours = Util.hours(user.getFriendlyName(), backendName, frontendName);
                    String nextRank = track.getNext(luckPerms.getGroupManager().getGroup(groupsOnTrack.get(0)));
                    if (nextRank == null) {
                        if (output.equals("")) {
                            output = "§2 Promoted to " + groupsOnTrack.get(0) + " which is the max rank on the track "
                                    + trackName + " tell the server admins to turn up the difficulty to 11 next time!";
                        } else {
                            output = "§2 You are at the max rank in the track " + trackName;
                        }
                        sucsess = false;
                        break;
                    }
                    if (debug) {
                        System.out.println("hours: " + hours);
                        System.out.println("highest group: " + groupsOnTrack.get(0) + "obj "
                                + luckPerms.getGroupManager().getGroup(groupsOnTrack.get(0)));
                        System.out.println("next track: " + nextRank);
                        System.out.println(" obj " + luckPerms.getGroupManager().getGroup(nextRank));
                        System.out.println("highest group: " + groupsOnTrack.get(0));
                    }
                    int neededHours = Integer.parseInt(luckPerms.getGroupManager().loadGroup(nextRank).join().get()
                            .getCachedData().getMetaData(luckPerms.getContextManager().getQueryOptions(user).get())
                            .getMetaValue("hoursNeeded"));
                    if (debug) {
                        System.out.println("needed hours: " + neededHours);
                    }
                    if (hours >= neededHours) {
                        if (debug) {
                            System.out.println("enough hours?: " + ((hours >= neededHours) ? "yup" : "nop"));
                            System.out.println("context " + luckPerms.getContextManager().getStaticContext());
                        }
                        PromotionResult result = track.promote(user,
                                ImmutableContextSet.builder().add("server", luckPerms.getServerName()).build());
                        if (debug) {
                            System.out.println("result " + result);
                        }
                        luckPerms.getUserManager().saveUser(user).join();
                        luckPerms.getTrackManager().saveTrack(track).join();
                        output = "§2 Promoted to " + nextRank + " on " + luckPerms.getServerName();
                        sucsess = true;
                    } else {
                        if (output.equals("")) {
                            output = "§4 Insuffient hours, you need " + (neededHours - hours)
                                    + " more to be promoted to " + nextRank;
                        }
                        sucsess = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (debug) {
                    System.out.println("no ranks on track");
                }
                return "§4 Woah there tiger, you don't have any ranks on the track " + trackName;
            }
        }
        return "§f[§6LuckRankup§f]" + output;
    }
}