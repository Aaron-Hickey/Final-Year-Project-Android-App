package com.example.hal9000.communitywatch;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

public class GetUserFromFile {

    private Gson gson;
    private FileReaderObject fro;
    private Context context;
    private final String fileName = "Logged_In_User";


    public GetUserFromFile(Context context){
        gson = new Gson();
        fro = new FileReaderObject();
        this.context = context;
    }

    public UserObject getUser()
    {
        gson = new Gson();
        fro = new FileReaderObject();

        try {
            String fileContents = fro.readFromFile(context, fileName);
            System.out.println("File Contents: "+fileContents);
            UserObject uo = gson.fromJson(fileContents, UserObject.class);
            return uo;
        }
        catch (Exception e)
        {
            System.out.println("No Logged In User");
            e.printStackTrace();
            return null;
        }
    }
}
