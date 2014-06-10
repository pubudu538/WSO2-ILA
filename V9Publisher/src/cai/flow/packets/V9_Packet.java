/**
 *
 */
package cai.flow.packets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;



import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.sample.servicestats.StatAgent;

import cai.flow.packets.v9.OptionFlow;
import cai.flow.packets.v9.OptionTemplate;
import cai.flow.packets.v9.OptionTemplateManager;
import cai.flow.packets.v9.Template;
import cai.flow.packets.v9.TemplateManager;
import cai.flow.struct.Scheme_DataPrefix;
import cai.sql.SQL;
import cai.utils.*;

/**
 * @author CaiMao V9 Flow Packet UDP��Ľ������ڲ�����FlowSet DataSet��������
 *
 * -------*---------------*------------------------------------------------------* |
 * Bytes | Contents | Description |
 * -------*---------------*------------------------------------------------------* |
 * 0-1 | version | The version of NetFlow records exported 009 |
 * -------*---------------*------------------------------------------------------* |
 * 2-3 | count | Number of flows exported in this packet (1-30) |
 * -------*---------------*------------------------------------------------------* |
 * 4-7 | SysUptime | Current time in milliseconds since the export device | | | |
 * booted |
 * -------*---------------*------------------------------------------------------* |
 * 8-11 | unix_secs | Current count of seconds since 0000 UTC 1970 |
 * -------*---------------*------------------------------------------------------* |
 * 12-15 |PackageSequence| pk id of all flows |
 * -------*---------------*------------------------------------------------------* |
 * 16-19 | Source ID | engine type+engine id |
 * -------*---------------*------------------------------------------------------* |
 * 20- | others | Unused (zero) bytes |
 * -------*---------------*------------------------------------------------------*
 *
 */
public class V9_Packet implements FlowPacket {
    long count;

    String routerIP;

    long SysUptime, unix_secs, packageSequence;

    long sourceId;

    Vector flows = null;

    Vector optionFlows = null;

    public static final int V9_Header_Size = 20;
    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            Runnable run = new Runnable() {
//                public void run() {
                    new V9_Packet("127.0.0.0", new byte[] {}, 0);
//                }
//            };
//            Thread t1 = new Thread(run);
//            Thread t2 = new Thread(run);
//            Thread t3 = new Thread(run);
//            t1.start();
//            t2.start();
//            t3.start();
//        }
    }

