package cai.flow.collector;

import java.io.UnsupportedEncodingException;

import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.sample.servicestats.StatAgent;

import cai.flow.packets.v9.TemplateManager;
import cai.sql.SQL;
import cai.utils.DoneException;
import cai.utils.Params;
import cai.utils.Syslog;

public class Run {
    static {
        try {
            Class.forName("cai.flow.collector.Collector");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void go(String args[]) {
        boolean run_collector = true;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("create_db")) {
                new SQL().create_DB();
                run_collector = false;
            } else if (args[i].equals("remove_db")) {
                new SQL().delete_DB();
                run_collector = false;
            } else if (args[i].startsWith("encoding=")) {
                Params.encoding = args[i].substring(9);

                try {
                    String test = "eeeeeee";
                    test.getBytes(Params.encoding);
                } catch (UnsupportedEncodingException e) {
                    System.err.println("RUN: Unsupported encoding: "
                                       + Params.encoding);
                    System.exit(0);
                }
            } else {
                System.err.println("RUN: Unknown argument -- " + args[i]);
                run_collector = false;
            }
        }

        if (run_collector) { 
            TemplateManager.getTemplateManager();
            new Collector().go();
        }
    }

    public static void main(String args[]) {
        
	 
    	try {
    	    StatAgent.setup("localhost", "7611", "admin", "admin");
    	    //StatAgent.publishEvents("Chamila", "Dilshan", 500, "chamila");
    	    
        }  catch (MalformedStreamDefinitionException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
        } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
        }
    	  catch (Exception e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    	    }
    	
    	try {
            go(args);
            System.out.println("Finished");
        } catch (DoneException e) {
            if (Syslog.log != null) {
                Syslog.log.print_exception(e);
            } else {
                System.err.println("Run error - " + e.toString());
            }
            System.out.println("Finished 1");
        } catch (Throwable e) {
            if (Syslog.log != null) {
                Syslog.log.print_exception(e);
            } else {
                throw e;
            }
            System.out.println("Finished 2 ");
        }
        System.out.println("Finished3");

    }
}
