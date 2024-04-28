package tokencontract.models;


public enum IssuerOrganisations {
    org0("org0-example-com");


    private final String mspid;


    IssuerOrganisations(final String mspid){
        this.mspid = mspid;

    }

    public String getMspid() {
        return mspid;
    }

    public static boolean contains(final String mspid){

        for (IssuerOrganisations o : IssuerOrganisations.values()) {
            if (mspid.equalsIgnoreCase(o.getMspid())) {
                return true;
            }
        }

        return false;
    }
    
}
