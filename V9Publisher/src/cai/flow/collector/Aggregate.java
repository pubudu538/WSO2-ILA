package cai.flow.collector;

import java.util.Iterator;
import java.util.LinkedList;

import cai.flow.packets.FlowPacket;
import cai.sql.SQL;
import cai.utils.DoneException;
import cai.utils.Resources;

public class Aggregate extends LinkedList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9049626103334099526L;

	SQL sql;

	boolean save_raw_flow;
	/**
	 * ���еĹ鲢��������֯
	 * @param resources
	 * @throws DoneException
	 */
	public Aggregate(Resources resources) throws DoneException {
//		sql = new SQL();
//
//		save_raw_flow = resources
//				.isTrue("flow.collector.aggregate.raw.enabled");
//
//		long interval;
//		//�ȷ����������õĹ鲢�߳�
//		if ((interval = resources.getInterval("flow.collector.SrcAS.interval")) != 0)
//			add(new Scheme_AggregatorSrcAS(sql, interval));
//
//		if ((interval = resources.getInterval("flow.collector.DstAS.interval")) != 0)
//			add(new Scheme_AggregatorDstAS(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.ASMatrix.interval")) != 0)
//			add(new Scheme_AggregatorASMatrix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.SrcNode.interval")) != 0)
//			add(new Scheme_AggregatorSrcNode(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.DstNode.interval")) != 0)
//			add(new Scheme_AggregatorDstNode(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.HostMatrix.interval")) != 0)
//			add(new Scheme_AggregatorHostMatrix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.SrcInterface.interval")) != 0)
//			add(new Scheme_AggregatorSrcInterface(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.DstInterface.interval")) != 0)
//			add(new Scheme_AggregatorDstInterface(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.InterfaceMatrix.interval")) != 0)
//			add(new Scheme_AggregatorInterfaceMatrix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.SrcPrefix.interval")) != 0)
//			add(new Scheme_AggregatorSrcPrefix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.DstPrefix.interval")) != 0)
//			add(new Scheme_AggregatorDstPrefix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.PrefixMatrix.interval")) != 0)
//			add(new Scheme_AggregatorPrefixMatrix(sql, interval));
//
//		if ((interval = resources
//				.getInterval("flow.collector.Protocol.interval")) != 0)
//			add(new Scheme_AggregatorProtocol(sql, interval));

	}

	public void process(final FlowPacket packet) {
		if (save_raw_flow)
			packet.process_raw(sql);

		for (Iterator list = iterator(); list.hasNext();)
			((Scheme_Aggregator) list.next()).add(packet);
	}

}
