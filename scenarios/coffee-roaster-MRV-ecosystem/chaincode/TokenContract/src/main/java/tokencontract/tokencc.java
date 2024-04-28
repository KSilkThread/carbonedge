package tokencontract;


import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;


import tokencontract.models.IssuerOrganisations;
import tokencontract.models.ReportingOrganisations;




@Contract(
        name = "TokenContract",
        info = @Info(
                title = "TokenContract",
                description = "This contract issues and transfers tokens",
                version = "0.0.1-Alpha",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "janbiermann24@gmail.com",
                        name = "Jan")))


@Default
public class tokencc implements ContractInterface {

    final Logger logger = Logger.getLogger(tokencc.class);

    
    /** 
     * @param context
     * @return String
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String init(final Context context){
        return "init completed";

    }

    /** 
     * @param ctx
     * @param amount
     * @return String
     */
    //issueToken
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String issue(final Context ctx, final long amount){
        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();

        
        // check if the mspid is allowed to issue tokens
        if(!IssuerOrganisations.contains(mspid)){
            logger.error("The organisation " + mspid + " is not allowed to issue tokens");
            throw new ChaincodeException("You are not allowed to issue tokens!");
        }

        // check if the amount of tokens is above 0
        if(amount < 0){
            logger.error("The organisation " + mspid + " is not allowed to issue a negative amount of tokens");
            throw new ChaincodeException("You have to issue a positive number of tokens!");
        }

        
        // Do the actual issuing
        CompositeKey key = new CompositeKey(mspid);
        String balanceissuer = stub.getStringState(key.toString());
        if (balanceissuer == null || balanceissuer.isEmpty()) {
            balanceissuer = String.valueOf(0);
        }
        Long newbalance = Math.addExact(Long.parseLong(balanceissuer), amount);
        stub.putStringState(key.toString(), String.valueOf(newbalance));
        logger.info(mspid + " added "+ amount + " tokens to his account");

        return "You ("+ mspid + ") added "+ amount + " tokens";
    }

    
    /** 
     * @param ctx
     * @param tomspid
     * @param balance
     * @return String
     */
    //transferToken
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String transfer(final Context ctx, final String tomspid, final long amount){
        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();
        CompositeKey keySender = new CompositeKey(mspid);
        CompositeKey keyReceiver = new CompositeKey(tomspid);

        // checking if the amount is above 0
        if(amount < 0){
            logger.error("The organisation " + mspid + " is not allowed to transfer a negative amount of tokens");
            throw new ChaincodeException("You have to transfer a positive number of tokens!");
        }

        // checking if the sender has enough tokens to complete the transaction
        String currentbalancesender = stub.getStringState(keySender.toString());

        if (currentbalancesender == null || currentbalancesender.isEmpty()) {
            currentbalancesender = String.valueOf(0);
        }


        if (Long.parseLong(currentbalancesender) < amount) {
            logger.error("The organisation " + mspid + " does not have the required amount of tokens!");
            throw new ChaincodeException("You do not have enough tokens to complete the transfer ");
        }

        String currentbalancereceiver = stub.getStringState(keyReceiver.toString());

        if (currentbalancereceiver == null || currentbalancereceiver.isEmpty()) {
            currentbalancereceiver = String.valueOf(0);
        }

        long newbalancesender = Math.subtractExact(amount, Long.parseLong(currentbalancesender));
        long newbalancereceiver = Math.addExact(amount, Long.parseLong(currentbalancereceiver));

        stub.putStringState(keySender.toString(), String.valueOf(newbalancesender));
        stub.putStringState(keyReceiver.toString(), String.valueOf(newbalancereceiver));


        logger.info(mspid + " sent " + amount + " tokens to " + tomspid);
        return "Your transaction ("+ mspid + " to " + tomspid +" + " + amount +" tokens) was completed successfully!)";
    } 

    
    /** 
     * @param ctx
     * @param mspid
     * @return String
     */
    //getBalance
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getBalance(final Context ctx){
        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();
        CompositeKey key = new CompositeKey(mspid);
        String currentbalance = stub.getStringState(key.toString());

        if (currentbalance == null || currentbalance.isEmpty()) {
            currentbalance = String.valueOf(0);
        }

        return "Your current balance is " + currentbalance + " token(s)!";
    }

    
    /** 
     * @param ctx
     * @param amount
     * @return String
     */
    //removeToken
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String remove(final Context ctx, final long amount){
        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();
        CompositeKey key = new CompositeKey(mspid);
        String currentbalance = stub.getStringState(key.toString());

        if (currentbalance == null || currentbalance.isEmpty()) {
            throw new ChaincodeException("You can not remove tokens!");
        }

        if (Long.parseLong(currentbalance) < amount) {
            throw new ChaincodeException("You can not remove the amount of tokens!");
        }

        String newbalance = String.valueOf(Math.subtractExact(Long.parseLong(currentbalance), amount));
        stub.putStringState(key.toString(), newbalance);

        return "You removed " + amount + " token(s) from your account!";
    }

    
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getBalanceFrom(final Context ctx, final String frommspid){
        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();

        
        // check if the organisation is allowed to see someone elses balance
        if(!ReportingOrganisations.contains(mspid)){
            logger.error("The organisation " + mspid + " is not allowed to view the balance of " + frommspid);
            throw new ChaincodeException("You are not allowed to use this method!");
        }

        CompositeKey key = new CompositeKey(frommspid);
        String currentbalance = stub.getStringState(key.toString());

        if (currentbalance == null || currentbalance.isEmpty()) {
            currentbalance = String.valueOf(0);
        }

        return frommspid + " current balance is " + currentbalance + " token(s)!";

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String removeFrom(final Context ctx, final String frommspid, final Long amount){

        ChaincodeStub stub = ctx.getStub();
        String mspid = ctx.getClientIdentity().getMSPID();

        
        // check if the mspid is allowed to issue tokens
        if(!ReportingOrganisations.contains(mspid)){
            logger.error("The organisation " + mspid + " is not allowed remove tokens from other organisations");
            throw new ChaincodeException("You are not allowed to use this method!");
        }
        
        CompositeKey key = new CompositeKey(frommspid);
        String currentbalance = stub.getStringState(key.toString());

        if (currentbalance == null || currentbalance.isEmpty()) {
            throw new ChaincodeException("You can not remove tokens!");
        }

        if (Long.parseLong(currentbalance) < amount) {
            throw new ChaincodeException("You can not remove the amount of tokens!");
        }

        String newbalance = String.valueOf(Math.subtractExact(Long.parseLong(currentbalance), amount));
        stub.putStringState(key.toString(), newbalance);

        return "You removed " + amount + " token(s) from "+ frommspid + " account!";

    }
    
}
