package com.example.hal9000.communitywatch;

public class ServerAddressHandler {

    private static ServerAddressHandler sah;
    //private String ServerAddress= "http://192.168.43.203:9000/";
  //  private String ServerAddress= "http://172.17.122.154:9000/";
  //  private String ServerAddress= "http://192.168.137.1:9000/";
  //  private String ServerAddress= "http://192.168.137.1:8080/";

  private String ServerAddress= "http://communitywatchenvironment.fegyzrkrmw.eu-west-1.elasticbeanstalk.com/";



    public static ServerAddressHandler getInstance()
    {
        if(sah == null)
        {
            sah = new ServerAddressHandler();
        }
        return sah;
    }

    public ServerAddressHandler()
    {

    }

    public String getAddress()
    {
        return ServerAddress;
    }
}
