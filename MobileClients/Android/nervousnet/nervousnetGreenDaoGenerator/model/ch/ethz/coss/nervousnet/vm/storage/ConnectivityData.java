package ch.ethz.coss.nervousnet.vm.storage;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table CONNECTIVITY_DATA.
 */
public class ConnectivityData implements SensorDataImpl {

    private Long id;
    private Long TimeStamp;
    private Boolean isConnected;
    private Integer networkType;
    private Boolean isRoaming;
    private String wifiHashId;
    private Integer wifiStrength;
    private String mobileHashId;
    private long Volatility;
    private Boolean ShareFlag;

    // KEEP FIELDS - put your custom fields here
    private int type;
    // KEEP FIELDS END

    public ConnectivityData() {
    }

    public ConnectivityData(Long id) {
        this.id = id;
    }

    public ConnectivityData(Long id, Long TimeStamp, Boolean isConnected, Integer networkType, Boolean isRoaming, String wifiHashId, Integer wifiStrength, String mobileHashId, long Volatility, Boolean ShareFlag) {
        this.id = id;
        this.TimeStamp = TimeStamp;
        this.isConnected = isConnected;
        this.networkType = networkType;
        this.isRoaming = isRoaming;
        this.wifiHashId = wifiHashId;
        this.wifiStrength = wifiStrength;
        this.mobileHashId = mobileHashId;
        this.Volatility = Volatility;
        this.ShareFlag = ShareFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long TimeStamp) {
        this.TimeStamp = TimeStamp;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Integer getNetworkType() {
        return networkType;
    }

    public void setNetworkType(Integer networkType) {
        this.networkType = networkType;
    }

    public Boolean getIsRoaming() {
        return isRoaming;
    }

    public void setIsRoaming(Boolean isRoaming) {
        this.isRoaming = isRoaming;
    }

    public String getWifiHashId() {
        return wifiHashId;
    }

    public void setWifiHashId(String wifiHashId) {
        this.wifiHashId = wifiHashId;
    }

    public Integer getWifiStrength() {
        return wifiStrength;
    }

    public void setWifiStrength(Integer wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    public String getMobileHashId() {
        return mobileHashId;
    }

    public void setMobileHashId(String mobileHashId) {
        this.mobileHashId = mobileHashId;
    }

    public long getVolatility() {
        return Volatility;
    }

    public void setVolatility(long Volatility) {
        this.Volatility = Volatility;
    }

    public Boolean getShareFlag() {
        return ShareFlag;
    }

    public void setShareFlag(Boolean ShareFlag) {
        this.ShareFlag = ShareFlag;
    }

    // KEEP METHODS - put your custom methods here
	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public void setType(Integer type) {
		// TODO Auto-generated method stub
		this.type = type;
	}
    // KEEP METHODS END

}