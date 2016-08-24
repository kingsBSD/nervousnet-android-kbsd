package ch.ethz.coss.nervousnet.vm.storage;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ACCEL_DATA.
 */
public class AccelData implements SensorDataImpl {

    private Long id;
    private Long TimeStamp;
    private Float X;
    private Float Y;
    private Float Z;
    private long Volatility;
    private Boolean ShareFlag;

    // KEEP FIELDS - put your custom fields here
    private int type;
    // KEEP FIELDS END

    public AccelData() {
    }

    public AccelData(Long id) {
        this.id = id;
    }

    public AccelData(Long id, Long TimeStamp, Float X, Float Y, Float Z, long Volatility, Boolean ShareFlag) {
        this.id = id;
        this.TimeStamp = TimeStamp;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
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

    public Float getX() {
        return X;
    }

    public void setX(Float X) {
        this.X = X;
    }

    public Float getY() {
        return Y;
    }

    public void setY(Float Y) {
        this.Y = Y;
    }

    public Float getZ() {
        return Z;
    }

    public void setZ(Float Z) {
        this.Z = Z;
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