    /**
     * ����UDP��ͷ�������е�flows�����洢���ڴ�Vector��
     *
     * @param RouterIP
     * @param buf
     * @param len
     * @throws DoneException
     */
    public V9_Packet(String RouterIP, byte[] buf, int len) throws DoneException {
//    	for (int i = 0; i < len; i++) {
//	        System.out.println(buf[i]);
//        }
//    	System.exit(0);
        if (Params.DEBUG) {
            // ����ʵ��
//            File tmpFile = new File("D:\\Dev\\netflow\\jnca\\savePacketT_211.98.0.147_256.cache.tmp");
            File tmpFile = new File(
                    "D:\\Dev\\netflow\\jnca\\savePacketT_211.98.0.147_256.cache.tmp");
            if (tmpFile.exists()) {
                try {
                    ObjectInputStream fIn = new ObjectInputStream(
                            new FileInputStream(tmpFile));
                    System.out.println("Directly read from " + fIn);
                    try {
                        buf = (byte[]) fIn.readObject();
                        len = ((Integer) fIn.readObject()).intValue();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    fIn.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    ObjectOutputStream fOut;
                    fOut = new ObjectOutputStream(new FileOutputStream(tmpFile));
                    fOut.writeObject(buf);
                    fOut.writeObject(new Integer(len));
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            // ����ʵ��
        }
        //System.out.println("in--");
        if (len < V9_Header_Size) {
            throw new DoneException("    * incomplete header *");
        }
        //System.out.println("in");
        this.routerIP = RouterIP;
        //System.out.println(RouterIP);
        count = Util.to_number(buf, 2, 2); // ����template data flowset��Ŀ

        SysUptime = Util.to_number(buf, 4, 4);
        Variation vrat = Variation.getInstance();
        vrat.setVary(Util.convertIPS2Long(RouterIP), SysUptime);
        unix_secs = Util.to_number(buf, 8, 4);
        packageSequence = Util.to_number(buf, 12, 4);
        sourceId = Util.to_number(buf, 16, 4);

        flows = new Vector((int) count * 30); // Let's first make some space
        optionFlows = new Vector();
        // ����
        long flowsetLength = 0l;
        // ����flowset��ѭ��
        for (int flowsetCounter = 0, packetOffset = V9_Header_Size;
                flowsetCounter < count
                && packetOffset < len; flowsetCounter++,
                packetOffset += flowsetLength) {
            // ����flowset�ڲ�
            long flowsetId = Util.to_number(buf, packetOffset, 2);
            flowsetLength = Util.to_number(buf, packetOffset + 2, 2);
            if (flowsetLength == 0) {
                throw new DoneException(
                        "there is a flowset len=0��packet invalid");
            }
            if (flowsetId == 0) {
                // template flowset������templateid��һ�����ݣ�����data flowset
                // ����template flowset
                int thisTemplateOffset = packetOffset + 4;
                do {
                    // ����һ��template
                    long templateId = Util
                                      .to_number(buf, thisTemplateOffset, 2);
                    long fieldCount = Util.to_number(buf,
                            thisTemplateOffset + 2, 2);
                    if (TemplateManager.getTemplateManager().getTemplate(
                            this.routerIP, (int) templateId) == null
                        || Params.v9TemplateOverwrite) {
                        try {
                            TemplateManager.getTemplateManager().acceptTemplate(
                                    this.routerIP, buf, thisTemplateOffset);
                        } catch (Exception e) {
                            if (Params.DEBUG) {
                                e.printStackTrace();
                            }
                            if ((e.toString() != null)
                                && (!e.toString().equals(""))) {
                                if (e.toString().startsWith("savePacket")) {
                                    try {
                                        ObjectOutputStream fOut;
                                        fOut = new ObjectOutputStream(new
                                                FileOutputStream("./" +
                                                e.toString() + ".cache.tmp"));
                                        fOut.writeObject(buf);
                                        fOut.writeObject(new Integer(len));
                                        fOut.flush();
                                        fOut.close();
                                        System.err.println("Saved ");
                                    } catch (FileNotFoundException e2) {
                                        e2.printStackTrace();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    System.err.println("An Error without save:" +
                                            e.toString());
                                }
                            }
                        }

                    }
                    thisTemplateOffset += fieldCount * 4 + 4; //�����ƺ�������
                } while (thisTemplateOffset - packetOffset < flowsetLength);
            } else if (flowsetId == 1) { // options flowset
                continue;
//                int thisOptionTemplateOffset = packetOffset + 4;
//                // bypass flowsetID and flowset length
//                do {
//                    // ����һ��template
//                    long optionTemplateId = Util.to_number(buf,
//                            thisOptionTemplateOffset, 2);
//                    long scopeLen = Util.to_number(buf,
//                            thisOptionTemplateOffset + 2, 2);
//                    long optionLen = Util.to_number(buf,
//                            thisOptionTemplateOffset + 4, 2);
//                    if (OptionTemplateManager.getOptionTemplateManager()
//                        .getOptionTemplate(this.routerIP,
//                                           (int) optionTemplateId) == null
//                        || Params.v9TemplateOverwrite) {
//                        OptionTemplateManager.getOptionTemplateManager()
//                                .acceptOptionTemplate(this.routerIP, buf,
//                                thisOptionTemplateOffset);
//                    }
//                    thisOptionTemplateOffset += scopeLen + optionLen + 6;
//                } while (thisOptionTemplateOffset -
//                         packetOffset < flowsetLength);
            } 
            
            
            
            else if (flowsetId > 255) {
                // data flowset
                // templateId==flowsetId
                Template tOfData = TemplateManager.getTemplateManager()
                                   .getTemplate(this.routerIP, (int) flowsetId); // flowsetId==templateId
                //System.out.println(flowsetId + "------    "); 
                //System.out.println("00");
                if (tOfData != null) {
                	//System.out.println("template");
                    int dataRecordLen = tOfData.getTypeOffset( -1); // ÿ������¼�ĳ���
                    // packetOffset+4 �ó�flowsetId �� length�ռ�
                    for (int idx = 0, p = packetOffset + 4;
                                          (p - packetOffset + dataRecordLen) <
                                          flowsetLength; //consider padding
                                          idx++, p += dataRecordLen) { //+5 makes OK
                        
                        V5_Flow f=null;
                        try {
                        	 
                            f = new V5_Flow(RouterIP, buf, p, tOfData);
                            
                            if(flowsetId==263){
                            try {
                      	    if(f.getSrcAddr().toString().startsWith("10.100") && Math.round(f.size/1024) > 0){	  
                      	    StatAgent.publishEvents(f.getSrcAddr().toString(), f.getDstAddr().toString(), Math.round(f.size/1024), getCurrentTimeStamp());
                      	}	} catch (AgentException e) {
                      	    
                      	    e.printStackTrace();
                          }
                            }
                            
                            //if(f.getSrcAddr().toString().equals("10.100.4.35")){
                            System.out.println(f.getSrcAddr() + "  " + f.getDstAddr() + "  " + f.size);
                            
                            
                            flows.add(f); 
                        } catch (DoneException e) {
                        	//f.getSrcAddr();
                            if (Params.DEBUG) {
                                e.printStackTrace();
                            }
                            if ((e.toString() != null)
                                && (!e.toString().equals(""))) {
                                if (e.toString().startsWith("savePacket")) {
                                    try {
                                        ObjectOutputStream fOut;
                                        fOut = new ObjectOutputStream(new
                                                FileOutputStream("./" +
                                                e.toString() + ".cache.tmp"));
                                        fOut.writeObject(buf);
                                        fOut.writeObject(new Integer(len));
                                        fOut.flush();
                                        fOut.close();
                                        System.err.println("Saved ");
                                    } catch (FileNotFoundException e2) {
                                        e2.printStackTrace();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    System.err.println(e.toString());
                                }
                            }
                        }
                    }
                } 
                
                
                
                
                else { //options packet, should refer to option template, not in use now
                    continue;
//                    OptionTemplate otOfData = OptionTemplateManager
//                                              .getOptionTemplateManager().
//                                              getOptionTemplate(
//                            this.routerIP, (int) flowsetId);
//                    if (otOfData != null) {
//                        int dataRecordLen = otOfData.getTypeOffset( -1); // ÿ������¼�ĳ���
//                        // packetOffset+4 �ó�flowsetId �� length�ռ�
//                        for (int idx = 0, p = packetOffset + 4; p
//                                              - packetOffset < flowsetLength;
//                                              idx++, p += dataRecordLen) {
//                            OptionFlow of;
//                            try {
//                                of = new OptionFlow(RouterIP, buf, p, otOfData);
////                                optionFlows.add(of); // ���뵽Vector�У����й鲢�Ϳ�����������
//                            } catch (DoneException e) {
//                                if (Params.DEBUG) {
//                                    e.printStackTrace();
//                                }
//                                System.err.println(e.toString());
//                            }
//                        }
//                    } else {
//                        System.err.println(this.routerIP + "��" + flowsetId
//                                           + "��һ������ʶ���template��");
//                    }
                }
            }
        }
    }

    protected static String add_raw_sql = null;
    
    public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}

    public void process_raw(SQL sql) {
        if (add_raw_sql == null) {
            add_raw_sql = SQL.resources.getAndTrim("SQL.Add.RawV9");
        }

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            ((V5_Flow) flowenum.nextElement()).save_raw4v9(SysUptime,
                    unix_secs, packageSequence, sourceId, sql.prepareStatement(
                            "Prepare INSERT to V9 raw table", add_raw_sql));
        }
        for (Enumeration oflowenum = optionFlows.elements(); oflowenum
                                     .hasMoreElements(); ) {
            ((OptionFlow) oflowenum.nextElement()).save_raw(SysUptime,
                    unix_secs, packageSequence, sourceId, sql.prepareStatement(
                            "Prepare INSERT to Option table", SQL.resources
                            .getAndTrim("SQL.Add.OptionsTable")));
        }
    }

    public Vector getSrcASVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataSrcAS());
        }

        return v;
    }

