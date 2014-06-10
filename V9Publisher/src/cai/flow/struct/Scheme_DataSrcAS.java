package cai.flow.struct;
/**
 * 
 * @author CaiMao
 *
 */
public class Scheme_DataSrcAS extends Scheme_Data {
	public String Src_As;

	public Scheme_DataSrcAS(String RouterIP, long Flows, long Missed,
			long dPkts, long dOctets, long Src_As) {
		super(RouterIP, Flows, Missed, dPkts, dOctets);
		
		this.Src_As = DataAS.aggregate(Src_As, DataAS.AS_Source);
	}

}
