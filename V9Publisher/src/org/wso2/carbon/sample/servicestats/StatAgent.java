package org.wso2.carbon.sample.servicestats;

import org.wso2.carbon.databridge.agent.thrift.AsyncDataPublisher;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.*;

import javax.security.sasl.AuthenticationException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class StatAgent {

    private static int sentEventCount = 0;
    public static final String STREAM_NAME1 = "org.wso2.log_analyzer.netflow.info1";
    public static final String VERSION1 = "1.0.0";

    static String host, port, username, passwd;
    static AsyncDataPublisher dataPublisher;
    static String streamId1 ;
    public static void setup(String host, String port, String username, String passwd) throws MalformedURLException, AgentException, org.wso2.carbon.databridge.commons.exception.AuthenticationException, TransportException, StreamDefinitionException, MalformedStreamDefinitionException, DifferentStreamDefinitionAlreadyDefinedException{

    	 System.out.println("Starting Statistics Agent");

         KeyStoreUtil.setTrustStoreParams();
    	
    	host = host;
        port = port;
        username = username;
        passwd = passwd;
        dataPublisher = new AsyncDataPublisher("tcp://" + "localhost" + ":" + "7611", username, passwd);
        
        //streamId1 = null;



        	 dataPublisher.addStreamDefinition("{" +
                    "  'name':'" + STREAM_NAME1 + "'," +
                    "  'version':'" + VERSION1 + "'," +
                    "  'nickName': 'Statistics'," +
                    "  'description': 'Service statistics'," +
                    "  'metaData':[" +
                    
                    "          {'name':'referer','type':'STRING'}" +
                    "  ]," +
                    "  'payloadData':[" +
                    "          {'name':'SrcIp','type':'STRING'}," +
                    "          {'name':'DestIp','type':'STRING'}," +
                    "          {'name':'size','type':'LONG'}," +
                    "          {'name':'timestamp','type':'STRING'}" +
                    "  ]" +
                    "}", STREAM_NAME1, VERSION1);
        	
       


    }


    public static void main(String[] args)
            throws AgentException, MalformedStreamDefinitionException,
                   StreamDefinitionException, DifferentStreamDefinitionAlreadyDefinedException,
                   MalformedURLException,
                   AuthenticationException, NoStreamDefinitionExistException,
                   org.wso2.carbon.databridge.commons.exception.AuthenticationException,
                   TransportException, SocketException {
        

        

        String streamId1 = null;



        //Publish event for a valid stream
        if (!streamId1.isEmpty()) {
            System.out.println("Stream ID: " + streamId1);


        }
    }

    public static void publishEvents(String SrcIp, String DestIp, long size, String timestamp)
            throws AgentException {

        	//System.out.println("Publish");
                Object[] meta = new Object[]{
                        "MetaData"
                };
//            if(SrcIp.startsWith("10.100")){
//            	System.out.println("-----------------------------------------------------------------------"+ SrcIp + "  " + size);
//            	
//            }
        	Object[] payload = new Object[]{
                        SrcIp,
                        DestIp,
                        size, 
                        timestamp
                        
                };

              Object[] correlation = null;


                dataPublisher.publish(STREAM_NAME1, VERSION1,meta, correlation, payload);
                
        
    }
    
public static String getSiteName(String ip){
		
		try{
			String[] numbers = ip.split("\\.");
			
			byte[] byteArray = new byte[]{(byte)Integer.parseInt(numbers[0]),(byte)Integer.parseInt(numbers[1]),(byte)Integer.parseInt(numbers[2]),(byte)Integer.parseInt(numbers[3])};
			InetAddress host = InetAddress.getByAddress(byteArray);
			return host.getCanonicalHostName();
			
		}catch(UnknownHostException e){
			return ip;
		}
	
	}

}
     
