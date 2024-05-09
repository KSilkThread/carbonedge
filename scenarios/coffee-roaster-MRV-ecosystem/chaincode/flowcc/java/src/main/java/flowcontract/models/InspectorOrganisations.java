package flowcontract.models;

public enum InspectorOrganisations {
    org0("org0-example-com");


    private final String mspid;


    InspectorOrganisations(final String mspid){
        this.mspid = mspid;

    }

    public String getMspid() {
        return mspid;
    }

    public static boolean contains(final String mspid){

        for (InspectorOrganisations o : InspectorOrganisations.values()) {
            if (mspid.equalsIgnoreCase(o.getMspid())) {
                return true;
            }
        }

        return false;
    }
    
}
