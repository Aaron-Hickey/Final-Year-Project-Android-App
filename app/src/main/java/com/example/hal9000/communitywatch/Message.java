package com.example.hal9000.communitywatch;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import static java.time.LocalTime.now;

public class Message {
    private String text; // message body
    private String name; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?
    private String timeOfMessageString;

    public Message(String text, String name, boolean belongsToCurrentUser, String timeOfMessageString) {
        this.text = text;
        this.name = name;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.timeOfMessageString = timeOfMessageString;


    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public String getTimeOfMessage()
    {
        return timeOfMessageString;
    }
}