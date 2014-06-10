import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import java.lang.String;

public class callRecordPublisher {

	private static final String HTTPD_LOG_STREAM = "log_analyzer.call_records_logs";
	private static final String VERSION = "1.0.0";
	private static final int MAX_LOGS = 1000;

	// private static final String SAMPLE_LOG_PATH =
	// System.getProperty("user.dir") + "/resources/dhcp-2013-12-01.log";

	public static void main(String[] args)
	                                      throws SocketException,
	                                      AgentException,
	                                      MalformedURLException,
	                                      AuthenticationException,
	                                      TransportException,
	                                      StreamDefinitionException,
	                                      MalformedStreamDefinitionException,
	                                      DifferentStreamDefinitionAlreadyDefinedException,
	                                      FileNotFoundException,
	                                      UnknownHostException,
	                                      org.wso2.carbon.databridge.commons.exception.AuthenticationException {
		System.out.println("Chamila");
		
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		

		String currentDir = System.getProperty("user.dir");
		System.setProperty("javax.net.ssl.trustStore", currentDir +
		                                               "/src/main/client-truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
		Agent agent = new Agent(agentConfiguration);

		String url = getProperty("url", "tcp://" + "localhost" + ":" + "7612");
		String username = getProperty("username", "admin");
		String password = getProperty("password", "admin");
                System.out.println("Publisher Started");
		DataPublisher dataPublisher = new DataPublisher(url, username, password, agent);
		System.out.println("Finished");
                String streamId = null;

		try {
			streamId = dataPublisher.findStream("log_analyzer.call_records_logs", VERSION);
			System.out.println("Stream already defined");

		} catch (NoStreamDefinitionExistException e) {
			streamId =
			           dataPublisher.defineStream("{" +
			                                      "  'name':'" +
			                                      HTTPD_LOG_STREAM +
			                                      "'," +
			                                      "  'version':'" +
			                                      VERSION +
			                                      "'," +
			                                      "  'nickName': 'Call record'," +
			                                      "  'description': 'Sample '," +
			                                      "  'metaData':[" +
			                                      "          {'name':'clientType','type':'STRING'}" +
			                                      "  ]," +
			                                      "  'payloadData':[" +
			                                      "          {'name':'callingPartyNumber','type':'STRING'}," +
			                                      "          {'name':'originIp','type':'STRING'}," +
			                                      "          {'name':'CalledPartyNumber','type':'STRING'}," +
			                                      "          {'name':'destIp','type':'STRING'}," +
			                                      "          {'name':'duration','type':'STRING'}," +
			                                      "          {'name':'timestamp','type':'STRING'}" +
			                                      "  ]" + "}");
		}

		//System.out.println(System.getProperty("user.dir"));
		String path = "/home/wso2-ila-server/callrecords/";
		int k = 31091;
		String files;
		outer:
		while(true){
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				listOfFiles[i].getName();
				if (files.endsWith("" + k)) {
					Scanner scanner =
			                  new Scanner(
			                              new FileInputStream(
			                                                  "/home/wso2-ila-server/callrecords/" + files));	
					scanner.nextLine();
					scanner.nextLine();
					String aLog;
					String[] parts;
					Object[] meta;
					Object[] payload;
					while (scanner.hasNextLine()) {
					try{
						aLog = scanner.nextLine();
						parts = aLog.split(",");

						long seconds = Integer.parseInt(parts[47]);
						if (seconds == 0) {
							continue;
						}
						long millis = seconds * 1000;
						Date date = new Date(millis);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
						String formattedDate = sdf.format(date);
						System.out.println(parts[8].split("\"")[1] + "  " + parts[80].split("\"")[1] + "  " + parts[29].split("\"")[1]);
						meta = new Object[] { "external" };
						payload =
						          new Object[] { parts[8].split("\"")[1], parts[80].split("\"")[1],
						                        parts[29].split("\"")[1], parts[81].split("\"")[1], parts[55],
						                        formattedDate };
						Event event = new Event(streamId, System.currentTimeMillis(), meta, null, payload);
						dataPublisher.publish(event);
					    }catch(Exception e){
						e.printStackTrace();}
					}
					
					
					
					
					
					System.out.println(files);
					System.out.println();
					/*if(listOfFiles[i].delete()){
    						System.out.println(files + " is deleted!");
    					}else{
    						System.out.println("Delete operation is failed.");
    					}*/
					k++;
					continue outer;
				}
			}
		}
		
		System.out.println("No File");
		
		try {
	        Thread.sleep(120000);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		}

		
		

		// Object[] meta = new Object[]{
		// "external"
		// };
		//
		// Object[] payload = new Object[]{
		// "0777777777",
		// "071717171",
		// "500",
		// "2014"
		//
		// };
		// Event event = new Event(streamId, System.currentTimeMillis(), meta,
		// null,
		// payload);
		// System.out.println(event);
		// dataPublisher.publish(event);
		// System.out.println("published");
		//dataPublisher.stop();

	}

	private static String getProperty(String name, String def) {
		String result = System.getProperty(name);
		if (result == null || result.length() == 0 || result == "") {
			result = def;
		}
		return result;
	}

}
