package cai.flow.collector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import cai.flow.collector.interpretator.IpSegmentManager;
import cai.flow.packets.FlowPacket;
import cai.flow.packets.V1_Packet;
import cai.flow.packets.V5_Packet;
import cai.flow.packets.V7_Packet;
import cai.flow.packets.V8_Packet;
import cai.flow.packets.V9_Packet;
import cai.utils.DoneException;
import cai.utils.Params;
import cai.utils.Resources;
import cai.utils.ServiceThread;
import cai.utils.SuperString;
import cai.utils.Syslog;
import cai.utils.Util;

class Collector {

	static Resources resources;

	static InetAddress localHost;

	static int localPort;

	static int receiveBufferSize;

	static boolean[] isVersionEnabled;

	static int max_queue_length;

	static int collector_thread;

	static final int MAX_VERION = 9;

	static Hashtable routers;

	static {
		resources = new Resources("NetFlow");
		//IpSegmentManager.getInstance();
		receiveBufferSize = resources.integer("net.receive.buffer.size");
		localPort = resources.integer("net.bind.port");

		String local = resources.get("net.bind.host");
		Params.v9TemplateOverwrite = resources
				.isTrue("flow.collector.V9.template.overwrite");

		Params.template_refreshFromHD = resources
				.isTrue("flow.collector.template.refreshFromHD");
		Params.ip2ipsConvert=resources.isTrue("flow.ip2ipsConvert");
		String ipSrcEx = resources.getAndTrim("ip.source.excludes");
		String ipSrcIn = resources.getAndTrim("ip.source.includes");
		StringTokenizer tknz = new StringTokenizer(ipSrcEx,",");
		Params.ipSrcExcludes=new long[tknz.countTokens()];
		int idxOfEx = 0;
		while(tknz.hasMoreElements()){
			long tmpl=Util.convertIPS2Long(tknz.nextToken());
			Params.ipSrcExcludes[idxOfEx++]=tmpl;//×¢Òâ,·Ç·šµØÖ·»áœ«ËùÓÐµØÖ·Žò¿ª--0.0.0.0
		}
		tknz=new StringTokenizer(ipSrcIn,",");
		Params.ipSrcIncludes=new long[tknz.countTokens()];
		int idxOfIn = 0;
		while(tknz.hasMoreElements()){
			long tmpl=Util.convertIPS2Long(tknz.nextToken());
			Params.ipSrcIncludes[idxOfIn++]=tmpl;//×¢Òâ,·Ç·šµØÖ·»áœ«ËùÓÐµØÖ·Žò¿ª--0.0.0.0
		}

		String ipDstEx = resources.getAndTrim("ip.dst.excludes");
		String ipDstIn = resources.getAndTrim("ip.dst.includes");
		tknz = new StringTokenizer(ipDstEx,",");
		Params.ipDstExcludes=new long[tknz.countTokens()];
		idxOfEx = 0;
		while(tknz.hasMoreElements()){
			long tmpl=Util.convertIPS2Long(tknz.nextToken());
			Params.ipDstExcludes[idxOfEx++]=tmpl;//×¢Òâ,·Ç·šµØÖ·»áœ«ËùÓÐµØÖ·Žò¿ª--0.0.0.0
		}
		tknz=new StringTokenizer(ipDstIn,",");
		Params.ipDstIncludes=new long[tknz.countTokens()];
		idxOfIn = 0;
		while(tknz.hasMoreElements()){
			long tmpl=Util.convertIPS2Long(tknz.nextToken());
			Params.ipDstIncludes[idxOfIn++]=tmpl;//×¢Òâ,·Ç·šµØÖ·»áœ«ËùÓÐµØÖ·Žò¿ª--0.0.0.0
		}

		if (local.equals("any"))
			localHost = null;
		else {
			try {
				localHost = InetAddress.getByName(local);
			} catch (UnknownHostException e) {
				localHost = null;
			}

			if (localHost == null)
				resources.error("unknown host `" + local + "'");
		}

		isVersionEnabled = new boolean[MAX_VERION];
		isVersionEnabled[0] = resources.isTrue("flow.collector.V1.enabled");
		isVersionEnabled[1] = false;
		isVersionEnabled[2] = false;
		isVersionEnabled[3] = false;
		isVersionEnabled[4] = resources.isTrue("flow.collector.V5.enabled");
		isVersionEnabled[5] = false;
		isVersionEnabled[6] = resources.isTrue("flow.collector.V7.enabled");
		isVersionEnabled[7] = resources.isTrue("flow.collector.V8.enabled");
		isVersionEnabled[8] = resources.isTrue("flow.collector.V9.enabled");

		max_queue_length = resources.integer("flow.collector.max_queue_length");
		collector_thread = resources.integer("flow.collector.collector.thread");

		if (collector_thread < 1)
			resources.error("key `" + collector_thread + "' bust be great one");

		routers = new Hashtable();

		ResourceBundle bundle = resources.getResourceBundle();
		String prefix = "flow.collector.router.group.";
		int prefix_len = prefix.length();

		for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
			String entry = (String) e.nextElement();

			if (!entry.startsWith(prefix))
				continue;

			InetAddress router_group = null;
			boolean putted = false;

			try {
				router_group = InetAddress.getByName(entry
						.substring(prefix_len));
			} catch (UnknownHostException e1) {
				resources.error("unknown host `" + entry.substring(prefix_len)
						+ "' in `" + entry + "'");
			}

			String the_routers = (String) bundle.getString(entry);

			for (StringTokenizer st = new StringTokenizer(the_routers); st
					.hasMoreElements();) {
				String router_name = st.nextToken();
				InetAddress router = null;

				try {
					router = InetAddress.getByName(router_name);
				} catch (UnknownHostException e2) {
					resources.error("unknown host `" + router_name + "' in `"
							+ entry + "'");
				}

				routers.put(router, router_group);
				putted = true;
			}

			if (!putted)
				resources.error("key `" + the_routers
						+ "' -- no routers in group");
		}
	}

	Syslog syslog;

	LinkedList data_queue;

	Aggregate aggregator;

	long queued = 0, processed = 0;

	int sampleRate = 1;

	int stat_interval;

	public Collector() {
		sampleRate = resources.integer("sample.rate");
		if (sampleRate == 0) {
			sampleRate = 1;
		}
		byte logLevel = Syslog.translatePriority(resources
				.get("flow.collector.syslog.level"));
		byte logOptions = Syslog.translateOptions(resources
				.get("flow.collector.syslog.options"));
		short logFacility = Syslog.translateFacility(resources
				.get("flow.collector.syslog.facility"));

		stat_interval = resources
				.getInterval("flow.collector.statistics.interval");

		if (logLevel == Syslog.LOG_ILLEGAL_P)
			resources.error("illegal flow.collector.syslog.level value");

		if (logOptions == Syslog.LOG_ILLEGAL_O)
			resources.error("illegal flow.collector.syslog.options value");

		if (logFacility == Syslog.LOG_ILLEGAL_F)
			resources.error("illegal flow.collector.syslog.facility value");

		syslog = new Syslog("NetFlow", logOptions, logFacility);
		syslog.setlogmask(Syslog.LOG_UPTO(logLevel));
		syslog.syslog(Syslog.LOG_DEBUG, "Syslog created: " + syslog.toString());

		aggregator = new Aggregate(resources);// ËùÓÐµÄ¹é²¢Ïß³ÌºÍSQL
		data_queue = new LinkedList();
	}

	/**
	 * ŽŽºÃ¿âÒÔºó£¬Ö÷Òª¹€×÷ÔÚÕâÀï
	 *
	 */
	void go() {
		/**
		 * ×îžßÓÅÏÈŒ¶£¬¶ÁUDP°üÏß³Ì
		 */
		ServiceThread rdr = new ServiceThread(this, syslog, "Reader at "
				+ (localHost == null ? "any" : "" + localHost) + ":"
				+ localPort, "Reader") {
			public void exec() throws Throwable {
				((Collector) o).reader_loop();
			}
		};
		rdr.setPriority(Thread.MAX_PRIORITY);
		rdr.setDaemon(true);
		rdr.start();

		ServiceThread statistics;
		/**
		 * Í³ŒÆÏß³Ìœöœö×öÍ³ŒÆºÍlog
		 */
		if (stat_interval != 0 && syslog.need(Syslog.LOG_NOTICE)) {
			statistics = new ServiceThread(this, syslog, "Statistics over "
					+ Util.toInterval(stat_interval), "Statistics") {
				public void exec() throws Throwable {
					((Collector) o).statistics_loop();
				}
			};

			statistics.setDaemon(true);
			statistics.start();
		}

		ServiceThread[] cols = new ServiceThread[collector_thread];

		for (int i = 0; i < collector_thread; i++) {
			String title = new String("Collector #" + (i + 1));
			ServiceThread col = new ServiceThread(this, syslog, title, title) {
				public void exec() {
					((Collector) o).collector_loop();
				}
			};

			cols[i] = col;
			col.start();
		}

		try {
			for (int i = 0; i < collector_thread; i++)
				cols[i].join();
		} catch (InterruptedException e) {
			syslog.syslog(Syslog.LOG_CRIT,
					"Collector - InterruptedException in main thread, exit");
		}
	}

	/**
	 * Í³ŒÆÏß³ÌµÄÖ÷·œ·š
	 *
	 * @throws Throwable
	 */
	public void statistics_loop() throws Throwable {
		long start = System.currentTimeMillis();

		while (true) {
			try {
				Thread.sleep(stat_interval * 1000);
			} catch (InterruptedException e) {
			}

			long u = System.currentTimeMillis() - start;
			String s = "" + ((float) queued * 1000 / u);
			int i = s.indexOf('.') + 3;

			if (i < s.length())
				s = s.substring(0, i);

			syslog.syslog(Syslog.LOG_NOTICE, "Pkts " + queued + "/" + processed
					+ ", " + s + " pkts/sec, " + Util.uptime_short(u / 1000));
		}
	}

	// œöœö×öÊµÑé
	// static DatagramPacket tmpPacket = null;
	// œöœö×öÊµÑé
	SampleManager sampler = null;
	{
		sampler = new SampleManager(sampleRate);
	}

	/**
	 * ¶ÁÈ¡UDP°ü
	 *
	 * @throws Throwable
	 */
	public void reader_loop() throws Throwable {
		DatagramSocket socket=null;

		try {
			try {
				socket = new DatagramSocket(9996, InetAddress.getByName("0.0.0.0"));
				socket.setReceiveBufferSize(receiveBufferSize);
			} catch (IOException exc) {
				syslog.syslog(Syslog.LOG_CRIT, "Reader - socket create error: "
						+ localHost + " - "
						+ SuperString.exceptionMsg(exc.toString()));
				throw exc;
			}

			while (true) {
				byte[] buf = new byte[2048];// Ð§ÂÊÔÚÕâžöµØ·œ¿ÉÒÔÌážß
				DatagramPacket p = null;
				// œöœö×öÊµÑé
				// if (tmpPacket!=null) {
				// System.out.println("Ö±œÓŽÓ"+tmpPacket+"ÖÐÈ¡ÊýŸÝ");
				// p = tmpPacket;
				// }
				// œöœö×öÊµÑé
				if (p == null) {
					p = new DatagramPacket(buf, buf.length);

					try {
						socket.receive(p);
					} catch (IOException exc) {
						syslog.syslog(Syslog.LOG_CRIT,
								"Reader - socket read error: "
										+ SuperString.exceptionMsg(exc
												.toString()));
						exc.printStackTrace();
						put_to_queue(null);// ±íÊŸnotifyAll
						break;
					}
				}
				// œöœö×öÊµÑé
				// if (tmpPacket==null) {
				// tmpPacket=p;
				// }
				// Thread.sleep(1000);
				// œöœö×öÊµÑé
				if (this.sampler.shouldDue()) {
					put_to_queue(p);
				}
				p = null;
			}
		} catch (Throwable e) {
			syslog.syslog(Syslog.LOG_CRIT,
					"Reader: exception, trying to abort collector");
			e.printStackTrace();
			put_to_queue(null);
			throw e;
		}
	}

	/**
	 * UDP°üµÄ»ºŽæ
	 *
	 * @param p
	 */
	void put_to_queue(final DatagramPacket p) {
		InetAddress router = p.getAddress();
		InetAddress group = (InetAddress) routers.get(router);

//		if (group == null) {
//			syslog.syslog(Syslog.LOG_ERR,
//					"A packet from an unauthorized router " + router
//							+ " is ignored");
//			return;
//		}

		syslog.syslog(Syslog.LOG_DEBUG, "Packet from " + router
				+ " is moved to group " + group);
		p.setAddress(group);// °ÑÕæÊµrouterµÄµØÖ·žÄ³ÉgroupµÄµØÖ·

		if (data_queue.size() > max_queue_length)
			syslog.syslog(Syslog.LOG_WARNING,
					"Reader - the queue is bigger then max_queue_length "
							+ data_queue.size() + "/" + max_queue_length);

		synchronized (data_queue) {
			data_queue.addLast(p);
			queued++;

			if (p == null)
				data_queue.notifyAll();// Èç¹û³öŽíÁË£¬ÄÇÃŽ
			else
				data_queue.notify();// »œÐÑ
		}
	}

	/**
	 * ÖÚ¶à²ÉŒ¯Ïß³ÌµÄÖ÷·œ·š
	 *
	 */
	void collector_loop() {
		boolean no_data = true;

		while (true) {
			Object p = null;

			synchronized (data_queue) {
				try {
					if (data_queue.getFirst() != null)
						p = data_queue.removeFirst();// È¡³öµÚÒ»žöUDP°ü

					no_data = false;
				} catch (NoSuchElementException ex) {
				}
			}

			if (no_data) {
				synchronized (data_queue) {
					try {
						data_queue.wait();// µÈŽý±»reader_loop notify
					} catch (InterruptedException e) {
					}
				}
			} else {
				no_data = true;

				if (p == null)// UDP³öÏÖÁËŽíÎó
					break;

				processPacket((DatagramPacket) p);
			}
		}
	}

	/**
	 * ŽŠÀíÒ»žöUDP°ü£¬ÓÉ¶àÏß³ÌÎÞ×ŽÌ¬µÄ²¢·¢
	 *
	 * @param p
	 */
	private synchronized void processPacket(final DatagramPacket p) {
		final byte[] buf = p.getData();
		int len = p.getLength();
		//System.out.println(p.getLength());
		String addr = "10.100.32.254";
//                p.getAddress().getAddress();
		boolean need_log = syslog.need(Syslog.LOG_INFO);

		synchronized (data_queue) {
			processed++;
		}

		if (need_log)
			syslog.syslog(Syslog.LOG_INFO, addr + "("
					+ p.getAddress().getHostName() + ") " + len + " bytes");

		try {
			if (len < 2)
				throw new DoneException("  * too short packet *");

			short version = (short) Util.to_number(buf, 0, 2);

			if (version > MAX_VERION || version <= 0)
				throw new DoneException("  * unsupported version *");

			if (!isVersionEnabled[version - 1])
				throw new DoneException("  * version " + version
						+ " disabled *");

			if (need_log)
				syslog.syslog(Syslog.LOG_INFO, "  version: " + version);

			FlowPacket packet;

			switch (version) {
			case 1:
				packet = (FlowPacket) new V1_Packet(addr, buf, len);
				break;
			case 5:
				packet = (FlowPacket) new V5_Packet(addr, buf, len);
				break;
			case 7:
				packet = (FlowPacket) new V7_Packet(addr, resources, buf, len);
				break;
			case 8:
				packet = (FlowPacket) new V8_Packet(addr, buf, len);
				break;
			case 9:
				packet = (FlowPacket) new V9_Packet(addr, buf, len);
				//System.out.println("V9 ");
				break;
			default:
				syslog.syslog(Syslog.LOG_CRIT,
						"Collector - BUG: Version problem, version=" + version);
				return;
			}

			aggregator.process(packet);
		} catch (DoneException e) {
			e.printStackTrace();
			if (need_log)
				syslog.syslog(Syslog.LOG_INFO, e.toString());
		}
	}
}