    public Vector getDstASVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataDstAS());
        }

        return v;
    }

    public Vector getASMatrixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataASMatrix());
        }

        return v;
    }

    public Vector getSrcNodeVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataSrcNode());
        }

        return v;
    }

    public Vector getDstNodeVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataDstNode());
        }

        return v;
    }

    public Vector getHostMatrixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataHostMatrix());
        }

        return v;
    }

    public Vector getSrcInterfaceVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataSrcInterface());
        }

        return v;
    }

    public Vector getDstInterfaceVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataDstInterface());
        }

        return v;
    }

    public Vector getInterfaceMatrixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataInterfaceMatrix());
        }

        return v;
    }

    public Vector getSrcPrefixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            Scheme_DataPrefix pfx = ((V5_Flow) flowenum.nextElement())
                                    .getDataSrcPrefix();
            if (pfx != null) {
                v.add(pfx);
            }
        }

        return v;
    }

    public Vector getDstPrefixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            Scheme_DataPrefix dpfx = ((V5_Flow) flowenum.nextElement())
                                     .getDataDstPrefix();
            if (dpfx != null) {
                v.add(dpfx);
            }
        }
        return v;
    }

    public Vector getPrefixMatrixVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataPrefixMatrix());
        }

        return v;
    }

    public Vector getProtocolVector() {
        Vector v = new Vector((int) count, (int) count);

        for (Enumeration flowenum = flows.elements(); flowenum
                                    .hasMoreElements(); ) {
            v.add(((V5_Flow) flowenum.nextElement()).getDataProtocol());
        }

        return v;
    }
}
