package com.tima.upnpdemo;

import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("MessageHandler"),
        serviceType = @UpnpServiceType(value = "MessageHandler", version = 1)
)
public class MessageHandler {
    
    @UpnpStateVariable(defaultValue = "")
    private String msg = "";
    
    @UpnpStateVariable(defaultValue = "")
    private String from = "";

    @UpnpAction(out = @UpnpOutputArgument(name = "Ret"))
    public String setMsg(@UpnpInputArgument(name = "Msg") String newMsg, 
    		@UpnpInputArgument(name = "From") String from) {
    	this.msg = newMsg; 
    	this.from = from;
    	System.out.println("receive new msg:" + newMsg);
    	MainActivity.mInstance.messageReceived(newMsg,from);
    	return "Received";
    }

}
