const hlffabric = require('fabric-network');
const cahlffabric = require('fabric-ca-client');
const fs = require('fs');

function loadMemoryWallet(node){

    try {

        return hlffabric.Wallets.newInMemoryWallet();

    } catch (error) {
        node.error(error);
    }
    
}

async function loadFileWallet(path, node){

    if(!path) node.error("The Path is not set!");

    try {
         return hlffabric.Wallets.newFileSystemWallet(path);
    } catch (error) {
        node.error(error);
    }
}

async function loadCCP(path, node){
    if(!path) node.error("The Path is not set!");

    try {
         const ccpJSON = (await fs.promises.readFile(path)).toString();
         return JSON.parse(ccpJSON);
    } catch (error) {
        node.error(error);
    }
}

async function createGateway(ccp, userid, wallet, chaincode, channel, node){

    try {

        const gateway = new hlffabric.Gateway();
        await gateway.connect(ccp, {identity: userid, wallet: wallet});
        const network = await gateway.getNetwork(channel);
        const cc = network.getContract(chaincode);
        return { gateway,
                 network,
                 cc     };

    } catch (error) {
        node.error(error);
    }
   
}

async function invokeEvaluateTransaction(cc, cmd, args, node){

    try {
        return await cc.evaluateTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }

}   

async function invokeSubmitTransactioncc(cc, cmd , args, node){
    try {
        return await cc.submitTransaction(cmd, ...args);
    } catch (error) {
        node.error(error);
    }
}

function createCAClient(ccp, caAddress, node){
    try {
        const caInfo = ccp.certificateAuthorities[caAddress];
        const caTLSCACerts = caInfo.tlsCACerts.pem;
        return new FabricCAServices(caInfo.url, {trustedRoots: caTLSCACerts, verify: false}, caInfo.caName);
    } catch (error) {
        node.error(error);
    }
}

//enroll User
async function enrollUser(caClient, wallet, enrollmentId, enrollmentSecret, mspId, node){

    const existingUser = await wallet.get(enrollmentId);

    if(existingUser){
        node.error("User already exists!")
        return;
    }

    const enrollment = await caClient.enroll({
        enrollmentID: enrollmentId,
        enrollmentSecret: enrollmentSecret 
    });

    const x509Identity = {
        credentials: {
            certificate: enrollment.certificate,
            privateKey: enrollment.key.toBytes(),
        },
        mspId: mspId,
        type: 'X.509'
    };

    await wallet.put(enrollmentId, x509Identity);

    node.log("User: "+ enrollmentId + " succesfully enrolled!");

}

//register User
async function registerUser(caClient, wallet, enrollmentId, role, node){

    const existingUser = await wallet.get(enrollmentId);
    const adminId = await wallet.get('admin');

    if(existingUser){
        node.error('Error User: ' + userid + ' already exists');
        return;
    }

    if(!'admin'){
        console.log('You are not the admin!')
        return;
    }

    const provider = wallet.getProviderRegistry().getProvider(adminId.type);
    const adminUser = await provider.getUserContext(adminId, 'admin');

    const secret = await caClient.register({
        enrollmentID: enrollmentId,
        role : role
    }, adminUser);

    return secret;
}

async function enrollAdminUser(caClient, wallet, userid, password, mspId, node){
    await enrollUser(caClient, wallet, userid, password, mspId, node);
}

// revoke User
function revokeUser(caClient, node){

}
    

module.exports = {loadCCP, loadFileWallet, loadMemoryWallet, createGateway, invokeEvaluateTransaction, invokeSubmitTransactioncc};