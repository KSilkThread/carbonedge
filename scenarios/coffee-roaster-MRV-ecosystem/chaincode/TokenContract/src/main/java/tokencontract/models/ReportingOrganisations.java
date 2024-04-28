package tokencontract.models;

public enum ReportingOrganisations {

    org0("org1-example-com"),
    org1("org0-example-com");



    private final String mspid;


    ReportingOrganisations(final String mspid){
        this.mspid = mspid;
    }

    public String getMspid() {
        return mspid;
    }

    public static boolean contains(final String mspid){

        for (ReportingOrganisations o : ReportingOrganisations.values()) {
            if (mspid.equalsIgnoreCase(o.getMspid())) {
                return true;
            }
        }

        return false;
    }
    
}
