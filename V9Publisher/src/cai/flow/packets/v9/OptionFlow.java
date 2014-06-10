package cai.flow.packets.v9;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cai.sql.SQL;
import cai.utils.DoneException;
import cai.utils.Params;
import cai.utils.Util;

/*-------*-----------*----------------------------------------------------------*
 | Bytes | Contents  | Description                                              |
 *-------*-----------*----------------------------------------------------------*
 | 0-1   |tmeplateid | templateid==flowsetid                                    |
 *-------*-----------*----------------------------------------------------------*
 | 4-7   | length    | TLV                                                      |
 *-------*-----------*----------------------------------------------------------*/

public class OptionFlow {
	String routerIP;

	long[] values = new long[Template.MAX_TYPE];

	OptionTemplate myTemplate = null;

	public OptionFlow(String RouterIP, final byte[] buf, int off,
			OptionTemplate t) throws DoneException {
		this.routerIP = RouterIP;
		myTemplate = t;
		if (buf.length < t.getTypeOffset(-1)) {
			throw new DoneException("OptionTemplate"
					+ t.getOptionTemplateId() );
		}
		
		int currOffset = 0, currLen = 0;
		for (int idx = 0; idx < Template.MAX_TYPE; idx++) {
			if (t.getTypeLen(idx) > 0) {// this is in the template
				currOffset = t.getTypeOffset(idx);
				currLen = t.getTypeLen(idx);
				if (currOffset >= 0 && currLen > 0) {
					values[idx] = Util
							.to_number(buf, off + currOffset, currLen);
				}
			}
		}
	}

	public void save_raw(long SysUptime, long unix_secs, long packageSequence,
			long sourceId, PreparedStatement add_raw_stm) {
		try {
			for (int typeName = 0; typeName < Template.MAX_TYPE; typeName++) {
				if (myTemplate.getTypeLen(typeName) > 0) {
					add_raw_stm.setString(1, routerIP);
					add_raw_stm.setLong(2, SysUptime);
					add_raw_stm.setLong(3, unix_secs);
					add_raw_stm.setLong(4, packageSequence);
					add_raw_stm.setLong(5, sourceId);
					add_raw_stm.setString(6, myTemplate.isScopeType(typeName)?"T":"F");
					add_raw_stm.setInt(7,myTemplate.getOptionTemplateId());
					add_raw_stm.setInt(8,typeName);
					add_raw_stm.setLong(9,values[typeName]);
					add_raw_stm.setString(10, Params.getCurrentTime());
					add_raw_stm.executeUpdate();
				}
			}
		} catch (SQLException e) {
			SQL.error_msg("INSERT to V9 raw table", e, null);
		}
	}
}
