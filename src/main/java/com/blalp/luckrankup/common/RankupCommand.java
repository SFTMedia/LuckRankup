package com.blalp.luckrankup.common;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;

public class RankupCommand {
    public static String doRankUp(String backendName, String frontendName, String trackName, String uniqueId,
            LuckPerms luckPerms) {
        Track track = luckPerms.getTrackManager().getTrack(trackName);
        // https://github.com/lucko/LuckPerms/wiki/Developer-API:-Usage#checking-if-a-player-is-in-a-group
        User user = luckPerms.getUserManager().getUser(uniqueId);
        luckPerms.getUserManager().loadUser(UUID.fromString(uniqueId));
        List<String> groupsOnTrack = user.getNodes().stream().filter(NodeType.INHERITANCE::matches)
                .map(NodeType.INHERITANCE::cast).map(InheritanceNode::getGroupName)
                .filter((o) -> track.containsGroup(o))
                .sorted((o1, o2) -> Integer.compare(track.getGroups().indexOf(o1), (track.getGroups().indexOf(o2))))
                .collect(Collectors.toList());
        if (groupsOnTrack.size() > 0) {
            try {
                int hours = Util.hours(luckPerms.getUserManager().getUser(uniqueId).getFriendlyName(), backendName,
                        frontendName);
                int neededHours = Integer.parseInt(luckPerms.getGroupManager()
                        .getGroup(track.getNext(luckPerms.getGroupManager().getGroup(groupsOnTrack.get(0))))
                        .getCachedData()
                        .getMetaData(luckPerms.getContextManager()
                                .getQueryOptions(luckPerms.getUserManager().getUser(uniqueId)).get())
                        .getMetaValue("hoursNeeded"));
                if (hours >= neededHours) {
                    track.promote(user, luckPerms.getContextManager().getStaticContext());
                    return "&1Promoted!";

                } else {
                    return "&1Insuffient Hours!";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Err.";
        } else {
            return "&4Woah there tiger, you don't have any ranks on the track " + trackName;
        }
    }
}