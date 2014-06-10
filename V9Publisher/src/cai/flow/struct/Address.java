package cai.flow.struct;

import cai.flow.collector.interpretator.IpSegmentManager;
import cai.utils.Util;

public class Address {
	public long address;

	public Address(long address) {
		this.address = address;
	}
	
	public String toString() {
//		return Util.str_addr(address);
		return IpSegmentManager.getInstance().convertIP(address);
	}

	public boolean equals(Address o) {
		return address == o.address;
	}
}
